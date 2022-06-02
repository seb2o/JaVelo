package ch.epfl.test;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.gui.*;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

public final class Stage8Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of("tiles");
        String tileServerHost = "https://tile.openstreetmap.org";
        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));

        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        ErrorManager errorManager = new ErrorManager();
        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph,tileManager,routeBean,errorManager);




        StackPane mainPane =
                new StackPane(annotatedMapManager.pane(), errorManager.pane());
        mainPane.getStylesheets().add("map.css");
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(900);
        primaryStage.show();
    }

}