package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
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
    private Polygon graph;
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
    private SimpleDoubleProperty rec2DwidthProperty;
    private SimpleDoubleProperty rec2DheightProperty;


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty, ReadOnlyDoubleProperty position) {

        this.elevationProfileProperty = elevationProfileProperty;
        this.position = position;
        this.mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);
        this.borderPane = new BorderPane();
        rec2DwidthProperty = new SimpleDoubleProperty();
        rec2DheightProperty = new SimpleDoubleProperty();

        this.captionText = new Text();
        this.graph = new Polygon();

        this.captionContainer = new VBox(captionText);
        this.pane = new Pane(graph);

        borderPane.getChildren().add(captionContainer);
        borderPane.getChildren().add(pane);


        this.rectangle2DProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        this.minHeight = elevationProfileProperty.get().minElevation();
        this.maxHeight = elevationProfileProperty.get().maxElevation();

        initializeTransform();







        borderPane.setOnMouseMoved( e -> {
            double distance = elevationProfileProperty.get().length();
            this.mousePositionOnProfileProperty = new SimpleDoubleProperty( (int)((distance / pane.getWidth()) * e.getX()));
        });
        ChangeListener<Number> updateOnResize = (o, ov, nv) -> updateDisplay();
        borderPane.heightProperty().addListener(updateOnResize);
        borderPane.widthProperty().addListener(updateOnResize);
    }

    public Pane pane(){
        return this.borderPane;
    }


    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        return this.mousePositionOnProfileProperty;
    }

    private void initializeTransform() {
        Affine screenToWorld = new Affine();
        screenToWorld.prependTranslation(-insets.getLeft(), -(rec2DheightProperty.get() + insets.getTop()));
        screenToWorld.prependScale(elevationProfileProperty.get().length() / rec2DwidthProperty.get(), -(maxHeight - minHeight) / rec2DheightProperty.get());
        screenToWorld.prependTranslation(0, minHeight);


        try {
            Affine worldToScreen = screenToWorld.createInverse();
            this.screenToWorldProperty = new SimpleObjectProperty<>(screenToWorld);
            this.worldToScreenProperty = new SimpleObjectProperty<>(worldToScreen);

        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }

    }

    private void updateDisplay() {
        this.rec2DwidthProperty.set( borderPane.getWidth() - insets.getRight() - insets.getLeft());
        this.rec2DheightProperty.set( borderPane.getHeight() - insets.getBottom() - insets.getTop());
        System.out.println(rec2DwidthProperty.get());
        System.out.println(rec2DheightProperty.get());
        this.rectangle2DProperty.set(new Rectangle2D(
                insets.getRight(),
                insets.getTop(),
                rec2DwidthProperty.get(),
                rec2DheightProperty.get())
        );


        List<Double> polygonPoints = new ArrayList<>();
        for (int i = 0; i <= borderPane.getWidth(); i++) {
            polygonPoints.add((double)i); //ligne du haut
            polygonPoints.add(0d);
        }
        polygonPoints.add(borderPane.getWidth()); //coordonnées en bas à droite
        polygonPoints.add(borderPane.getHeight());
        polygonPoints.add(0d);                    //coordonnées en bas à gauche
        polygonPoints.add(borderPane.getWidth());
        graph.getPoints().setAll(polygonPoints);


    }

}
