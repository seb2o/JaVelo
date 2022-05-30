package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

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
        setStyles();
        createListeners();
        bindRectangle();
        bindPolygon();

    }

    public Pane pane(){
        return this.borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        return this.mousePositionOnProfileProperty;
    }

    private void bindPolygon() {
        ChangeListener<Object> updatePolygon = (o, ov, nv) -> {
            if(elevationProfileProperty.get() != null) updatePolygon();
        };
        pane.heightProperty().addListener(updatePolygon);
        pane.widthProperty().addListener(updatePolygon);
        elevationProfileProperty.addListener(updatePolygon);

    }

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
                rectangle2DProperty));

        this.worldToScreenProperty.bind(Bindings.createObjectBinding( () ->
               screenToWorldProperty.get() == null ?
                       null :
                       screenToWorldProperty.get().createInverse(), screenToWorldProperty));
    }

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

    private void bindLine() {
        line.layoutXProperty().bind(
                Bindings.createDoubleBinding(
                        () -> worldToScreenProperty.get().transform(
                                                highlightedPosition.doubleValue(),
                                                rectangle2DProperty.get().getMinY()).getX(),
                        highlightedPosition));
        line.startYProperty().bind(
                Bindings.createDoubleBinding(() -> rectangle2DProperty.get().getMinY(),
                        rectangle2DProperty)
        );
        line.endYProperty().bind(
                Bindings.createDoubleBinding(() -> rectangle2DProperty.get().getMaxY(),
                        rectangle2DProperty)
        );
    }

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

    private void createListeners() {

        pane.setOnMouseEntered( e-> {
            System.out.println(gridLabels.getChildren());
        });

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
                bindTransform();
                bindLine();
                updatePolygon();
                updateSteps();
                updateGrid();
            }

        }));
        rectangle2DProperty.addListener((a,b,c) -> {
            if(elevationProfileProperty.get() != null){
                updateSteps();
                updateGrid();
            }
        });

    }

    private void updateGrid() {
        grid.getElements().removeAll(grid.getElements());
        gridLabels.getChildren().removeAll(gridLabels.getChildren());
        double posStepS = posStep*rectangle2DProperty.get().getWidth()/routeLength;
        double eleStepS = eleStep*rectangle2DProperty.get().getHeight()/(maxElevation-minElevation);

        if(posStepS <=0.0 || eleStepS <= 0.0) return;

        for (double i = rectangle2DProperty.get().getMinX(); i <= rectangle2DProperty.get().getMaxX()+1; i+=posStepS) {
            grid.getElements().addAll(new MoveTo(i,rectangle2DProperty.get().getMinY()),new LineTo(i,rectangle2DProperty.get().getMaxY()));
            posLabel(i);
        }
        double firstHorizontalLineY = rectangle2DProperty.get().getMaxY()-rectangle2DProperty.get().getMaxY()%eleStepS;
        for (double i = firstHorizontalLineY; i >= rectangle2DProperty.get().getMinY(); i-=eleStepS) {
            grid.getElements().addAll(new MoveTo(rectangle2DProperty.get().getMinX(),i),new LineTo(rectangle2DProperty.get().getMaxX(),i));
            eleLabel(i);
        }
    }

    private void setStyles() {
        borderPane.getStylesheets().add("elevation_profile.css");
        vBox.setId("profile_data");
        grid.setId("grid");
        polygon.setId("profile");
        gridLabels.getStyleClass().setAll("grid_label","horizontal","vertical");
    }

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

    private void eleLabel(double eleS) {
        System.out.println((int) screenToWorldProperty.get().transform(0, eleS).getY());
        Text t = new Text(String.valueOf(
                (int)screenToWorldProperty.get().transform(0,eleS).getY()));
        t.textOriginProperty().set(VPos.CENTER);
        t.setX(t.prefWidth(0)-2);
        t.setY(eleS);

        t.setFont(Font.font("Avenir", FONT_SIZE_AVENIR));
        t.getStyleClass().addAll("grid_label","vertical");
        gridLabels.getChildren().add(t);


    }
    private void posLabel(double pos) {
        Text t = new Text(String.valueOf(
                (int)(screenToWorldProperty.get().transform(pos,0).getX()*METERS_TO_KILOMETERS)));
        t.textOriginProperty().set(VPos.TOP);
        t.setX(pos - t.prefWidth(0)/2);
        t.setY(pane.getHeight()-insets.getBottom());

        t.setFont(Font.font("Avenir", FONT_SIZE_AVENIR));
        t.getStyleClass().addAll("grid_label","horizontal");
        gridLabels.getChildren().add(t);




    }



}
