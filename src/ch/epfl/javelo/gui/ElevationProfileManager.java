package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
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
    private ReadOnlyDoubleProperty position;

    //hierarchie javafx//

    //conteneur principal
    private BorderPane borderPane;

    //legende
    private VBox captionContainer;
    private Text captionText;

    //profil : graphe, grille et étiquettes
    private Pane pane;
    private Path grid;
    private Polygon polygon;
    private Line highlighted;
    private Group gridLabels;

    //fin hierarchie javafx//


    //informations internes

    //a communiquer a l'exterieur
    private ReadOnlyDoubleProperty mousePositionOnProfileProperty;

    //bordures
    private final Insets insets = new Insets(10, 10, 20, 40);
    private ObjectProperty<Rectangle2D> rectangle2DProperty;

    //conversion coordonées graph - itinéraire
    private SimpleObjectProperty<Transform> screenToWorldProperty;
    private SimpleObjectProperty<Transform> worldToScreenProperty;



    private double minHeight;
    private double maxHeight;
    private double rec2Dwidth;
    private double rec2Dheight;


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty, ReadOnlyDoubleProperty position) {

        this.elevationProfileProperty = elevationProfileProperty;
        this.position = position;
        this.mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);
        this.screenToWorldProperty = new SimpleObjectProperty<>();
        this.worldToScreenProperty = new SimpleObjectProperty<>();
        this.borderPane = new BorderPane();
;

        this.captionText = new Text();
        this.polygon = new Polygon();

        this.captionContainer = new VBox(captionText);
        this.pane = new Pane();
        pane.getChildren().add(polygon);
        polygon.setId("profile");
        captionContainer.setId("profile_data");
        this.captionContainer.setBackground(Background.fill(Color.RED));
        this.pane.setBackground(Background.fill(Color.BLUE));
        borderPane = new BorderPane(pane,null,null,captionContainer,null);

        this.rectangle2DProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        this.minHeight = elevationProfileProperty.get().minElevation();
        this.maxHeight = elevationProfileProperty.get().maxElevation();
        borderPane.getStylesheets().add("elevation_profile.css");

        pane.setOnMouseMoved( e -> {
            double distance = elevationProfileProperty.get().length();
            this.mousePositionOnProfileProperty = new SimpleDoubleProperty( (int)((distance / pane.getWidth()) * e.getX()));
        });


        ChangeListener<Number> updateOnResize = (o, ov, nv) -> {
            updatePolygon();
        };
        pane.heightProperty().addListener(updateOnResize);
        pane.widthProperty().addListener(updateOnResize);

        bindTransform();
        bindRectangle();

    }



    public Pane pane(){
        return this.borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        return this.mousePositionOnProfileProperty;
    }

    private void bindTransform() {
        this.screenToWorldProperty.bind(Bindings.createObjectBinding( () -> {
            Affine screenToWorld = new Affine();
            screenToWorld.prependTranslation(-insets.getLeft(), -rec2Dheight - insets.getTop());
            screenToWorld.prependScale(elevationProfileProperty.get().length() / rec2Dwidth, -(maxHeight - minHeight) / rec2Dheight);
            screenToWorld.prependTranslation(0, minHeight);
            return screenToWorld;
        },rectangle2DProperty));

        this.worldToScreenProperty.bind(Bindings.createObjectBinding( () -> screenToWorldProperty.get().createInverse(), screenToWorldProperty));
    }

    private void bindRectangle() {
        this.rectangle2DProperty.bind(Bindings.createObjectBinding(
                () -> {
                    this.rec2Dwidth =  pane.getWidth() - insets.getRight() - insets.getLeft() ;
                    this.rec2Dheight =  pane.getHeight() - insets.getBottom() - insets.getTop();
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

    private void updatePolygon() {//todo ptetre probleme de la dernière arête pas parfaitement verticale
        polygon.getPoints().removeAll(polygon.getPoints());
        List<Double> graphPoints = new ArrayList<>();
        ElevationProfile profile = elevationProfileProperty.get();
        double wStep = profile.length()/rectangle2DProperty.get().getWidth();
        for (double i = rectangle2DProperty.get().getMinX(); i < pane.getWidth()-insets.getRight()  ; i++) {
            double wX = (i-insets.getLeft())*wStep;
            graphPoints.add(i);
            graphPoints.add(worldToScreenProperty.get().transform(wX,profile.elevationAt(wX)).getY());
        }

        polygon.getPoints().setAll(graphPoints);
        polygon.getPoints().addAll(pane.getWidth()-insets.getRight(), worldToScreenProperty.get().transform(profile.length(),profile.elevationAt(profile.length())).getY());
        polygon.getPoints().addAll(pane.getWidth()-insets.getRight(),pane.getHeight()-insets.getBottom());
        polygon.getPoints().addAll(insets.getLeft(),pane.getHeight()-insets.getBottom());

    }

}
