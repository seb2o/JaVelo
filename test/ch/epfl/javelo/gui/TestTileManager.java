package ch.epfl.javelo.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

public final class TestTileManager extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws IOException {
        TileManager tm = new TileManager(
                Path.of("tiles"), "https://tile.openstreetmap.org");
        try {
            tm.getImageOf(new TileManager.TileId(19, 271725, 185422));
            tm.getImageOf(new TileManager.TileId(19, 271725, 185422));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Platform.exit();
    }
}
