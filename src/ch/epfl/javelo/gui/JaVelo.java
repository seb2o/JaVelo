package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {
    public static void main(String[] args) { launch(args); }

    private BorderPane pane;
    private ElevationProfileManager elevationProfileManager;
    private ObservableList<Waypoint> waypoints;
    private final double maxStepLength = 5;
    private SimpleObjectProperty<ElevationProfile> elevationProfileProperty = new SimpleObjectProperty<>();
    private ReadOnlyDoubleProperty highlightedPositionProperty;

    @Override
    public void start(Stage primaryStage) throws IOException, NonInvertibleTransformException {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "tile.openstreetmap.org";
        CostFunction costFunction = new CityBikeCF(graph);
        RouteBean routeBean = new RouteBean(new RouteComputer(graph, costFunction));
        Consumer<String> errorConsumer = new ErrorConsumer();
        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);

        this.elevationProfileManager = new ElevationProfileManager(elevationProfileProperty,routeBean.highlightedPositionProperty());
        highlightedPositionProperty = elevationProfileManager.mousePositionOnProfileProperty();

        SplitPane splitpane = new SplitPane();
        splitpane.getItems().add(annotatedMapManager.pane());
        splitpane.orientationProperty().set(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(annotatedMapManager.pane(),false);

        this.waypoints = routeBean.waypoints();
        this.pane = new BorderPane(splitpane);
        pane.getStylesheets().add("map.css");

        //todo titre : JaVelo;
        //todo setOnAction;
        MenuItem exportGPX = new MenuItem("Exporter GPX");
        Menu Fichier = new Menu("Fichier", null, exportGPX);
        MenuBar menuBar = new MenuBar(Fichier);

        pane.setTop(menuBar);
        pane.setCenter(splitpane);
        primaryStage.setScene(new Scene(pane));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.show();

        routeBean.routeProperty().addListener(((observable, oldValue, newValue) -> {
            if( waypoints.size() < 2 || routeBean.route() == null || newValue == null){
                elevationProfileProperty.set(null);
            }
            else{
                elevationProfileProperty.set(ElevationProfileComputer.elevationProfile(newValue, maxStepLength));
            }
        }));

        waypoints.addListener((ListChangeListener<Waypoint>) c ->{
            if(waypoints.size() >= 2 && routeBean.route() != null){
                elevationProfileProperty.set(ElevationProfileComputer.elevationProfile(routeBean.route(), maxStepLength));
            }
        });

        elevationProfileProperty.addListener(((observable, oldValue, newValue) -> {
            if(elevationProfileProperty.get() == null){
                splitpane.getItems().remove(1);
            }
            else{
                if(splitpane.getItems().size() == 1){
                    splitpane.getItems().add(elevationProfileManager.pane());
                }
                else{
                    splitpane.getItems().set(1,elevationProfileManager.pane());
                }
            }
        }));

        highlightedPositionProperty.addListener((observable, oldValue, newValue) -> {
            if(highlightedPositionProperty.getValue() > 0){
                routeBean.setHighlightedPosition(routeBean.highlightedPositionProperty().get());
            }
            else{
                routeBean.setHighlightedPosition(elevationProfileManager.mousePositionOnProfileProperty().get());
            }
        });

        splitpane.getItems().addListener((ListChangeListener<Node>) c -> {
            exportGPX.disableProperty().set(c.getList().size() != 1);
        });
    }

    private static final class ErrorConsumer implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
}
