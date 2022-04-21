package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapManager {

    private boolean redrawNeeded = true;
    private Pane pane;
    private Canvas canvas;
    private TileManager tileManager;
    private WaypointsManager waypointsManager;
    private MapViewParameters mapViewParameters;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, MapViewParameters mapViewParameters){
        this.pane = new Pane();
        this.canvas = new Canvas();
        this.pane.getChildren().add(canvas);
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParameters = mapViewParameters;

        canvas.widthProperty().bind(pane.widthProperty());
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
        TileManager.TileId tileId = new TileManager.TileId(mapViewParameters.zoomLevel(),x,y);

        if(!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        try{
            Image image = tileManager.getImageOf(tileId);
            graphicsContext.drawImage(image,0,0);
        }
        catch (IOException ignored) {
        }
        //LOOP
        //LOOP
    }

    private void redrawOnNextPulse(){
        redrawNeeded = true;
        Platform.requestNextPulse();
    }



    //MOLETTE
    //DEPLACEMENT CARTE
    //PT DE PASSAGE (CLIC)

}
