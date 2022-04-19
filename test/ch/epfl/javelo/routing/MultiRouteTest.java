package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;



import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MultiRouteTest {


    record MultiRouteWithHisRoutes(MultiRoute multiRoute, List<Route> singleRoutes){}

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
     * retourne une multiroute composée simplement d'une liste de single multiRoute
     * @param routes
     * @return
     */
    MultiRoute multiRouteCreator0(List<Route> routes) {
        return new MultiRoute(routes);
    }

    /**
     *  cree une multiroute contenant une multiroute contenant une liste de single multiRoute
     * @param routes
     * @return
     */
    MultiRoute multiRouteCreator1(List<Route> routes) {
        return new MultiRoute(Collections.singletonList(new MultiRoute(routes)));
    }

    /**
     * cree une multiroute contenant deux multiroute contenant des single multiRoute
     * @param routes
     * @return
     */
    MultiRoute multiRouteCreator2(List<Route> routes) {
        List<Route> routesStepList = new ArrayList<>();
        routesStepList.add(new MultiRoute(routes.subList(0, routes.size() / 2)));
        routesStepList.add(new MultiRoute(routes.subList(routes.size() / 2, routes.size())));
        return new MultiRoute(routesStepList);
    }

    /**
     * cree une multiroute commencznt et finissant par une single multiRoute et contenant une multiroute composéee d'une
     *  multi multiRoute composée de singles multiRoute au milieu.
     * @param routes
     * @return
     */
    MultiRoute multiRouteCreator3(List<Route> routes) {
        List<Route> routesStepList = new ArrayList<>();
        routesStepList.add(routes.get(0));
        routesStepList.add(new MultiRoute(Collections.singletonList(new MultiRoute(routes.subList(1, routes.size() - 1)))));
        routesStepList.add(routes.get(routes.size()-1));
        return new MultiRoute(routesStepList);
    }

    /**
     * creer une multiroute avec une multiroute en debut et fin et des single multiRoute au milieu
     * @param routes
     * @return
     */
    MultiRoute multiRouteCreator4(List<Route> routes) {
        List<Route> routesStepList = new ArrayList<>();
        routesStepList.add(new MultiRoute(routes.subList(0,2)));
        routesStepList.addAll(routes.subList(2, routes.size() - 2));
        routesStepList.add(new MultiRoute(routes.subList(routes.size()-2, routes.size())));
        return new MultiRoute(routesStepList);
    }


    List<MultiRouteWithHisRoutes> multiRoutesCasesGenerator() {
        List<MultiRouteWithHisRoutes> routes = new ArrayList<>();
        List<Route>[] sr = new List[5];
        for (int i = 0; i < 5; i++) {
            sr[i] = singleRouteListCreator(r.nextInt(4,10),r.nextInt(5,20));
        }
        routes.add(new MultiRouteWithHisRoutes(multiRouteCreator0(sr[0]),sr[0]));
        routes.add(new MultiRouteWithHisRoutes(multiRouteCreator1(sr[1]),sr[1]));
        routes.add(new MultiRouteWithHisRoutes(multiRouteCreator2(sr[2]),sr[2]));
        routes.add(new MultiRouteWithHisRoutes(multiRouteCreator3(sr[3]),sr[3]));
        routes.add(new MultiRouteWithHisRoutes(multiRouteCreator4(sr[4]),sr[4]));

        return routes;

    }





    @Test
    void singleRouteConstructorThrowsOnEmptyEdgeList() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SingleRoute(List.of());
        });
    }


    @Test // retourne l'index du segment de l'itinéraire contenant la position donnée
    void indexOfSegmentAtTest() {
        List<MultiRouteWithHisRoutes> multiRoutesWithRoutes = multiRoutesCasesGenerator();
        MultiRoute currentMultiRoute;
        List<Route> currentRoutes;
        for (int i = 0; i < 5; i++) {
            System.out.printf("Index of Segment at current test : %d \n",i);
            currentMultiRoute = multiRoutesWithRoutes.get(i).multiRoute;
            currentRoutes = multiRoutesWithRoutes.get(i).singleRoutes;
            assertEquals(0,currentMultiRoute.indexOfSegmentAt(0));
            assertEquals(currentRoutes.size()==1 ? 0 : 1 ,currentMultiRoute.indexOfSegmentAt(currentRoutes.get(0).length()+0.1));
        }

    }

    @Test
    void lengthTest() {

        List<MultiRouteWithHisRoutes> multiRoutesWithRoutes = multiRoutesCasesGenerator();
        MultiRoute currentMultiRoute;
        List<Route> currentRoutes;

        for (int i = 0; i < 5; i++) {
            double currentMultiRouteEdgesLenght = 0;
            currentMultiRoute = multiRoutesWithRoutes.get(i).multiRoute;
            currentRoutes = multiRoutesWithRoutes.get(i).singleRoutes;
            for (Route r : currentRoutes  ) {
                for (Edge e : r.edges()  ) {
                    currentMultiRouteEdgesLenght+=e.length();
                };
            }
            assertEquals(currentMultiRouteEdgesLenght,currentMultiRoute.length(),1e-3);

        }
    }

    @Test
    public void edgesTest() throws IOException {
        List<MultiRouteWithHisRoutes> multiRoutesWithRoutes = multiRoutesCasesGenerator();
        MultiRoute currentMultiRoute;
        List<Route> currentRoutes;
        List<Edge> edgesFromRoutes;
        for (int i = 0; i < 5; i++) {
            System.out.printf("test number %d \n",i);
            edgesFromRoutes = new ArrayList<>();
            currentMultiRoute = multiRoutesWithRoutes.get(i).multiRoute;
            currentRoutes = multiRoutesWithRoutes.get(i).singleRoutes;
            for (Route r : currentRoutes) {
                edgesFromRoutes.addAll(r.edges());

            }
            assertArrayEquals(edgesFromRoutes.toArray(),currentMultiRoute.edges().toArray());
        }
    }

    @Test//retourne sans doublons les points situées aux aretes de l'itinéraire
    public void pointsTest(){

        List<PointCh> pointsFromRoute;

        List<MultiRouteWithHisRoutes> multiRoutesWithRoutes = multiRoutesCasesGenerator();
        MultiRoute currentMultiRoute;
        List<Route> currentRoutes;
        for (int i = 0; i < 5; i++) {
            currentMultiRoute = multiRoutesWithRoutes.get(i).multiRoute;
            currentRoutes = multiRoutesWithRoutes.get(i).singleRoutes;

            pointsFromRoute = new ArrayList<>();
            for (Route r : currentRoutes) {
                pointsFromRoute.addAll(r.points().subList(0,r.points().size()-1));
            }
            pointsFromRoute.add(currentRoutes.get(currentRoutes.size() - 1).points().get(currentRoutes.get(currentRoutes.size() - 1).points().size() - 1));
            assertArrayEquals(pointsFromRoute.toArray(),currentMultiRoute.points().toArray());
        }

    }

    @Test//retourne le point se trouvant à la position donnée le long de l'itinéraire
    public void pointAtTest(){

        List<MultiRouteWithHisRoutes> multiRoutesWithRoutes = multiRoutesCasesGenerator();
        MultiRoute currentMultiRoute;
        List<Route> currentRoutes;
        for (int i = 0; i < 5; i++) {
            currentMultiRoute = multiRoutesWithRoutes.get(i).multiRoute;
            currentRoutes = multiRoutesWithRoutes.get(i).singleRoutes;

            assertEquals(currentRoutes.get(0).pointAt(0),currentMultiRoute.pointAt(0));
            assertEquals(currentRoutes.get(currentRoutes.size()-1).pointAt(Double.MAX_VALUE),currentMultiRoute.pointAt(currentMultiRoute.length()));
            assertEquals(currentRoutes.get(1).pointAt(currentRoutes.get(1).length()+Math.nextDown(0.0)),currentMultiRoute.pointAt(currentRoutes.get(0).length()+currentRoutes.get(1).length()+Math.nextDown(0.0)));
        }

    }

    @Test @Disabled
