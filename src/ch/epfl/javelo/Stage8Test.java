package ch.epfl.javelo;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.gui.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class Stage8Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of("tiles");
        String tileServerHost = "https://tile.openstreetmap.org";
        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));
        routeBean.setHighlightedPosition(1000);

        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
        ObservableList<Waypoint> waypoints = routeBean.waypoints();

        Consumer<String> errorConsumer = new ErrorConsumer();

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        waypoints,
                        errorConsumer);
        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);
        RouteManager routeManager = new RouteManager(routeBean,mapViewParametersP,errorConsumer);


        StackPane mainPane =
                new StackPane(baseMapManager.pane(),waypointsManager.pane(),routeManager.pane());
        mainPane.getStylesheets().add("map.css");
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(900);
        primaryStage.show();
    }

    private static final class ErrorConsumer implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
}