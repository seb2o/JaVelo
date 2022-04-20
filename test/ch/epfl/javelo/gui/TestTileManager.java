package ch.epfl.javelo.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.nio.file.Path;

public final class TestTileManager extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        TileManager tm = new TileManager(
                Path.of("tiles"), "https://tile.openstreetmap.org");
        tm.getImageOf(new TileManager.TileId(19, 271725, 185422));
        tm.getImageOf(new TileManager.TileId(19, 271725, 185422));
        Platform.exit();
    }
}
