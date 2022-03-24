package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ElevationProfileComputerTest {

    @Test
    public void elevationProfileTestOneEdge() {
        int nSamples = newRandom().nextInt(5);
        double edgeLength = newRandom().nextDouble();
        double position = newRandom().nextDouble();

        float[] samples = new float[nSamples];
        for (int i = 0; i < nSamples ; i++) {
            samples[i] = newRandom().nextFloat();
        }
        DoubleUnaryOperator profile = Functions.sampled(samples, edgeLength);
        var expected =  profile.applyAsDouble(position);
        List<Edge> edges = new ArrayList<Edge>(){};
        edges.add(new Edge(0,0,new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MIN_E+50,SwissBounds.MIN_N+50),10, profile));
        SingleRoute route = new SingleRoute(edges);
        var actual = route.elevationAt(position);
        assertEquals(expected,actual);

    }

    @Test
    public void elevationProfileTestSeveralEdge() {

        List<Edge> edges = new ArrayList<Edge>(){};

        int numberOfEdges = newRandom().nextInt(0,5);


        int nSamples = newRandom().nextInt(0,5);
        double edgeLength = newRandom().nextDouble();
        double position = newRandom().nextDouble();

        float[] samples = new float[nSamples];
        for (int i = 0; i < nSamples ; i++) {
            samples[i] = newRandom().nextFloat();
            System.out.print(samples[i]+"; ");
        }
        DoubleUnaryOperator profile = Functions.sampled(samples, edgeLength);
        var expected =  profile.applyAsDouble(position);
        edges.add(new Edge(0,0,new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MIN_E+50,SwissBounds.MIN_N+50),10, profile));
        SingleRoute route = new SingleRoute(edges);
        var actual = route.elevationAt(position);
        assertEquals(expected,actual);

    }


}
