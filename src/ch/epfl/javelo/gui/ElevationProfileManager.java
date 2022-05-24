package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

public final class ElevationProfileManager {
    private ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private ReadOnlyDoubleProperty position;
    private BorderPane borderPane;
    private Pane pane;
    private Insets insets = new Insets(10, 10, 20, 40);
    private ReadOnlyDoubleProperty mousePositionOnProfileProperty;
    private SimpleObjectProperty<Transform> screenToWorldProperty;
    private SimpleObjectProperty<Transform> worldToScreenProperty;
    private double minHeight;
    private double maxHeight;
    private double rec2Dwidth;
    private double rec2Dheight;
    private ObjectProperty<Rectangle2D> rectangle2DProperty;


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty, ReadOnlyDoubleProperty position)
            throws NonInvertibleTransformException {
        //Todo : catch au lieu du thorws ?

        this.elevationProfileProperty = elevationProfileProperty;
        this.position = position;
        this.borderPane = new BorderPane();
        this.pane = new Pane();
        borderPane.getChildren().add(pane);
        this.rec2Dwidth = 600 - insets.getRight() - insets.getLeft();
        this.rec2Dheight = 300 - insets.getBottom() - insets.getTop();
        this.rectangle2DProperty = new SimpleObjectProperty<>(new Rectangle2D(insets.getRight(), insets.getTop(),rec2Dwidth, rec2Dheight));
        this.minHeight = elevationProfileProperty.get().minElevation();
        this.maxHeight = elevationProfileProperty.get().maxElevation();
        /////////////////////////////////////////////
        ///////////// DEBUT TRANSFORM ///////////////
        /////////////////////////////////////////////
        Affine screenToWorld = new Affine();
        screenToWorld.prependTranslation( -insets.getLeft(), -(rec2Dheight + insets.getTop()) );
        screenToWorld.prependScale( elevationProfileProperty.get().length() / rec2Dwidth, - (maxHeight - minHeight) / rec2Dheight);
        //Todo : peut etre enlever le "-" dans le dernier terme
        // screenToWorld.prependTranslation(0, minElevation); à rajouter askip

        Affine worldToScreen = screenToWorld.createInverse();

        this.screenToWorldProperty = new SimpleObjectProperty<>(screenToWorld);
        this.worldToScreenProperty = new SimpleObjectProperty<>(worldToScreen);
        /////////////////////////////////////////////
        /////////////// FIN TRANSFORM /////////////// //Todo : faire une méthode "initializeTransforms()" à la place ?
        /////////////////////////////////////////////


        /////////////////////////////////////////////
        ///// DÉBUT DE CONSTRUCTION DU POLYGONE /////
        /////////////////////////////////////////////
        Polygon polygon = new Polygon();

        List<Double> polygonPoints = new ArrayList<>();
        for (int i = 0; i <= borderPane.getWidth(); i++) {
            polygonPoints.add((double)i); //ligne du haut
            polygonPoints.add(0d);
        }
        polygonPoints.add(borderPane.getWidth()); //coordonnées en bas à droite
        polygonPoints.add(borderPane.getHeight());
        polygonPoints.add(0d);                    //coordonnées en bas à gauche
        polygonPoints.add(borderPane.getWidth());

        polygon.getPoints().addAll(polygonPoints);
        pane.getChildren().add(polygon);
        /////////////////////////////////////////////
        ////// FIN DE CONSTRUCTION DU POLYGONE //////
        /////////////////////////////////////////////


        this.mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);

        borderPane.setOnMouseDragOver((mouseDragEvent) ->{
            double distance = elevationProfileProperty.get().length();
            this.mousePositionOnProfileProperty = new SimpleDoubleProperty( (int)((distance / pane.getWidth()) * mouseDragEvent.getX()));
            //Todo : arrondi comme j'ai fait avec transtypage ?
        });

    }

    public Pane pane(){
        System.out.println(borderPane.getWidth()+ " " + borderPane.getHeight());
        return this.borderPane;
    }


    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        return this.mousePositionOnProfileProperty;
    }
}
