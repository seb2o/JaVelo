package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ElevationProfileComputerTest {

    @Test@Disabled
    public void elevationProfileTestSeveralEdge() throws IOException {

        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        PointCh minPoint = new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N);
        PointCh maxPoint = new PointCh(SwissBounds.MAX_E,SwissBounds.MAX_N);
        double diag = minPoint.distanceTo(maxPoint);

        List<Edge> edges = new ArrayList<>();
        edges.add(
                new Edge(
                        0,
                        1,
                        minPoint,
                        maxPoint,
                        20,
                        Functions.sampled(new float[]{1000f,3000f},20)
                )
        );
        edges.add(
                new Edge(
                        50,
                        120,
                        new PointCh(SwissBounds.MIN_E+100,SwissBounds.MIN_N+20),
                        new PointCh(SwissBounds.MIN_E+20,SwissBounds.MIN_N+100),
                        30,
                        Functions.constant(Double.NaN)
                )
        );
        edges.add(
                new Edge(
                        50,
                        120,
                        new PointCh(SwissBounds.MIN_E+200,SwissBounds.MIN_N+40),
                        new PointCh(SwissBounds.MIN_E+40,SwissBounds.MIN_N+200),
                        25,
                        Functions.sampled(new float[]{0f,0.625f,1f,20f,12f,0.12f,100f},25)
                )
        );
        SingleRoute route = new SingleRoute(edges);

        var profile = ElevationProfileComputer.elevationProfile(route,0.001);
        assertEquals(1500f, profile.elevationAt(35),1e-1);


    }


}
