package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapManager {

    private boolean redrawNeeded = true;
    private Canvas canvas;
    private Pane pane;
    private TileManager tileManager;
    private WaypointsManager waypointsManager;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, MapViewParameters properties){
        this.canvas = new Canvas();
        this.pane = new Pane();
        pane.setOnScroll(pane.getOnScroll());
        pane.setOnMousePressed(pane.getOnMousePressed());
        ScrollEvent = new ScrollEvent()
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;

        canvas.widthProperty().bind(pane().widthProperty());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

    }

    public Pane pane(){
        return pane;
    }

    private void redrawIfNeeded(){
        TileManager.TileId tileId = new TileManager.TileId(1,0,0);

        if(!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        try{Image image = tileManager.getImageOf(tileId);
            graphicsContext.drawImage(image,10,10);
        }
        catch (IOException ignored) {
        }

        //LOOP
        ScrollEvent scrollEvent = pane.getOnScroll();
        tileId.zoomLevel() += pane.getOnScroll().g
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
