package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.List;
import java.util.function.Consumer;

public final class WaypointsManager {
    private Pane pane;
    private ObservableList<Waypoint> waypoints;
    private Graph graph;
    private Consumer<String> consumer;
    private ObjectProperty<MapViewParameters> mapViewParameters;

    private  SimpleObjectProperty<Point2D> lastDragPointerPosition = new SimpleObjectProperty<>(new Point2D(0,0));

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParameters, ObservableList<Waypoint> waypoints, Consumer<String> consumer){
        this.consumer = consumer;
        this.pane = new Pane();
//        pane.setPickOnBounds(false);
        this.waypoints = waypoints;
        this.graph = graph;
        this.mapViewParameters = mapViewParameters;
        for (Waypoint waypoint : waypoints) {
            PointWebMercator pwb = PointWebMercator.ofPointCh(waypoint.coordinates());
            Group pin = createPin();
            pin.setLayoutX(mapViewParameters.get().viewX(pwb));
            pin.setLayoutY(mapViewParameters.get().viewY(pwb));
            pane.getChildren().add(pin);
        }
        updateColor();
        pane.setPickOnBounds(false);

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

    private void updateColor(){
        int index = 0;
        List<Node> list = pane.getChildren();
        int size = list.size();
        for (Node waypointNode : list ){
            List<String> styleClass = waypointNode.getStyleClass();
            styleClass.removeAll(List.of("first","middle","last"));
            String color = index == 0 ? "first" : index == size - 1 ? "last" : "middle";
            styleClass.add(color);
            index++;
        }
    }

    public Pane pane(){
        return pane;
    }

    public ObservableList<Waypoint> waypoints(){
        return waypoints;
    }

    public boolean addWaypoint(double x,double y){
        return addWaypointAtIndex(x,y,waypoints.size());
    }

    private boolean addWaypointAtIndex(double x, double y, int atIndex){
        PointWebMercator pwb = PointWebMercator.of(mapViewParameters.get().zoomLevel(),x,y);
        PointCh pch = pwb.toPointCh();
        int closestNodeId = graph.nodeClosestTo(pch,1000);
        if(closestNodeId == -1){
            consumer.accept("Aucune route à proximité !");
            return false;
        }
        Waypoint newWaypoint = new Waypoint(pch, closestNodeId);
        Group group = createPin();
        group.setLayoutX(mapViewParameters.get().viewX(pwb));
        group.setLayoutY(mapViewParameters.get().viewY(pwb));
        try {//todo degeu a changer
            waypoints.add(atIndex, newWaypoint);
            pane.getChildren().add(atIndex, group);
        } catch (Exception e) {
            System.out.println("catcherrt");
            waypoints.add( newWaypoint);
            pane.getChildren().add( group);
        }
        int index = 0;
        for (Node node : pane.getChildren()) {
            node.getStyleClass().removeAll("first","middle","last");
            if(index == 0){
                node.getStyleClass().add("first");
            }
            else if(index == pane.getChildren().size() - 1){
                node.getStyleClass().add("last");
            }
            else{
                node.getStyleClass().add("middle");
            }
            index++;
        }


        group.setOnMousePressed(e ->{
            if(e.isPrimaryButtonDown()){
                lastDragPointerPosition.set(new Point2D(e.getSceneX(),e.getSceneY()));
            }
        });

        group.setOnMouseDragged(e ->{
            if(e.isPrimaryButtonDown()){
                double offsetX = (lastDragPointerPosition.get().getX() - e.getSceneX());
                double offsetY = (lastDragPointerPosition.get().getY() - e.getSceneY());
                group.setLayoutX(group.getLayoutX() - offsetX);
                group.setLayoutY(group.getLayoutY() - offsetY);

                lastDragPointerPosition.set(new Point2D(e.getSceneX(), e.getSceneY()));
            }
        });

        group.setOnMouseReleased(e ->{
            if(e.isStillSincePress()){
                this.waypoints().remove(newWaypoint);
                pane.getChildren().remove(group);
                updateColor();
            }
            else{
                if(addWaypointAtIndex(
                        group.getLayoutX() + mapViewParameters.get().originX(),
                        group.getLayoutY() + mapViewParameters.get().originY(),
                        atIndex)){
                    this.waypoints().remove(newWaypoint);
                    pane.getChildren().remove(group);
                    if(waypoints.size() - 1 == atIndex){
                        pane.getChildren().get(pane.getChildren().size() - 1)
                                .getStyleClass().remove("middle");
                        pane.getChildren().get(pane.getChildren().size() - 1)
                                .getStyleClass().add("last");
                    }
                }
                else{
                    PointWebMercator oldPos = PointWebMercator.ofPointCh(newWaypoint.coordinates());
                    int zoomLevel = mapViewParameters.get().zoomLevel();
                    double originX = mapViewParameters.get().originX();
                    double originY = mapViewParameters.get().originY();

                    group.setLayoutX(oldPos.xAtZoomLevel(zoomLevel) - originX);
                    group.setLayoutY(oldPos.yAtZoomLevel(zoomLevel) - originY);
                }
            }
        });
        return true;
    }


}