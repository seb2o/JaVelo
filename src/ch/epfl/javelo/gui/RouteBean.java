package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public final class RouteBean {
    private ObservableList<Waypoint> waypoints;
    private SimpleObjectProperty<Route> route; //Todo : retourner une ReadOnlyObjectProperty
    private DoubleProperty highlightedPosition;
    private SimpleObjectProperty<ElevationProfile> elevationProfile; //Todo : idem
    private RouteComputer routeComputer;

    public RouteBean(RouteComputer routeComputer){
        this.routeComputer = routeComputer;
        this.highlightedPosition = new SimpleDoubleProperty();
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();

        this.waypoints = FXCollections.observableArrayList(
                new Waypoint(new PointCh(2532697, 1152350), 159049),
                new Waypoint(new PointCh(2538659, 1154350), 117669));

        if(waypoints.size() >= 2){
            this.route.set(computeRouteBetween(0, waypoints.size()));
            //elevationProfile = new SimpleObjectProperty<>(ElevationProfileComputer.elevationProfile(route.get(),5.0));
        }
        else{
            this.route = new SimpleObjectProperty<>(null);
            //this.elevationProfile = new SimpleObjectProperty<>(null);
        }
        waypoints.addListener((ListChangeListener<Waypoint>) c -> {
            c.next();
            if(c.wasAdded()){
                route.set(computeRouteBetween(c.getFrom() - 1, c.getFrom()));
                //elevationProfile = new SimpleObjectProperty<>(ElevationProfileComputer.elevationProfile(route.get(),5.0));
            }
            if(c.wasRemoved()){
                route.set(computeRouteBetween(1,2));
            }

            //Todo : wasremoved etc;
            //Todo : cache ? mais ca me parait inutile;
        });


    }
    private Route computeRouteBetween(int start, int end){
        Preconditions.checkArgument(start < end); //Todo :peut-être dispensable
        List<Route> routeList = new ArrayList<>();
        for (int i = 0; i < waypoints().size() - 1; i++) { //Todo : remettre tsart et end ?
            routeList.add(routeComputer.bestRouteBetween(waypoints.get(i).closestNodeId(), waypoints.get(i+1).closestNodeId()));
        }
        return new MultiRoute(routeList);

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
}
