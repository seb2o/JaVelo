package ch.epfl.javelo.routing;



import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleRouteTest {

    @Test
    public void pointClosestToTest() {


        List<Edge> edges = new ArrayList<>();
        PointCh minPoint = new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N);
        PointCh nextPoint1 = new PointCh(SwissBounds.MIN_E+20d,SwissBounds.MIN_N);
        PointCh nextPoint2 = new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N+20);
        PointCh nextPoint3 = new PointCh(SwissBounds.MIN_E+20,SwissBounds.MIN_N+20);
        double rac2 = Math.sqrt(2);

        edges.add(
                new Edge(
                        0,
                        1,
                        minPoint,
                        nextPoint1,
                        minPoint.distanceTo(nextPoint1),
                        Functions.constant(20)
                )
        );

        edges.add(
                new Edge(
                        1,
                        2,
                        nextPoint1,
                        nextPoint2,
                        nextPoint1.distanceTo(nextPoint2),
                        Functions.constant(20)
                )
        );

        SingleRoute route = new SingleRoute(edges);

        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E+10,SwissBounds.MIN_N+10),20+10*rac2,10*rac2),route.pointClosestTo(nextPoint3));

    }
}
