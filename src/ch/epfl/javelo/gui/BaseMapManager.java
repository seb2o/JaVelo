package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class BaseMapManager {

    private boolean redrawNeeded = true;
    private Pane pane;
    private Canvas canvas;
    private TileManager tileManager;
    private WaypointsManager waypointsManager;
    private MapViewParameters mapViewParameters;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters>  mapViewParameters){
        this.canvas = new Canvas();
        this.pane = new Pane();
        this.pane.getChildren().add(canvas);
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParameters = mapViewParameters.get();

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

    }

    public Pane pane(){
        return pane;
    }

    private void redrawIfNeeded(){
        int x,y;
        x = Math.floorDiv((int) mapViewParameters.originX(),256);
        y = Math.floorDiv((int) mapViewParameters.originY(),256);
        int offsetx, offsety;
        offsetx = (int)mapViewParameters.originX() % 256;
        offsety = (int)mapViewParameters.originY() % 256;

        if(!redrawNeeded) return;
        redrawNeeded = false;

        System.out.println(this.pane.getWidth() + " " + this.pane.getHeight());
        System.out.println(this.waypointsManager.pane().getWidth() + " " + this.waypointsManager.pane().getHeight());
        try{
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            for (int i = 0; i <= (int)pane.getWidth() / 256 + 1; i++){
                for (int j = 0; j <= (int)pane.getHeight() / 256 + 1; j++) {
                    Image image = tileManager.getImageOf((new TileManager.TileId(mapViewParameters.zoomLevel(), x + i, y + j)));
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
