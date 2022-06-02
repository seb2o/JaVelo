package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edgar Gonzalez (328095)
 * @author Sébastien Boo (345870)
 */
public final class ElevationProfileManager {

    //informations à afficher -exterieur-
    private ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private ReadOnlyDoubleProperty highlightedPosition;

    //hierarchie javafx
    //conteneur principal
    private BorderPane borderPane;

    //legende
    private VBox vBox;
    private Text vBoxText;

    //profil : graphe, grille et étiquettes
    private Pane pane;
    private Path grid;
    private Polygon polygon;
    private Line line;
    private Group gridLabels;
    //fin hierarchie javafx


    //informations internes

    //highlight
    private DoubleProperty mousePositionOnProfileProperty;

    //bordures
    private final Insets insets = new Insets(10, 10, 20, 40);
    private ObjectProperty<Rectangle2D> rectangle2DProperty;

    //conversion coordonées graph - itinéraire
    private SimpleObjectProperty<Transform> screenToWorldProperty;
    private SimpleObjectProperty<Transform> worldToScreenProperty;

    //grille
    int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
    private int posStep;
    private int eleStep;
    private static final int VERTICAL_STEP_TRESHOLD = 50;
    private final static int HORIZONTAL_STEP_TRESHOLD = 25;
    private static final double METERS_TO_KILOMETERS = 1d/1000d;
    private static final int FONT_SIZE_AVENIR = 10;

    //infos profil souvent accédées
    private double minElevation;
    private double maxElevation;
    private double routeLength;



    public ElevationProfileManager(
        ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
        ReadOnlyDoubleProperty highlightedPosition) {

        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightedPosition = highlightedPosition;
        mousePositionOnProfileProperty = new SimpleDoubleProperty(highlightedPosition.doubleValue());
        screenToWorldProperty = new SimpleObjectProperty<>();
        worldToScreenProperty = new SimpleObjectProperty<>();
        rectangle2DProperty = new SimpleObjectProperty<>();

        vBox = new VBox();
        vBoxText = new Text();

        pane = new Pane();
        grid = new Path();
        gridLabels = new Group();
        polygon = new Polygon();
        line = new Line();

        borderPane = new BorderPane(pane,null,null, vBox,null);

        pane.getChildren().addAll(grid,gridLabels,polygon,line);
        vBox.getChildren().add(vBoxText);
        borderPane.getStylesheets().add("elevation_profile.css");
        vBox.setId("profile_data");
        grid.setId("grid");
        polygon.setId("profile");
        gridLabels.getStyleClass().setAll("grid_label","horizontal","vertical");


        createListeners();
        bindRectangle();
        bindTransform();
        bindLine();

    }

    /**
     * getter pour le pane contenant le dessin du profil et les informations associées
     * @return le pane, jamais null
     */
    public Pane pane(){
        return this.borderPane;
    }

    /**
     * getter pour la propriété contenant la position de la souris sur le profil
     * en fonction de la route et pas de l'écran
     * @return la propriété contenant cette valeure, valeure qui peut être nulle
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        return this.mousePositionOnProfileProperty;
    }

    /**
     * méthode interne permettant de lier la conversion des coordonnées réelles
     * en coordonnées sur le panneau javafx et son inverse à la taille du rectangle
     * contenant le profil et au profil en lui même
     */
    private void bindTransform() {
        this.screenToWorldProperty.bind(Bindings.createObjectBinding(
                () -> {
                    Affine screenToWorld = new Affine();
                    double rec2Dheight = rectangle2DProperty.get().getHeight();
                    double rec2Dwidth = rectangle2DProperty.get().getWidth();

                    screenToWorld.prependTranslation(
                            -insets.getLeft(),
                            -rec2Dheight - insets.getTop());
                    screenToWorld.prependScale(
                            routeLength / rec2Dwidth,
                            -(maxElevation - minElevation) / rec2Dheight);
                    screenToWorld.prependTranslation(
                            0,
                            minElevation);
                    return screenToWorld;
                },
                rectangle2DProperty,elevationProfileProperty));

        this.worldToScreenProperty.bind(Bindings.createObjectBinding( () ->
               screenToWorldProperty.get() == null ?
                       null :
                       screenToWorldProperty.get().createInverse(), screenToWorldProperty));
    }

