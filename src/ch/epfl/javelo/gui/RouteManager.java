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

/**
 * Classe gèrant l'affichage de l'itinéraire et une partie de l'interaction avec lui.
 */
public final class RouteManager {

    private Pane pane;
    private ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;
    private RouteBean routeBean;
    private Circle highlight;

    /**
     * Constructeur du gestionnaire de route.
     * @param routeBean son bean de Route associé.
     * @param mapViewParametersProperty des paramètres de carte.
     */
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

        //Listener qui gère le placement de la route sur la carte en fonction de changements sur le niveau
        // de zoom ou de position du font de carte.
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

        //Listener qui gère l'affichage et le placement de la polyline représentant la route quand le nombre
        //waypoint change.
        routeBean.routeProperty().addListener(((observable, oldValue, newValue) -> {
            polyline.getPoints().setAll(buildPointList());

            polyline.visibleProperty().set(!routeBean.shouldHideRoute());
            highlight.visibleProperty().set(!routeBean.shouldHideRoute());

            polyline.setLayoutX(0);
            polyline.setLayoutY(0);
            updateHighlightPosition();
        }));

        //Listener qui ajoute un WayPoint intermédiaire quand le point en subrillance est cliqué.
        highlight.setOnMouseClicked(e ->{
            PointCh pointCh = mapViewParameters.get().pointAt(highlight.getLayoutX(), highlight.getLayoutY()).toPointCh();
            int nodeId = routeBean.route().nodeClosestTo(routeBean.highlightedPosition());
            if(nodeId != -1 ){
                routeBean.waypoints().add(
                        routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition()) + 1,
                        new Waypoint(pointCh, nodeId));
            }

        });



        //Listener qui met à jour la position affichée du point en surbrillance quand la position change.
        routeBean.highlightedPositionProperty().addListener((observable, oldValue, newValue) -> updateHighlightPosition());

    }

    /**
     * @return le pane associé au gestionnaire de route.
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Construit la liste des coordonnées à partir de la route qui vont servir à former la polyline.
     * Le premier élément est la coordonnée x du 1er noeud, le deuxième la coordonnée y du 1er noeud,
     * le troisième est la coordonnée x du 2ème noeud etc.
     * @return la liste des coordonnées en pixels des points de la polyline.
     */
    private List<Double> buildPointList(){
        if(routeBean.route() == null || routeBean.route().edges().isEmpty()){
            highlight.visibleProperty().set(false);
            return new ArrayList<>();
        }
        List<Double> pointListTemp = new ArrayList<>();
        for (Edge edge: routeBean.route().edges()) {
            PointCh pch = edge.fromPoint();
            PointWebMercator pwb = PointWebMercator.ofPointCh(pch);
            pointListTemp.add(mapViewParameters.get().viewX(pwb));
            pointListTemp.add(mapViewParameters.get().viewY(pwb));
        }
        PointCh lastPointCh = routeBean.route().edges()
                .get(routeBean.route().edges().size() - 1)
                .toPoint();
        PointWebMercator lastPwb = PointWebMercator.ofPointCh(lastPointCh);
        pointListTemp.add(mapViewParameters.get().viewX(lastPwb));
        pointListTemp.add(mapViewParameters.get().viewY(lastPwb));
        return pointListTemp;
    }

    /**
     * Met à jour la position du point en surbrillance sur l'itinéraire et décide s'il doit être visible ou non.
     */
    private void updateHighlightPosition(){
        if(routeBean.route() != null){
            double highlightPos = routeBean.highlightedPosition();
            if (Double.isNaN(highlightPos)) {
                highlight.visibleProperty().set(false);
                return;
            }
            PointCh highlightPch = routeBean.route().pointAt(highlightPos);
            highlight.setLayoutX(mapViewParameters.get().viewX(PointWebMercator.ofPointCh(highlightPch)));
            highlight.setLayoutY(mapViewParameters.get().viewY(PointWebMercator.ofPointCh(highlightPch)));
            highlight.visibleProperty().set(true);
        }
        else{
            highlight.visibleProperty().set(false);
        }
    }

}
