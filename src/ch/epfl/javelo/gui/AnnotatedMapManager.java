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


/**
 * Classe gérant l'affichage de la carte «annotée»,
 * c.-à-d. le fond de carte au-dessus duquel sont superposés l'itinéraire et les points de passage.
 */
public final class AnnotatedMapManager {
    private BaseMapManager baseMapManager;
    private WaypointsManager waypointsManager;
    private SimpleObjectProperty<MapViewParameters> mapViewParametersProperty;
    private RouteManager routeManager;
    private Pane pane;
    private DoubleProperty mousePositionOnRouteProperty;
    private SimpleObjectProperty<Point2D> mousePos;

    /**
     * Constructeur de carte annotée.
     * @param graph le Graph associé à la carte.
     * @param tileManager le gestionnaire de tuile associé à la carte.
     * @param routeBean le bean de route associé à la carte.
     * @param errorManager le gestionnaire d'erreur associé à la carte.
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, ErrorManager errorManager){
        this.mapViewParametersProperty = new SimpleObjectProperty<>(new MapViewParameters(12,543200,370650));
        this.waypointsManager = new WaypointsManager(graph,mapViewParametersProperty, routeBean.waypoints(), errorManager);
        this.baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersProperty);
        this.routeManager = new RouteManager(routeBean,mapViewParametersProperty);
        this.mousePos = new SimpleObjectProperty<>(new Point2D(0,0));
        this.pane = new StackPane(baseMapManager.pane(),waypointsManager.pane(),routeManager.pane());
        pane.getStylesheets().add("map.css");
        this.mousePositionOnRouteProperty = new SimpleDoubleProperty(Double.NaN);


        //Listener qui gère la position du point en surbrillance sur l'intinéraire
        //si la souris en est assez proche.
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

        //Listener qui gère le cas ou la souris sort du panneau contenant la carte annotée.
        pane.setOnMouseExited(e ->{
            mousePositionOnRouteProperty.set(Double.NaN);
        });

        //Listener qui met à jour la position du point en surbrillance par rapport à la position de la souris
        //sur le profile.
        mousePositionOnRouteProperty.addListener(((observable, oldValue, newValue) -> {
            routeBean.setHighlightedPosition(mousePositionOnRouteProperty.get());
        }));
    }

    /**
     * @return le Pane contenant la carte annotée.
     */
    public Pane pane() {
        return pane;
    }

    /**
     * @return la propriété contenant la position du pointeur de la souris le long de l'itinéraire.
     */
    public DoubleProperty mousePositionOnRouteProperty(){
        return mousePositionOnRouteProperty;
    }

}