    /**
     * méthode interne permettant de lier le rectangle dans lequel est dessiné le profil
     * à la taille du panneau
     */
    private void bindRectangle() {
        this.rectangle2DProperty.bind(Bindings.createObjectBinding(
                () -> {
                    double rec2Dwidth =  pane.getWidth()
                            - insets.getRight()
                            - insets.getLeft() ;
                    double rec2Dheight =  pane.getHeight()
                            - insets.getBottom()
                            - insets.getTop();
                    return new Rectangle2D(
                            insets.getLeft(),
                            insets.getTop(),
                            rec2Dwidth > 0 ?
                                    rec2Dwidth : 0,
                            rec2Dheight > 0 ?
                                    rec2Dheight : 0);
                },
                pane.widthProperty(),
                pane.heightProperty()));
    }

    /**
     * méthode interne permettant de lier la ligne d'affichage de la position de
     * la souris sur le profil au rectangle et a la position de la souris
     */
    private void bindLine() {
        line.layoutXProperty().bind(
                Bindings.createDoubleBinding(
                        () -> worldToScreenProperty.get().transform(
                                                highlightedPosition.doubleValue(),
                                                rectangle2DProperty.get().getMinY()).getX(),
                        highlightedPosition,worldToScreenProperty));
        line.startYProperty().bind(
                Bindings.createDoubleBinding(() -> rectangle2DProperty.get().getMinY(),
                        rectangle2DProperty)
        );
        line.endYProperty().bind(
                Bindings.createDoubleBinding(() -> rectangle2DProperty.get().getMaxY(),
                        rectangle2DProperty)
        );
    }

    /**
     * méthode intenre permmettant de mettre à jour le dessin du polygone; appellée
     * lorsque le panneau est redimensionné ou losrque le profil change et n'est pas null
     */
    private void updatePolygon() {

        polygon.getPoints().removeAll(polygon.getPoints());

        List<Double> graphPoints = new ArrayList<>();
        ElevationProfile profile = elevationProfileProperty.get();
        double wStep = routeLength/rectangle2DProperty.get().getWidth();

        for (double i = rectangle2DProperty.get().getMinX();
             i < pane.getWidth()-insets.getRight();
             i++) {
            double wX = (i-insets.getLeft())*wStep;
            graphPoints.add(i);
            graphPoints.add(worldToScreenProperty.get()
                    .transform(wX,profile.elevationAt(wX))
                    .getY());
        }

        polygon.getPoints().setAll(graphPoints);
        polygon.getPoints().addAll(
                pane.getWidth()
                        -insets.getRight(),
                worldToScreenProperty.get()
                        .transform(
                                routeLength,
                                profile.elevationAt(routeLength))
                        .getY());
        polygon.getPoints().addAll(
                pane.getWidth()
                        -insets.getRight(),
                pane.getHeight()
                        -insets.getBottom());
        polygon.getPoints().addAll(
                insets.getLeft(),
                pane.getHeight()
                        -insets.getBottom());

    }

    /**
     * méthode interne ajoutant les listeners nécéssaires à
     * mettre à jour la position de la souris sur le profil
     * mettre à jour la grille et le dessin du polygone lorsque
     * le profil change et n'est pas null ou que le panneau est redimmensionné
     *
     */
    private void createListeners() {



        pane.setOnMouseMoved( e -> mousePositionOnProfileProperty.set(
                rectangle2DProperty.get().contains(e.getX(),e.getY()) ?
                (e.getX()-insets.getLeft())
                        *routeLength
                        /rectangle2DProperty.get().getWidth() :
                Double.NaN));

        pane.setOnMouseExited(e ->
                mousePositionOnProfileProperty.set(Double.NaN));

        elevationProfileProperty.addListener(((observable, oldValue, newValue) -> {
            ElevationProfile p = elevationProfileProperty.get();
            if(p != null){
                minElevation = p.minElevation();
                maxElevation = p.maxElevation();
                routeLength = p.length();
                updatePolygon();
                updateGrid();
            }

        }));

        rectangle2DProperty.addListener((o,oV,nV) -> {
            if(screenToWorldProperty.get() != null){
                updateGrid();
                updatePolygon();
            }
        });

    }

