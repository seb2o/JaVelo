package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultiRouteTest {


    record MultiRouteWithAttributes(
            MultiRoute route,
            int numberOfSegments,
            double totalLenght,
            double[] lenghtOfIntermediatesSegments,
            PointCh[] edgePoints,
            Edge[] edges
            ){}

     Random r = new Random();

    /**
     * create a list of edges with incomplete profiles where the elevation at position i is approximatively equal to i
     * @param numberOfEdge the number of edges you want
     * @return the list of the edge
     */
    List<Edge> edgesCreator(int numberOfEdge) {

        List<Edge> edges = new ArrayList<>();

        double previousPointEastCoord = SwissBounds.MIN_E + (SwissBounds.MAX_E - SwissBounds.MIN_E)/2;
        double previousPointNorthCoord = SwissBounds.MIN_N + (SwissBounds.MAX_N - SwissBounds.MIN_N)/2;
        double nextPointEastOffset = 1;
        double nextPointNorthOffset = 1;
        double nextPointEastCoord = previousPointEastCoord + nextPointEastOffset;
        double nextPointNorthCoord = previousPointNorthCoord + nextPointNorthOffset;
        double distanceBetweenEdgesPoints;
        double totalDistanceCovered = 0;
        int offsetBound = 10000/numberOfEdge;

        for (int i = 0; i < numberOfEdge; i++) {

            System.out.printf("east offset : %f ; north offset : %f \n", nextPointEastOffset,nextPointNorthOffset);

            previousPointEastCoord += nextPointEastOffset;
            previousPointNorthCoord += nextPointNorthOffset;

            nextPointEastOffset += Math.pow(-1,i+1)*r.nextDouble(offsetBound);
            nextPointNorthOffset +=Math.pow(-1,i+1)*r.nextDouble(offsetBound);

            nextPointEastCoord += nextPointEastOffset;
            nextPointNorthCoord += nextPointNorthOffset;
            distanceBetweenEdgesPoints = Math.sqrt(Math.pow( nextPointEastOffset,2) + Math.pow(nextPointNorthOffset,2));

            int copyOfI = i;
            double copyOfTotalDistanceCovered = totalDistanceCovered;
            double copyOfDistanceBetweenEdgesPoints = distanceBetweenEdgesPoints;
            edges.add(
                    new Edge(
                            i,
                            i+1,
                            new PointCh(previousPointEastCoord,previousPointNorthCoord),
                            new PointCh(nextPointEastCoord,nextPointNorthCoord),
                            distanceBetweenEdgesPoints,
                            x -> x%copyOfI == 0
                                    ? Double.NaN
                                    : Math2.interpolate(copyOfTotalDistanceCovered,copyOfTotalDistanceCovered+ copyOfDistanceBetweenEdgesPoints,x/copyOfDistanceBetweenEdgesPoints)
                    )
            );
            totalDistanceCovered+=distanceBetweenEdgesPoints;
        }
        return edges;
    }

    List<Route> singleRouteListCreator(int numberOfRoutes, int routeMaxNumberOfEdges) {
        List<Route> routes = new ArrayList<>();
        List<Edge> edges = edgesCreator(routeMaxNumberOfEdges*numberOfRoutes);
        int edgesUsed = 0;
        int routeNumberOfEdges;
        for (int i = 0; i < numberOfRoutes; i++) {
            routeNumberOfEdges = r.nextInt(1,routeMaxNumberOfEdges);
            routes.add(new SingleRoute(List.copyOf(edges.subList(edgesUsed,edgesUsed+routeNumberOfEdges))));
            edgesUsed+=routeNumberOfEdges;
        }
        return routes;
    }


    /**
     * retourne une multiroute composée simplement d'une liste de single route
     * @param routes
     * @return
     */
    Route multirouteCreator0(List<Route> routes) {
        return new MultiRoute(routes);
    }

    /**
     *  cree une multiroute contenant une multiroute contenant une liste de single route
     * @param routes
     * @return
     */
    Route multiRouteCreator1(List<Route> routes) {
        return new MultiRoute(Collections.singletonList(new MultiRoute(routes)));
    }

    /**
     * cree une multiroute contenant deux multiroute contenant des single route
     * @param routes
     * @return
     */
    Route multiRouteCreator2(List<Route> routes) {
        List<Route> routesStepList = new ArrayList<>();
        routesStepList.add(new MultiRoute(routes.subList(routes.size() / 2, routes.size() - 1)));
        routesStepList.add(new MultiRoute(routes.subList(routes.size() / 2, routes.size() - 1)));
        return new MultiRoute(routesStepList);
    }

    /**
     * cree une multiroute commencznt et finissant par une single route et contenant une multiroute composéee d'une
     *  multi route composée de singles route au milieu.
     * @param routes
     * @return
     */
    Route multiRouteCreator3(List<Route> routes) {
        List<Route> routesStepList = new ArrayList<>();
        routesStepList.add(routes.get(0));
        routesStepList.add(new MultiRoute(Collections.singletonList(new MultiRoute(routes.subList(1, routes.size() - 2)))));
        routesStepList.add(routes.get(routes.size()-1));
        return new MultiRoute(routesStepList);
    }

    //todo creer multiroute commencant par multi route avec single route au millieu,
    //todo commencant par mr et fnissant par sr , et vice versa
    //todo puis regrouper les 4 methodes de création de multiroute en une seule crréant 4 multiroute
    //todo a partir d'edges différentes et retournant 4 multiRoute associées avec leurs liste de routes et leurs liste d'edge
    //todo creer un record stockant multiroute, liste de ses segments et liste d'edges.


    @Test
    void singleRouteConstructorThrowsOnEmptyEdgeList() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SingleRoute(List.of());
        });
    }


    @Test// retourne l'index du segment de l'itinéraire contenant la position donnée
    void indexOfSegmentAtTest() {

    }

    @Test
    void lengthTest() {

    }

    @Test//retourne les aretes de l'itinéraire
    public void edgesTest(){

    }

    @Test//retourne sans doublons les points situées aux aretes de l'itinéraire
    public void pointsTest(){

    }

    @Test//retourne le point se trouvant à la position donnée le long de l'itinéraire
    public void pointAtTest(){

    }

    @Test// retourne l'altitude à la position donnée le long de l'itinéraire,
    // qui peut valoir NaN si l'arête contenant cette position n'a pas de profil
    public void elevationAtTest(){

    }

    @Test//retourne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
    public void nodeClosestToTest(){

    }

    @Test//retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné
    public void pointClosestToTest(){

    }


}
