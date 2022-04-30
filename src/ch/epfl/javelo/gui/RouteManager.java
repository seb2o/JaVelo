package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Edge;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class RouteManager {

    private Pane pane;
    private MapViewParameters mapViewParameters;
    private RouteBean routeBean;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty, Consumer<String> consumer){
        this.pane = new Pane();
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParametersProperty.get();
        Polyline polyline = new Polyline();
        Circle highlight = new Circle(5);
        polyline.setId("route");
        highlight.setId("highlight");
        pane.getChildren().addAll(polyline,highlight);
        pane.setPickOnBounds(false);
        polyline.getPoints().addAll(buildPointList());

        PointCh highlightPch = routeBean.route().pointAt(routeBean.getHighlitedPosition());
        highlight.setLayoutX(mapViewParameters.viewX(PointWebMercator.ofPointCh(highlightPch)));
        highlight.setLayoutY(mapViewParameters.viewY(PointWebMercator.ofPointCh(highlightPch)));

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
                highlight.setLayoutX(highlight.getLayoutX() + oldX - newX);
                highlight.setLayoutY(highlight.getLayoutY() + oldY - newY);
            }
        });

        routeBean.routeProperty().addListener(((observable, oldValue, newValue) -> {
            polyline.getPoints().setAll(buildPointList());
        }));
    }
    public Pane pane(){
        return pane;
    }

    public List<Double> buildPointList(){
        List<Double> pointListTemp = new ArrayList<>();
        for (Edge edge: routeBean.route().edges()) {
            PointCh pch = edge.fromPoint();
            PointWebMercator pwb = PointWebMercator.ofPointCh(pch);
            pointListTemp.add(mapViewParameters.viewX(pwb));
            pointListTemp.add(mapViewParameters.viewY(pwb));
        }
        return pointListTemp;
    }
}
