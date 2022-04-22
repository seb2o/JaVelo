package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.beans.EventHandler;
import java.io.IOException;


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
        SimpleObjectProperty<Point2D> lastPointerPosition = new SimpleObjectProperty<>();
        pane.setOnScroll(scrollEvent -> {
            lastPointerPosition.set(new Point2D(scrollEvent.getX(), scrollEvent.getY()));
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 250);
            int zoomDelta = (int)Math.signum(scrollEvent.getDeltaY());
            int tempZoom = mapViewParameters.get().zoomLevel() + zoomDelta;
            int newZoom = Math2.clamp(8,mapViewParameters.get().zoomLevel() + zoomDelta,19);
            int newOriginX,newOriginY;
            if(newZoom != tempZoom){
                zoomDelta = 0;
            }
            if(zoomDelta > 0){
                newOriginX = (int)Math.pow(2,zoomDelta)*(int)mapViewParameters.get().originX() + zoomDelta * (int)lastPointerPosition.get().getX();
                newOriginY = (int)Math.pow(2,zoomDelta)*(int)mapViewParameters.get().originY() + zoomDelta * (int)lastPointerPosition.get().getY();
            }
            else if(zoomDelta < 0){
                newOriginX = ((int)mapViewParameters.get().originX() - (int)lastPointerPosition.get().getX())/2;
                newOriginY = ((int)mapViewParameters.get().originY() - (int)lastPointerPosition.get().getY())/2;
            }
            else{
                newOriginX = (int)mapViewParameters.get().originX();
                newOriginY = (int)mapViewParameters.get().originY();
            }
            mapViewParameters.set(mapViewParameters.get().withNewZoom(newZoom).withMinXY(newOriginX,newOriginY));
            redrawOnNextPulse();
            System.out.println(newOriginX);
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
