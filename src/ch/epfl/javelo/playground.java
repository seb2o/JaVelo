package ch.epfl.javelo;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class playground {


    static Random r = new Random();

    public static void main(String[] args) throws IOException {
        List<Route> routes = singleRouteListCreator(5,100);
        Route mr = new MultiRoute(routes);
        KmlPrinter.write("bigRouteTest",mr);
    }

    static List<Edge> edgesCreator(int numberOfEdge) {

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

            nextPointEastOffset += Math.pow(-1,i)*r.nextDouble(offsetBound);
            nextPointNorthOffset +=Math.pow(-1,i)*r.nextDouble(offsetBound);

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

    static List<Route> singleRouteListCreator(int numberOfRoutes, int routeMaxNumberOfEdges) {
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

}
