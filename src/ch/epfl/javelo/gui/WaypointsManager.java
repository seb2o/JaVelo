package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.List;



/**
 * @author Edgar Gonzalez (328095)
 * @author Sébastien Boo (345870)
 * Classe gèrant l'affichage et l'interaction avec les points de passage.
 */
public final class WaypointsManager {
    private Pane pane;
    private ObservableList<Waypoint> waypoints;
    private Graph graph;
    private ErrorManager errorManager;
    private ObjectProperty<MapViewParameters> mapViewParameters;

    private SimpleObjectProperty<Point2D> lastDragPointerPosition = new SimpleObjectProperty<>(new Point2D(0,0));

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParameters, ObservableList<Waypoint> waypoints, ErrorManager errorManager){
        this.errorManager = errorManager;
        this.pane = new Pane();
        this.waypoints = waypoints;
        this.graph = graph;
        this.mapViewParameters = mapViewParameters;
        updateColor();
        pane.setPickOnBounds(false);

        waypoints.addListener((ListChangeListener<Waypoint>) c -> {
            c.next();
            if(c.wasAdded()){
                int index = c.getFrom();
                Waypoint addedWaypoint = waypoints().get(index);
                PointWebMercator pwb = PointWebMercator.ofPointCh(addedWaypoint.coordinates());
                Group group = createPin();
                group.setLayoutX(mapViewParameters.get().viewX(pwb));
                group.setLayoutY(mapViewParameters.get().viewY(pwb));
                try {
                    pane.getChildren().add(index, group);
                } catch (Exception e) {
                    pane.getChildren().add(group);
                }

                updateColor();
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
                        this.waypoints().remove(addedWaypoint);
                        pane.getChildren().remove(group);
                        updateColor();
                    }
                    else{
                        if(addWaypointAtIndex(
                                group.getLayoutX() + mapViewParameters.get().originX(),
                                group.getLayoutY() + mapViewParameters.get().originY(),
                                waypoints.indexOf(addedWaypoint))){
                            this.waypoints().remove(addedWaypoint);
                            pane.getChildren().remove(group);
                            updateColor();
                        }
                        else{
                            PointWebMercator oldPos = PointWebMercator.ofPointCh(addedWaypoint.coordinates());
                            int zoomLevel = mapViewParameters.get().zoomLevel();
                            double originX = mapViewParameters.get().originX();
                            double originY = mapViewParameters.get().originY();
                            group.setLayoutX(oldPos.xAtZoomLevel(zoomLevel) - originX);
                            group.setLayoutY(oldPos.yAtZoomLevel(zoomLevel) - originY);
                        }
                    }
                });
            }



        });

    }

    /**
     * Crée un nouveau pin grâce au SVG.
     * @return un nouveau Group, représentant un pin, sans couleur.
     */
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

    /**
     * Méthode qui gère les couleurs des waypoints.
     */
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

    /**
     * @return le Pane associé.
     */
    public Pane pane(){
        return pane;
    }

    /**
     * @return une liste observable de tous les waypoints de la carte.
     */
    public ObservableList<Waypoint> waypoints(){
        return waypoints;
    }

    /**
     * Ajoute un waypoint à la fin de la liste, qui est donc un point d'arrivé.
     * @param x la coordonnée X en PointWebMercator de ce Waypoint.
     * @param y la coordonnée Y en PointWebMercator de ce Waypoint.
     */
    public void addWaypoint(double x, double y){
        addWaypointAtIndex(x, y, waypoints.size());
    }

    /**
     * Ajoute un waypoint à l'index donné.
     * @param x la coordonnée X en PointWebMercator de ce Waypoint.
     * @param y la coordonnée Y en PointWebMercator de ce Waypoint.
     * @return true si le point a bien pu être placé, false sinon.
     */
    private boolean addWaypointAtIndex(double x, double y, int atIndex) {//todo erreur quand waypoint consécutifs même point
        PointWebMercator pwb = PointWebMercator.of(mapViewParameters.get().zoomLevel(), x, y);
        PointCh pch = pwb.toPointCh();
        int closestNodeId = graph.nodeClosestTo(pch, 1000);
        if (closestNodeId == -1) {
            errorManager.displayError("Aucune route à proximité !");
            return false;
        }
        Waypoint newWaypoint = new Waypoint(pch, closestNodeId);
        try {
            waypoints.add(atIndex, newWaypoint);
        } catch (Exception e) {
            waypoints.add(newWaypoint);
        }
        return true;
    }

}