package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;


/**
 * Classe représentant un bean JavaFX regroupant les propriétés relatives aux points de passage et à l'itinéraire correspondant.
 */
public final class RouteBean {
    private final double MAX_STEP_LENGTH = 5d;
    private final ObservableList<Waypoint> waypoints;
    private final SimpleObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final SimpleObjectProperty<ElevationProfile> elevationProfile;
    private final RouteComputer routeComputer;
    private final LinkedHashMap<NodePair, Route> cache = new LinkedHashMap<>(100, 0.75f, true);
    private boolean shouldHideRoute;

    /**
     * Constructeur de RouteBean.
     * @param routeComputer un calculateur d'itinéraire
     */
    public RouteBean(RouteComputer routeComputer){
        this.shouldHideRoute = false;
        this.routeComputer = routeComputer;
        this.highlightedPosition = new SimpleDoubleProperty(Double.NaN);
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();

        this.waypoints = FXCollections.observableArrayList();

        this.route.set(null);
        this.elevationProfile.set(null);

        //Listener gérant le calcul de la route et du profile,
        // en fonction du changement sur les waypoints.
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
                    this.shouldHideRoute = true;
                    this.route.set(null);
                    this.elevationProfile.set(null);
                }

            }
        });

    }

    /**
     * Combine les routes entre chaque route calculée par computeRouteBetween pour créer la route totale.
     * @return l'itinéraire complet.
     */
    private Route computeMultiRoute(){
        List<Route> routeList = new ArrayList<>();
        for(int index = 0; index < waypoints.size() - 1; index++){
            if(waypoints().get(index).nodeId() == waypoints().get(index+1).nodeId()){
                continue;
            }
            Route temproute = computeRouteBetween(waypoints().get(index), waypoints().get(index+1));
            if(temproute == null){
                shouldHideRoute = true;
                return null;
            }
            routeList.add(temproute);
        }
        shouldHideRoute = false;
        if(routeList.isEmpty()){
            shouldHideRoute = true;
            return null;
        }
        return new MultiRoute(routeList);
    }

    /**
     * Méthode permettant a routeManager de savoir s'il doit cacher la route ou non.
     * @return un booléen qui est vrai si la route doit être cachée?
     */
    public boolean shouldHideRoute(){
        return shouldHideRoute;
    }

    /**
     * @return le profile associé à la route actuelle.
     */
    private ElevationProfile computeElevationProfile(){
        return this.route.get() != null ? ElevationProfileComputer.elevationProfile(this.route.get(), MAX_STEP_LENGTH) : null;
    }

    /**
     * Calcule une route entre deux waypoints.
     * @param w1 le waypoint de départ.
     * @param w2 le waypoint d'arrivé.
     * @return la route entre ces deux waypoints.
     */
    private Route computeRouteBetween(Waypoint w1, Waypoint w2){
        int node1 = w1.nodeId();
        int node2 = w2.nodeId();
        NodePair np = new NodePair(node1,node2);
        Route route = cache.get(np);
        if(route != null){
            return route;
        }
        route = routeComputer.bestRouteBetween(node1,node2);
        cache.put(np, route);
        return route;
    }

    /**
     * Donne accès à la propriété en lecture seule contenant la route.
     * @return la propriété de la route associée au RouteBean.
     */
    public ReadOnlyObjectProperty<Route> routeProperty(){
        return route;
    }

    /**
     * @return la route associée au RouteBean.
     */
    public Route route(){
        return this.route.get();
    }

    /**
     * @return la liste observable des waypoints composant la route.
     */
    public ObservableList<Waypoint> waypoints(){
        return this.waypoints;
    }

    /**
     * @return la propriété contenant la position du point en surbrillance sur la route.
     */
    public DoubleProperty highlightedPositionProperty(){
        return highlightedPosition;
    }

    /**
     * Définit la position de la position en surbrillance sur la route.
     * @param highlightedPosition la position en mètres depuis le point de départ.
     */
    public void setHighlightedPosition(double highlightedPosition) {
        this.highlightedPosition.set(highlightedPosition);
    }

    /**
     * @return la position du point en surbrillance sur l'itinéraire en mètres depuis le point de départ.
     */
    public double highlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * Méthode fournie dans la partie 11, permettant de donner l'index du segment qui se trouve a la position donnée
     * du point de départ, en tenant compte d'eventuels segments vides sur le chemin.
     * @param position la position sur le chemin.
     * @return l'index du segment sur la route, où se situe la position donnée.
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeId();
            int n2 = waypoints.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    /**
     * Record privé qui sert à gérer le cache.
     */
    private record NodePair(int node1, int node2) {
        /**
         * Constructeur
         *
         * @param node1 le premier node.
         * @param node2 le deuxième node.
         */
        private NodePair {
        }


        /**
         * Redéfinition de la méthode equals, afin qu'elle soit utile dans le cache.
         *
         * @return true si les nodes des deux NodePairs sont égaux deux à deux.
         */
        @Override
        public boolean equals(Object object) {
            if (object instanceof NodePair) {
                int oNode1 = ((NodePair) object).node1;
                int oNode2 = ((NodePair) object).node2;
                return (oNode1 == node1 && oNode2 == node2);
            }
            return false;
        }

        /**
         * Redéfinition de hashCode afin de respecter "x.equals(y) => x.hashCode() == y.hashCode()";
         *
         * @return le hashCode;
         */
        @Override
        public int hashCode() {
            return Objects.hash(node1, node2);
        }
    }
}