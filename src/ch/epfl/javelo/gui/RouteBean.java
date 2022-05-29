package ch.epfl.javelo.gui;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class RouteBean {
    private final double MAX_STEP_LENGTH = 5d;
    private ObservableList<Waypoint> waypoints;
    private SimpleObjectProperty<Route> route;
    private DoubleProperty highlightedPosition;
    private SimpleObjectProperty<ElevationProfile> elevationProfile;
    private RouteComputer routeComputer;
    private LinkedHashMap<WaypointPair, Route> cache = new LinkedHashMap<>(100, 0.75f, true);
    private boolean shouldHideRoute;

    public RouteBean(RouteComputer routeComputer){
        this.shouldHideRoute = false;
        this.routeComputer = routeComputer;
        this.highlightedPosition = new SimpleDoubleProperty(Double.NaN);
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();

        this.waypoints = FXCollections.observableArrayList();

        this.route.set(null);
        this.elevationProfile.set(null);

        waypoints.addListener((ListChangeListener<Waypoint>) c -> {
            c.next();
            if(c.wasAdded()){
                if(waypoints.size() >= 2){
                    this.route.set(computeMultiRoute());
                    this.elevationProfile.set((computeElevationProfile()));

                }
                else{
                    this.route.set(null);
                    this.elevationProfile.set(null);
                }
            }
            if(c.wasRemoved()){
                if(waypoints.size() >= 2){
                    this.route.set(computeMultiRoute());
                    this.elevationProfile.set((computeElevationProfile()));
                }
                else{
                    this.route.set(null);
                    this.elevationProfile.set(null);
                }

            }
        });

    }


    private Route computeMultiRoute(){
        List<Route> routeList = new ArrayList<>();
        for(int index = 0; index < waypoints.size() - 1; index++){
            if(waypoints().get(index).closestNodeId() == waypoints().get(index+1).closestNodeId()){
                continue;
            }
            Route temproute = computeRouteBetween(waypoints().get(index), waypoints().get(index+1));
            if(temproute == null){
                shouldHideRoute = true;
                return null;
//                return new SingleRoute(List.of(new Edge(1,1, new PointCh(2532697, 1152350), new PointCh(2532697, 1152351), 20, Functions.constant(10))));//todo pk pas juste null ?
            }
            shouldHideRoute = false;
            routeList.add(temproute);
        }
        return new MultiRoute(routeList);
    }

    public boolean shouldHideRoute(){
        return shouldHideRoute;
    }

    private ElevationProfile computeElevationProfile(){
        return this.route.get() != null ? ElevationProfileComputer.elevationProfile(this.route.get(), MAX_STEP_LENGTH) : null;
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
        return this.route.get(); //todo : pas un getter intrusif ? car pas readonly, transtypage à faire ? Multiroute est immuable
    }

    public ObservableList<Waypoint> waypoints(){
        return this.waypoints;
    }

    public DoubleProperty highlightedPositionProperty(){
        return highlightedPosition;
    }

    public void setHighlightedPosition(double highlightedPosition) {
        this.highlightedPosition.set(highlightedPosition);
    }

    public double highlightedPosition() {
        return highlightedPosition.get();
    }

    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).closestNodeId();
            int n2 = waypoints.get(i + 1).closestNodeId(); //todo il appelle cette variable "nodeId" dans la p11.. Modifier ?
            if (n1 == n2) index += 1;
        }
        return index;
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