package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

public final class ElevationProfileManager {

    //informations à afficher -exterieur-
    private ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private ReadOnlyDoubleProperty highlightedPosition;

    //hierarchie javafx//
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
    //fin hierarchie javafx//


    //informations internes

    //highlight
    private DoubleProperty mousePositionOnProfileProperty;
    private SimpleDoubleProperty mouseOnPaneProperty;

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
    private final static int HORIZONTAL_SPACING = 25;
    private static final int VERTICAL_SPACING = 50;





    public ElevationProfileManager(
            ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
            ReadOnlyDoubleProperty highlightedPosition) {

        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightedPosition = highlightedPosition;
        mousePositionOnProfileProperty = new SimpleDoubleProperty(highlightedPosition.doubleValue());
        screenToWorldProperty = new SimpleObjectProperty<>();
        worldToScreenProperty = new SimpleObjectProperty<>();
        rectangle2DProperty = new SimpleObjectProperty<>();
        mouseOnPaneProperty = new SimpleDoubleProperty(Double.NaN);

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



        //todo a virer
        this.vBox.setBackground(Background.fill(Color.RED));
        this.pane.setBackground(Background.fill(Color.BLUE));


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
                    double minHeight = elevationProfileProperty.get().minElevation();
                    double maxHeight = elevationProfileProperty.get().maxElevation();
                    double rec2Dheight = rectangle2DProperty.get().getHeight();
                    double rec2Dwidth = rectangle2DProperty.get().getWidth();

                    screenToWorld.prependTranslation(
                            -insets.getLeft(),
                            -rec2Dheight - insets.getTop());
                    screenToWorld.prependScale(
                            elevationProfileProperty.get().length() / rec2Dwidth,
                            -(maxHeight - minHeight) / rec2Dheight);
                    screenToWorld.prependTranslation(
                            0,
                            minHeight);
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
                        mousePositionOnProfileProperty));
        line.startYProperty().bind(
                Bindings.createDoubleBinding(() -> rectangle2DProperty.get().getMinY(),
                        rectangle2DProperty)
        );
        line.endYProperty().bind(
                Bindings.createDoubleBinding(() -> rectangle2DProperty.get().getMaxY(),
                        rectangle2DProperty)
        );
        line.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                rectangle2DProperty.get().contains(
                        mousePositionOnProfileProperty.doubleValue(),
                        rectangle2DProperty.get().getMinY()),
                rectangle2DProperty,mousePositionOnProfileProperty));
    }

    private void updatePolygon() {//todo ptetre probleme de la dernière arête pas parfaitement verticale

        polygon.getPoints().removeAll(polygon.getPoints());

        List<Double> graphPoints = new ArrayList<>();
        ElevationProfile profile = elevationProfileProperty.get();
        double wStep = profile.length()/rectangle2DProperty.get().getWidth();

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
                                profile.length(),
                                profile.elevationAt(profile.length()))
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
        pane.setOnMouseMoved( e -> {
            });
        pane.setOnMouseExited(e ->{

        });

        elevationProfileProperty.addListener(((observable, oldValue, newValue) -> {
            System.out.println("updating profile");
            if(elevationProfileProperty.get() != null){
                bindRectangle();
                bindTransform();
                bindLine();
                updatePolygon();
            }

        }));
    }

    private void setStyles() {
        borderPane.getStylesheets().add("elevation_profile.css");
        vBox.setId("profile_data");
        grid.setId("grid");
        polygon.setId("profile");
        gridLabels.getStyleClass().setAll("grid_label","horizontal","vertical");
    }

    private void gridSpacing() {
        double length = elevationProfileProperty.get().length();
        double minW = elevationProfileProperty.get().minElevation();
        double maxW = elevationProfileProperty.get().maxElevation();
        double heightW = maxW - minW;
        double heightS = rectangle2DProperty.get().getHeight();
        double widthS = rectangle2DProperty.get().getWidth();
        int eleIndex = ELE_STEPS.length-1;
        int posIndex = POS_STEPS.length-1;
        double nOfhorizontals = heightW/ELE_STEPS[eleIndex];
        double nOfVertical = length/POS_STEPS[posIndex];
        while (heightS / nOfhorizontals > HORIZONTAL_SPACING ) {
            eleIndex--;
            nOfhorizontals = heightW/ELE_STEPS[eleIndex];
        }
        while (widthS / nOfVertical > VERTICAL_SPACING ) {
            posIndex--;
            nOfVertical = length/POS_STEPS[posIndex];
        }




    }



}
