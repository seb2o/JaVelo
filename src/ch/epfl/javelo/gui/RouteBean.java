package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.routing.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public final class RouteBean {
    private ObservableList<Waypoint> waypoints;
    private List<Route> routeList;
    private ReadOnlyObjectProperty<Route> route;
    private DoubleProperty highlightedPosition;
    private ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private RouteComputer routeComputer;

    public RouteBean(RouteComputer routeComputer){
        this.routeList = new ArrayList<>();
        this.routeComputer = routeComputer;
        if(waypoints.size() >=2){
            computeRouteBetween(0, waypoints.size());
            elevationProfile = new SimpleObjectProperty<>(ElevationProfileComputer.elevationProfile(route.get(),5.0));
        }
        else{
            this.route = new SimpleObjectProperty<>(null);
            this.elevationProfile = new SimpleObjectProperty<>(null);
        }
        waypoints.addListener((ListChangeListener<Waypoint>) c -> {
            if(c.wasAdded()){
                c.next();
                computeRouteBetween(c.getFrom() - 1, c.getFrom());
                elevationProfile = new SimpleObjectProperty<>(ElevationProfileComputer.elevationProfile(route.get(),5.0));
            }

            //Todo : wasremoved etc;
            //Todo : cache ? mais ca me parait inutile;
        });


    }
    private void computeRouteBetween(int start, int end){
        Preconditions.checkArgument(start < end);
        for (int i = start; i < end - 1; i++) {
            routeList.add(routeComputer.bestRouteBetween(waypoints.get(i).closestNodeId(), waypoints.get(i+1).closestNodeId()));
        }
        this.route = new SimpleObjectProperty<>(new MultiRoute(routeList));
    }

}
