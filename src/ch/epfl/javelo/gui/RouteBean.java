package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class RouteBean {
    private ObservableList<Waypoint> waypoints;
    private SimpleObjectProperty<Route> route; //Todo : retourner une ReadOnlyObjectProperty
    private DoubleProperty highlightedPosition;
    private SimpleObjectProperty<ElevationProfile> elevationProfile; //Todo : idem
    private RouteComputer routeComputer;
    private LinkedHashMap<WaypointPair, Route> cache = new LinkedHashMap<>(100, 0.75f, true);

    public RouteBean(RouteComputer routeComputer){
        this.routeComputer = routeComputer;
        this.highlightedPosition = new SimpleDoubleProperty(Double.NaN);
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();

        this.waypoints = FXCollections.observableArrayList(
                new Waypoint(new PointCh(2532697, 1152350), 159049), //Todo : mettre dans des ctes ?
                new Waypoint(new PointCh(2538659, 1154350), 117669));

        if(waypoints.size() >= 2){
            this.route.set(computeMultiRoute());
            //elevationProfile = new SimpleObjectProperty<>(ElevationProfileComputer.elevationProfile(route.get(),5.0));
        }
        else{
            this.route = new SimpleObjectProperty<>(null);
            //this.elevationProfile = new SimpleObjectProperty<>(null);
        }
        waypoints.addListener((ListChangeListener<Waypoint>) c -> {
            c.next();
            if(c.wasAdded()){
                route.set(computeMultiRoute());
                //elevationProfile = new SimpleObjectProperty<>(ElevationProfileComputer.elevationProfile(route.get(),5.0));
            }
            if(c.wasRemoved()){
                route.set(computeMultiRoute());
            }
        });

    }


    private MultiRoute computeMultiRoute(){
        List<Route> routeList = new ArrayList<>();
        for(int index = 0; index < waypoints.size() - 1; index++){
            routeList.add(computeRouteBetween(waypoints().get(index), waypoints().get(index+1)));
        }
        return new MultiRoute(routeList);
    }

    private Route computeRouteBetween(Waypoint w1, Waypoint w2){
        WaypointPair wp = new WaypointPair(w1,w2);
        Route route = cache.get(wp);
            if(route != null){
                return route;
            }
        return routeComputer.bestRouteBetween(w1.closestNodeId(), w2.closestNodeId());
    }

    public ReadOnlyObjectProperty<Route> routeProperty(){
        return route;
    }

    public Route route(){
        return this.route.get(); //todo : pas un getter intrusif ? car pas readonly, transtypage à faire ?
    }

    public ObservableList<Waypoint> waypoints(){
        return this.waypoints;
    }

    public Double getHighlitedPosition(){
        return this.highlightedPosition.get();
    }

    public void setHighlightedPosition(double highlightedPosition) {
        this.highlightedPosition.set(highlightedPosition);
    }

    private class WaypointPair{
        private Waypoint w1;
        private Waypoint w2;
        private WaypointPair(Waypoint w1, Waypoint w2){
            this.w1 = w1;
            this.w2 = w2;
        }

        public Waypoint get1(){
            return w1;
        }

        public  Waypoint get2(){
            return w2;
        }

        @Override
        public boolean equals(Object object){
            if(object instanceof  WaypointPair){
                Waypoint ow1 = ((WaypointPair) object).get1();
                Waypoint ow2 = ((WaypointPair) object).get2();
                return (ow1.equals(w1) && ow2.equals(w2)) || (ow1.equals(w2) && ow2.equals(w1));
                //Todo : peut etre pas mettre le deuxieme termes après le ||;
            }
            return false;
        }
    }
}
