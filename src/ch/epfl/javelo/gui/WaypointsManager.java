package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

public final class WaypointsManager {
    private Pane pane;
    private ObservableList<Waypoint> waypoints;
    private Graph graph;
    private Consumer<String> consumer;


    public WaypointsManager(Graph graph, MapViewParameters mapViewParameters, ObservableList<Waypoint> waypoints, Consumer<String> consumer){
        this.consumer = consumer;
        this.pane = new Pane();
        this.waypoints = waypoints;
        this.graph = graph;
        int index = 0;
        for (Waypoint waypoint : waypoints) {
            Group group = createPin();
            pane.getChildren().add(group);
            String CSSClass = index == 0 ? "first" : (index != waypoints.size()-1 ? "middle" : "last"); //Todo : insh ca marche
            group.getStyleClass().add(CSSClass);
            //PointWebMercator pwb = new PointWebMercator(waypoint.waypoint().e(),waypoint.waypoint().n());
            //Todo :  mettre autre chose..

            //group.setLayoutX(mapViewParameters.viewX(pwb));
            //group.setLayoutY(mapViewParameters.viewY(pwb));
            index++;
        }
        //Todo : pas sûr de devoir recréer un nouveau pin à chaque fois, à tester.

    }

    private Group createPin(){
        Group group = new Group();
        SVGPath exterior = new SVGPath(), interior = new SVGPath();
        exterior.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        interior.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        exterior.getStyleClass().add("pin_outside");
        interior.getStyleClass().add("pin_inside");
        group.getStyleClass().add("pin");
        group.getChildren().add(exterior);
        group.getChildren().add(interior);
        return group;
    }

    public Pane pane(){
        return pane; //Todo : pb d'immuabilité ?
    }

    public void addWaypoint(double x, double y){
        PointCh pointCh = new PointCh(x,y);
        int closestNodeId = graph.nodeClosestTo(pointCh,1000);
        if(closestNodeId == -1){
            consumer.accept("Aucune route à proximité !");
        }
        waypoints.add(new Waypoint(pointCh, closestNodeId));
    }


}
