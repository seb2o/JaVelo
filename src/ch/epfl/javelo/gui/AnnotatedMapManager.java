package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {
    private Graph graph;
    private TileManager tileManager;
    private RouteBean routeBean;
    private ErrorManager errorManager;
    private BaseMapManager baseMapManager;
    private WaypointsManager waypointsManager;
    private SimpleObjectProperty<MapViewParameters> mapViewParametersProperty;
    private RouteManager routeManager;
    private Pane pane;
    private DoubleProperty mousePositionOnRouteProperty;
    private SimpleObjectProperty<Point2D> mousePos;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, ErrorManager errorManager){
        this.graph = graph;
        this.tileManager = tileManager;
        this.routeBean = routeBean;
        this.errorManager = errorManager;
        this.mapViewParametersProperty = new SimpleObjectProperty<>(new MapViewParameters(12,543200,370650));
        this.waypointsManager = new WaypointsManager(graph,mapViewParametersProperty, routeBean.waypoints(), errorManager);
        this.baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersProperty);
        this.routeManager = new RouteManager(routeBean,mapViewParametersProperty);
        this.mousePos = new SimpleObjectProperty<>(new Point2D(0,0));
        this.pane = new StackPane(baseMapManager.pane(),waypointsManager.pane(),routeManager.pane());
        pane.getStylesheets().add("map.css");
        this.mousePositionOnRouteProperty = new SimpleDoubleProperty(Double.NaN);

        pane.setOnMouseMoved(e ->{
            mousePos.set(new Point2D(e.getX(),e.getY()));
            if(routeBean.route() != null){
                Point2D currentMousePos = mousePos.get();
                int zoomLevel = mapViewParametersProperty.get().zoomLevel();
                PointWebMercator pwb = mapViewParametersProperty.get().pointAt(currentMousePos.getX(),currentMousePos.getY());
                PointCh pch = pwb.toPointCh();
                if(pch != null){
                    RoutePoint closestRoutePoint = routeBean.route().pointClosestTo(pch);
                    PointWebMercator routePwb = PointWebMercator.ofPointCh(closestRoutePoint.point());
                    double squaredDistance = Math2.squaredNorm(routePwb.xAtZoomLevel(zoomLevel) - pwb.xAtZoomLevel(zoomLevel),routePwb.yAtZoomLevel(zoomLevel) - pwb.yAtZoomLevel(zoomLevel));
                    if(squaredDistance < 15 * 15 && routeBean.waypoints().size() > 1){
                        mousePositionOnRouteProperty.set(closestRoutePoint.position());
                        return;
                    }
                }
            }
            mousePositionOnRouteProperty.set(Double.NaN);

        });
        pane.setOnMouseExited(e ->{
            mousePositionOnRouteProperty.set(Double.NaN);
        });

        mousePositionOnRouteProperty.addListener(((observable, oldValue, newValue) -> {
            routeBean.setHighlightedPosition(mousePositionOnRouteProperty.get());
        }));
    }

    public Pane pane() {
        return pane;
    }

    public DoubleProperty mousePositionOnRouteProperty(){
        return mousePositionOnRouteProperty;
    }

}