    /**
     * méthode interne permettant le redessin de la grille et des informations associées
     * appellée par les listeners susmentionnées
     */
    private void updateGrid() {
        updateSteps();
        grid.getElements().removeAll(grid.getElements());
        gridLabels.getChildren().removeAll(gridLabels.getChildren());
        double posStepS =  (posStep*rectangle2DProperty.get().getWidth()/routeLength);
        double eleStepS =  (eleStep*rectangle2DProperty.get().getHeight()/(maxElevation-minElevation));

        if(posStepS <=0.0 || eleStepS <= 0.0) return;

        int index = 0;
        for (double i = rectangle2DProperty.get().getMinX(); i <= rectangle2DProperty.get().getMaxX(); i+=posStepS) {
            grid.getElements().addAll(new MoveTo(i,rectangle2DProperty.get().getMinY()),new LineTo(i,rectangle2DProperty.get().getMaxY()));
            posLabel(i,index++);

        }
        index = (int) Math.ceil(minElevation/eleStep);
        double firstHorizontalLineY = worldToScreenProperty.get().transform(0,eleStep*index).getY();
        for (double i = firstHorizontalLineY; i >= rectangle2DProperty.get().getMinY(); i-=eleStepS) {
            grid.getElements().addAll(new MoveTo(rectangle2DProperty.get().getMinX(),i),new LineTo(rectangle2DProperty.get().getMaxX(),i));
            eleLabel(index++);
        }

        this.vBoxText.setText(String.format("Longueur : %.1f km" +
                "     Montée : %.0f m" +
                "     Descente : %.0f m" +
                "     Altitude : de %.0f m à %.0f m",
                (int)routeLength*METERS_TO_KILOMETERS,
                elevationProfileProperty.get().totalAscent(),
                elevationProfileProperty.get().totalDescent(),
                minElevation,
                maxElevation));

    }

    /**
     * méthode interne permettant de calculer les valeurs d'écartement des lignes de la grille
     */
    private void updateSteps() {

            double screenLength = rectangle2DProperty.get().getWidth();
            int posIndex = 0;
            int nOfVertical = (int) (routeLength/POS_STEPS[posIndex]);
            while (screenLength / nOfVertical < VERTICAL_STEP_TRESHOLD && posIndex < POS_STEPS.length - 1) {
                posIndex++;
                nOfVertical = (int) (routeLength/POS_STEPS[posIndex]);
            }
            posStep = POS_STEPS[posIndex];



            double heightS = rectangle2DProperty.get().getHeight();
            double heightW = maxElevation - minElevation;
            int eleIndex = 0;
            int nOfHorizontal = (int) (heightW/ELE_STEPS[eleIndex]);

            while (heightS / nOfHorizontal < HORIZONTAL_STEP_TRESHOLD && eleIndex < ELE_STEPS.length - 1) {
                eleIndex++;
                nOfHorizontal = (int) (heightW/ELE_STEPS[eleIndex]);
            }
            eleStep = ELE_STEPS[eleIndex];
    }

    /**
     * méthode interne permettant l'ajout d'une étiquette de hauteur dans le dessin de la grille
     * @param index index de la ligne dessinée, le 0 correspondant à la ligne d'altitude minimum
     */
    private void eleLabel(int index) {
        Text t = new Text(String.valueOf(eleStep*index));
        t.textOriginProperty().set(VPos.CENTER);
        t.setX(rectangle2DProperty.get().getMinX() - (t.prefWidth(0) + 2));
        t.setY(worldToScreenProperty.get().transform(0,eleStep*index).getY());

        t.setFont(Font.font("Avenir", FONT_SIZE_AVENIR));
        t.getStyleClass().addAll("grid_label","vertical");
        gridLabels.getChildren().add(t);


    }

    /**
     * méthode interne permettant l'ajout d'une étiquette de position dans le dessin de la grille
     * @param posS la position sur l'écran à laquelle rajouter l'étiquette
     * @param index index de la ligne dessinée, le 0 correspondant à la position 0
     */
    private void posLabel(double posS, int index) {
        Text t = new Text(String.valueOf((int)(posStep*index*METERS_TO_KILOMETERS)));
        t.textOriginProperty().set(VPos.TOP);
        t.setX(posS - t.prefWidth(0)/2);
        t.setY(pane.getHeight()-insets.getBottom());

        t.setFont(Font.font("Avenir", FONT_SIZE_AVENIR));
        t.getStyleClass().addAll("grid_label","horizontal");
        gridLabels.getChildren().add(t);




    }



}
