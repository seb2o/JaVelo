package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.beans.EventHandler;
import java.io.IOException;
import java.util.ArrayList;


public final class BaseMapManager {

    private boolean redrawNeeded = true;
    private Pane pane;
    private Canvas canvas;
    private TileManager tileManager;
    private WaypointsManager waypointsManager;
    private ObjectProperty<MapViewParameters> mapViewParameters;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters>  mapViewParameters){
        this.canvas = new Canvas();
        this.pane = new Pane();
        this.pane.getChildren().add(canvas);
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParameters = mapViewParameters;
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        SimpleObjectProperty<Point2D> lastScrollPointerPosition = new SimpleObjectProperty<>();
        pane.setOnScroll(scrollEvent -> {
            lastScrollPointerPosition.set(new Point2D(scrollEvent.getX(), scrollEvent.getY()));
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 25);
            int zoomDelta = (int)Math.signum(scrollEvent.getDeltaY());
            int tempZoom = mapViewParameters.get().zoomLevel() + zoomDelta;
            int newZoom = Math2.clamp(8,mapViewParameters.get().zoomLevel() + zoomDelta,19);
            int newOriginX,newOriginY;


            if(newZoom != tempZoom){
                return;
            }
            if(zoomDelta > 0){
                newOriginX = 2*(int)mapViewParameters.get().originX() +(int)lastScrollPointerPosition.get().getX();
                newOriginY = 2*(int)mapViewParameters.get().originY() +(int)lastScrollPointerPosition.get().getY();
            }
            else if(zoomDelta < 0){
                newOriginX = ((int)mapViewParameters.get().originX() - (int)lastScrollPointerPosition.get().getX())/2;
                newOriginY = ((int)mapViewParameters.get().originY() - (int)lastScrollPointerPosition.get().getY())/2;
            }
            else{
                return;
            }

            int i = 0;
            for (Node waypoint: waypointsManager.pane().getChildren()){
                double oldLayoutX = mapViewParameters.get().viewX(PointWebMercator.ofPointCh(waypointsManager.waypoints().get(i).waypoint()));
                double oldLayoutY = mapViewParameters.get().viewY(PointWebMercator.ofPointCh(waypointsManager.waypoints().get(i).waypoint()));
                double mouseDiffX = oldLayoutX - scrollEvent.getX();
                double mouseDiffY = oldLayoutY - scrollEvent.getY();
                waypoint.setLayoutX(scrollEvent.getX() + mouseDiffX * Math.pow(2,zoomDelta));
                waypoint.setLayoutY(scrollEvent.getY() + mouseDiffY * Math.pow(2,zoomDelta));
                i++;
            }

            mapViewParameters.set(mapViewParameters.get().withNewZoom(newZoom).withMinXY(newOriginX,newOriginY));
            redrawOnNextPulse();
        });

        SimpleObjectProperty<Point2D> lastDragPointerPosition = new SimpleObjectProperty<>();

        pane.setOnMousePressed(e -> {
            if(e.isPrimaryButtonDown()){
                lastDragPointerPosition.set(new Point2D(e.getX(), e.getY()));
            }
        });

        pane.setOnMouseDragged(e -> {
            if(e.isPrimaryButtonDown()){
                int offsetX = (int) (lastDragPointerPosition.get().getX() - e.getX());
                int offsetY = (int) (lastDragPointerPosition.get().getY() - e.getY());
                int oldXOrigin = (int)mapViewParameters.get().originX();
                int oldYOrigin = (int)mapViewParameters.get().originY();

                mapViewParameters.set(mapViewParameters.get().withMinXY(oldXOrigin + offsetX, oldYOrigin + offsetY));
                lastDragPointerPosition.set(new Point2D(e.getX(), e.getY()));
                for (Node waypoint: waypointsManager.pane().getChildren()) {
                    waypoint.setLayoutX(waypoint.getLayoutX() - offsetX);
                    waypoint.setLayoutY(waypoint.getLayoutY() - offsetY); //Todo : un "-" ici pour le offset, mais un "+" au dessus wtf??
                }
                redrawOnNextPulse();
            }
        });

        pane.setOnMouseReleased( e -> {
            if(e.isStillSincePress()){
                waypointsManager.addWaypoint(mapViewParameters.get().originX() + e.getX(),
                                             mapViewParameters.get().originY() + e.getY());
            }
        });
    }

    public Pane pane(){
        return pane;
    }

    private void redrawIfNeeded(){
        int x,y;
        x = Math.floorDiv((int) mapViewParameters.get().originX(),256);
        y = Math.floorDiv((int) mapViewParameters.get().originY(),256);
        int offsetx, offsety;
        offsetx = (int)mapViewParameters.get().originX() % 256;
        offsety = (int)mapViewParameters.get().originY() % 256;

        if(!redrawNeeded) return;
        redrawNeeded = false;

        try{
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            for (int i = 0; i <= (int)pane.getWidth() / 256 + 1; i++){
                for (int j = 0; j <= (int)pane.getHeight() / 256 + 1; j++) {
                    Image image = tileManager.getImageOf((new TileManager.TileId(mapViewParameters.get().zoomLevel(), x + i, y + j)));
                    graphicsContext.drawImage(image,i*256 - offsetx,j*256 - offsety);
                }
            }
        }
        catch (IOException ignored) {
        }
        redrawOnNextPulse();
    }

    private void redrawOnNextPulse(){ //à appeler quand un event est appelé.
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    //MOLETTE
    //DEPLACEMENT CARTE
    //PT DE PASSAGE (CLIC)

}
