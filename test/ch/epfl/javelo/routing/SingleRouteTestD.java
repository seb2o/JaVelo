package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;

public class SingleRouteTestD {

    @Test
            void yolo() {
        float[] floatArray = {0.0f, 2.0f, 4.0f, 6.0f, 8.0f, 10.0f};
        float[] floatArray2 = {10.0f, Float.NaN, 4.0f, 6.0f, 8.0f, 9.0f};
        float[] floatArray3 = {0.0f, 3.0f, 0.0f, 3.0f, 0.0f, 3.0f, 0.0f, 3.0f};
        ElevationProfile elevationProfile1 = new ElevationProfile(10, floatArray);
        ElevationProfile elevationProfile2 = new ElevationProfile(5, floatArray2);
        ElevationProfile elevationProfile3 = new ElevationProfile(8, floatArray3);
        Edge edge1 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10.0, Functions.sampled(floatArray, 10d));
        Edge edge2 = new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N), 5, Functions.sampled(floatArray2, 5));
        ArrayList<Edge> edges = new ArrayList<Edge>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
    }

    @Test
    void elevationAt() {
        float[] floatArray = {0f, 2f, Float.NaN, Float.NaN, 8f, 10f};
        ElevationProfile elevationProfile1 = new ElevationProfile(10, floatArray);
        Edge edge1 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10.0, Functions.sampled(floatArray, 10d));
        ArrayList<Edge> edges = new ArrayList<Edge>();
        edges.add(edge1);
        SingleRoute route = new SingleRoute(edges);
        System.out.println(route.elevationAt(2.0));
    }

    @Test
    void elevationAt2() {
        float[] floatArray = {0f, 1f, 2f, Float.NaN, 4f, 5f};
        ElevationProfile elevationProfile1 = new ElevationProfile(5d, floatArray);
        Edge edge1 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N), 5d, Functions.sampled(floatArray, 5));
        ArrayList<Edge> edges = new ArrayList<Edge>();
        edges.add(edge1);
        SingleRoute route = new SingleRoute(edges);
        System.out.println(route.elevationAt(2.0));
    }
}