// retourne l'altitude à la position donnée le long de l'itinéraire,
    // qui peut valoir NaN si l'arête contenant cette position n'a pas de profil
    public void elevationAtTest(){
        List<MultiRouteWithHisRoutes> multiRoutesWithRoutes = multiRoutesCasesGenerator();
        MultiRoute currentMultiRoute;
        List<Route> currentRoutes;
        for (int i = 0; i < 5; i++) {
            currentMultiRoute = multiRoutesWithRoutes.get(i).multiRoute;
            currentRoutes = multiRoutesWithRoutes.get(i).singleRoutes;

            assertEquals(currentRoutes.get(0).elevationAt(0),currentMultiRoute.elevationAt(0));
            assertEquals(currentRoutes.get(currentRoutes.size()-1).elevationAt(Double.MAX_VALUE),currentMultiRoute.elevationAt(currentMultiRoute.length()),1e-3);
            System.out.printf("elevation at the beginning of the second route : %f \n",currentRoutes.get(0).length()+currentRoutes.get(1).length());
            assertEquals(currentRoutes.get(1).elevationAt(currentRoutes.get(1).length()),currentMultiRoute.elevationAt(currentRoutes.get(0).length()+currentRoutes.get(1).length()),1e-3);
        }

    }

    @Test//retourne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
    public void nodeClosestToTest(){

    }

    @Test//retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné
    public void pointClosestToTest(){
        List<MultiRouteWithHisRoutes> multiRoutesWithRoutes = multiRoutesCasesGenerator();
        MultiRoute currentMultiRoute;
        List<Route> currentRoutes;
        for (int i = 0; i < 5; i++) {
            currentMultiRoute = multiRoutesWithRoutes.get(i).multiRoute;
            currentRoutes = multiRoutesWithRoutes.get(i).singleRoutes;


        }


    }


}
