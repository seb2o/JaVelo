package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
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

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, MapViewParameters mapViewParameters){
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

    }

    public Pane pane(){
        return pane;
    }

    private void redrawIfNeeded(){
        int x,y;
        x = Math.floorDiv((int) mapViewParameters.originX(),256);
        y = Math.floorDiv((int) mapViewParameters.originY(),256);



        if(!redrawNeeded) return;
        redrawNeeded = false;

        try{
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            for (int i = 0; i <= (int)pane.getWidth() / 256; i++){
                for (int j = 0; j <= (int)pane.getHeight() / 256; j++) {
                    Image image = tileManager.getImageOf((new TileManager.TileId(mapViewParameters.zoomLevel(), x + i, y + j)));
                    graphicsContext.drawImage(image,i*256,j*256);
                }
            }
            System.out.println(pane.getWidth() + " "+pane.getHeight());
            System.out.println(canvas.getWidth() + " "+canvas.getHeight());
        }
        catch (IOException ignored) {
        }
    }

    private void redrawOnNextPulse(){ //à appeler quand un event est appelé.
        redrawNeeded = true;
        Platform.requestNextPulse();
    }



    //MOLETTE
    //DEPLACEMENT CARTE
    //PT DE PASSAGE (CLIC)

}
