package ch.epfl;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.RoutePoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutePointTest {
    RoutePoint test1 = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),3, 25);
    RoutePoint test2 = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),25,30);

    @Test
    void withPositionShiftedBy() {
        RoutePoint expected1 = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),8, 25);
        RoutePoint expected2 = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),35, 30);

        assertEquals(expected1,test1.withPositionShiftedBy(5));
        assertEquals(expected2,test2.withPositionShiftedBy(10));
    }

    @Test
    void withPositionShiftedByNegative() {
        RoutePoint expected1 = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),2, 25);
        RoutePoint expected2 = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),5, 30);

        assertEquals(expected1,test1.withPositionShiftedBy(-1));
        assertEquals(expected2,test2.withPositionShiftedBy(-20));
    }

    @Test
    void min() {
        assertEquals(test1, test1.min(test2));
        assertEquals(test1, test2.min(test1));

        assertEquals(test1, test1.min(new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),5,30)));
        assertEquals(test1, test2.min(new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),3, 25)));

    }

}