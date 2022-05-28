package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Edge;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class RouteManager {

    private Pane pane;
    private ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;
    private RouteBean routeBean;
    private Circle highlight;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty){
        this.pane = new Pane();
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParametersProperty;
        Polyline polyline = new Polyline();
        highlight = new Circle(5);
        polyline.setId("route");
        highlight.setId("highlight");
        pane.getChildren().addAll(polyline, highlight);
        pane.setPickOnBounds(false);
        updateHighlightPosition();

        mapViewParametersProperty.addListener( (observable, oldValue, newValue) -> {
            double oldX = oldValue.originX();
            double oldY = oldValue.originY();
            double newX = newValue.originX();
            double newY = newValue.originY();
            int oldZoom = oldValue.zoomLevel();
            int newZoom = newValue.zoomLevel();

            if((oldX != newX || oldY != newY) && oldZoom == newZoom){
                polyline.setLayoutX(polyline.getLayoutX() + oldX - newX);
                polyline.setLayoutY(polyline.getLayoutY() + oldY - newY);
                if(!Double.isNaN(routeBean.highlightedPosition())){
                    highlight.setLayoutX(highlight.getLayoutX() + oldX - newX);
                    highlight.setLayoutY(highlight.getLayoutY() + oldY - newY);
                }
            }

            if(oldZoom != newZoom){
                if(polyline.getPoints().size() != 0){
                    polyline.getPoints().setAll(buildPointList());
                }
                polyline.setLayoutX(0);
                polyline.setLayoutY(0);
                updateHighlightPosition();
            }
        });

        routeBean.routeProperty().addListener(((observable, oldValue, newValue) -> {
            List<Double> pointList = buildPointList();
            if(pointList == null){
                polyline.visibleProperty().set(false);
                highlight.visibleProperty().set(false);
            }
            else{
                polyline.getPoints().setAll(buildPointList());
                polyline.visibleProperty().set(!routeBean.shouldHideRoute());
                highlight.visibleProperty().set(!routeBean.shouldHideRoute());
            }
            polyline.setLayoutX(0);
            polyline.setLayoutY(0);
            updateHighlightPosition();
        }));

        highlight.setOnMouseClicked(e ->{
            PointCh pointCh = mapViewParameters.get().pointAt(highlight.getLayoutX(), highlight.getLayoutY()).toPointCh();
            int nodeId = routeBean.route().nodeClosestTo(routeBean.highlightedPosition());
            if(nodeId == -1 ){
            }
            else{
                routeBean.waypoints().add(routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition()) + 1, new Waypoint(pointCh, nodeId));
            }

        });

        routeBean.highlightedPositionProperty().addListener((observable, oldValue, newValue) -> {
            updateHighlightPosition();
        });

    }
    public Pane pane(){
        return pane;
    }

    public boolean waypointExistsAtNodeId(int nodeId){
        for (Waypoint waypoint: routeBean.waypoints()) {
            if(waypoint.closestNodeId() == nodeId){
                return true;
            }
        }
        return false;
    }


    private List<Double> buildPointList(){
        if(routeBean.route() == null || routeBean.route().edges().isEmpty()){
            return null;
        }
        List<Double> pointListTemp = new ArrayList<>();
        for (Edge edge: routeBean.route().edges()) {
            PointCh pch = edge.fromPoint();
            PointWebMercator pwb = PointWebMercator.ofPointCh(pch);
            pointListTemp.add(mapViewParameters.get().viewX(pwb));
            pointListTemp.add(mapViewParameters.get().viewY(pwb));
        }
        PointCh lastPointCh = routeBean.route().edges().get(routeBean.route().edges().size() - 1).toPoint();
        PointWebMercator lastPwb = PointWebMercator.ofPointCh(lastPointCh);
        pointListTemp.add(mapViewParameters.get().viewX(lastPwb));
        pointListTemp.add(mapViewParameters.get().viewY(lastPwb));
        return pointListTemp;
    }
    private void updateHighlightPosition(){
        if(routeBean.route() != null){
            double highlightPos = routeBean.highlightedPosition();
            if (Double.isNaN(highlightPos)) {
                highlight.visibleProperty().set(false);
                return;
            }
            highlight.visibleProperty().set(!routeBean.shouldHideRoute());
            PointCh highlightPch = routeBean.route().pointAt(highlightPos);
            highlight.setLayoutX(mapViewParameters.get().viewX(PointWebMercator.ofPointCh(highlightPch)));
            highlight.setLayoutY(mapViewParameters.get().viewY(PointWebMercator.ofPointCh(highlightPch)));
        }
        else{
            highlight.visibleProperty().set(false);
        }
    }

}
