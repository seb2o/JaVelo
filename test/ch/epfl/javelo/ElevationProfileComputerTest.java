package ch.epfl.javelo;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class ElevationProfileComputerTest {

    @Test
    public void elevationProfileTest() {
        DoubleUnaryOperator profile = Functions.sampled(new float[]{1,2,3,4,20,6,8,9,10},10);
        System.out.println("manual compuation : "+profile.applyAsDouble(4.5));
        List<Edge> edges = new ArrayList<Edge>(){};
        edges.add(new Edge(0,0,new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MIN_E+50,SwissBounds.MIN_N+50),10, profile));
        SingleRoute route = new SingleRoute(edges);
        System.out.println("actual computation : "+route.elevationAt(4.5));
    }


}
