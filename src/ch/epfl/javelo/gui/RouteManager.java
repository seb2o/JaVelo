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

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty, Consumer<String> consumer){
        this.pane = new Pane();
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParametersProperty;
        Polyline polyline = new Polyline();
        highlight = new Circle(5);
        polyline.setId("route");
        highlight.setId("highlight");
        pane.getChildren().addAll(polyline, highlight);
        pane.setPickOnBounds(false);
//        polyline.getPoints().addAll(buildPointList());
//        updateHighlightPosition();


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
                if(!Double.isNaN(routeBean.getHighlitedPosition())){
                    highlight.setLayoutX(highlight.getLayoutX() + oldX - newX);
                    highlight.setLayoutY(highlight.getLayoutY() + oldY - newY);
                }
            }

            if(oldZoom != newZoom){
                polyline.getPoints().setAll(buildPointList());
                polyline.setLayoutX(0);
                polyline.setLayoutY(0); //Pas facile à trouver !
                updateHighlightPosition();
            }
        });

        routeBean.routeProperty().addListener(((observable, oldValue, newValue) -> {
            polyline.getPoints().setAll(buildPointList());
            polyline.setLayoutX(0);
            polyline.setLayoutY(0);
        }));

        highlight.setOnMousePressed(e ->{
            if(e.isStillSincePress()){
                System.out.println("Un point de passage est déjà présent à cet endroit !\n");
            }
            //Todo : faire un truc pour creer un waypoint au bon endroit quand on clique sur le cercle
        });

    }
    public Pane pane(){
        return pane;
    }

    private List<Double> buildPointList(){
        List<Double> pointListTemp = new ArrayList<>();
        for (Edge edge: routeBean.route().edges()) {
            PointCh pch = edge.fromPoint();
            PointWebMercator pwb = PointWebMercator.ofPointCh(pch);
            pointListTemp.add(mapViewParameters.get().viewX(pwb));
            pointListTemp.add(mapViewParameters.get().viewY(pwb));
        }
        return pointListTemp;
    }
    private void updateHighlightPosition(){
        double highlightPos = routeBean.getHighlitedPosition();
        if (Double.isNaN(highlightPos)) {
            return;
        }
        PointCh highlightPch = routeBean.route().pointAt(highlightPos);
        highlight.setLayoutX(mapViewParameters.get().viewX(PointWebMercator.ofPointCh(highlightPch)));
        highlight.setLayoutY(mapViewParameters.get().viewY(PointWebMercator.ofPointCh(highlightPch)));
    }

}
