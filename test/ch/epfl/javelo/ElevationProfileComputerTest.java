package ch.epfl.javelo;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class ElevationProfileComputerTest {

    @Test
    public void elevationProfileTest() {
        DoubleUnaryOperator profile = null;
        List<Edge> edges = new ArrayList<Edge>(){};
        edges.add(new Edge(0,0,new PointCh(0,0),new PointCh(0,0),0, null));
        SingleRoute route = new SingleRoute(edges);
    }


}
