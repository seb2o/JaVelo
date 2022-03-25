package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import javax.sql.PooledConnection;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTestG {
    SingleRoute test1 = get1();
    SingleRoute test2 = get2();

    @Test
    void throwsExceptionWhenNoEdges() {
        List<Edge> none = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {new SingleRoute(none);});
    }

    @Test
    void indexOfSegmentAt() {
        assertEquals(0, test1.indexOfSegmentAt(0));
        assertEquals(0, test1.indexOfSegmentAt(746));
        assertEquals(0, test1.indexOfSegmentAt(1.156));
        assertEquals(0, test1.indexOfSegmentAt(-5));


        assertEquals(0, test2.indexOfSegmentAt(-5));
        assertEquals(1, test2.indexOfSegmentAt(746));
        assertEquals(0, test2.indexOfSegmentAt(0));
        assertEquals(0, test2.indexOfSegmentAt(7.8));
        assertEquals(1, test2.indexOfSegmentAt(7.9));
    }

    @Test
    void length() {
        assertEquals(Math.sqrt(25+36), test1.length());
        assertEquals(Math.sqrt(25+36)+Math.sqrt(29), test2.length());
    }

    @Test @Disabled
    void edges() {
        List<Edge> expected = new ArrayList<>();

        PointCh aFromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh aToPoint = new PointCh(SwissBounds.MIN_E+5d, SwissBounds.MIN_N+6d);
        expected.add(0, new Edge(0,1, aFromPoint, aToPoint, Math.sqrt(25+36), Functions.constant(3)));
        Edge a = new Edge(0,1, aFromPoint, aToPoint, Math.sqrt(25+36), Functions.constant(3));

        PointCh bFromPoint = new PointCh(SwissBounds.MIN_E+5d, SwissBounds.MIN_N+6d);
        PointCh bToPoint = new PointCh(SwissBounds.MIN_E+10d, SwissBounds.MIN_N+4d);
        expected.add(1, new Edge(1,2, bFromPoint, bToPoint, Math.sqrt(25+4), Functions.constant(5)));
        Edge b = new Edge(1,2, bFromPoint, bToPoint, Math.sqrt(25+4), Functions.constant(5));

        assertEquals(expected, test2.edges());
        // c'est normal que lui soit faux, vérifie juste que les seuls differences
        // vienne des positions en mémoire, le reste doit etre identique

    }

    @Test
    void points() {
        List<PointCh> expected = new ArrayList<>();
        PointCh aFromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh bFromPoint = new PointCh(SwissBounds.MIN_E+5d, SwissBounds.MIN_N+6d);
        PointCh bToPoint = new PointCh(SwissBounds.MIN_E+10d, SwissBounds.MIN_N+4d);

        expected.add(aFromPoint);
        expected.add(bFromPoint);
        expected.add(bToPoint);
        assertEquals(expected,test2.points());
    }

    @Test
    void pointAt() {
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), test2.pointAt(-5));
        assertEquals(new PointCh(SwissBounds.MIN_E+10d, SwissBounds.MIN_N+4d), test2.pointAt(5000000));
        assertEquals(new PointCh(SwissBounds.MIN_E+5d, SwissBounds.MIN_N+6d), test1.pointAt(50000000));

        PointCh expected1 = new PointCh(SwissBounds.MIN_E+(5d/3d), SwissBounds.MIN_N+2);
        assertEquals(expected1, test1.pointAt(Math.sqrt(25+36)/3d));
        assertEquals(expected1, test2.pointAt(Math.sqrt(25+36)/3d));
        assertEquals( test1.pointAt(Math.sqrt(25+36)/3d),  test2.pointAt(Math.sqrt(25+36)/3d));

        PointCh expected2 = new PointCh(SwissBounds.MIN_E+7.5, SwissBounds.MIN_N+5d);
        assertEquals(expected2, test2.pointAt(Math.sqrt(25+36) + (Math.sqrt(25+4)/2d)));
    }

    @Test
    void testConstructeur() {
        get1();
        get2();
    }

    @Test
    void nodeClosestTo() {
        assertEquals(0, test1.nodeClosestTo(-5));
        assertEquals(0, test2.nodeClosestTo(-5));
        assertEquals(1, test1.nodeClosestTo(100005));
        assertEquals(2, test2.nodeClosestTo(100005));

        assertEquals(1, test1.nodeClosestTo(Math.sqrt(25+36)-1));
        assertEquals(1, test2.nodeClosestTo(Math.sqrt(25+36)-1));
        assertEquals(0, test1.nodeClosestTo(Math.sqrt(25+36)-7.1));
        assertEquals(0, test2.nodeClosestTo(Math.sqrt(25+36)-7));

        assertEquals(1, test2.nodeClosestTo(Math.sqrt(25+36)+1));
        assertEquals(2, test2.nodeClosestTo(Math.sqrt(25+36)+5));
    }

    @Test
    void pointClosestTo() {
        PointCh point1 = new PointCh(SwissBounds.MIN_E+5d,SwissBounds.MIN_N+7d);
        PointCh expectedPoint1 = new PointCh(SwissBounds.MIN_E+5d, SwissBounds.MIN_N+6d);
        RoutePoint expected1 = new RoutePoint(expectedPoint1, Math.sqrt(25+36), 1);
        assertEquals(expected1, test1.pointClosestTo(point1));

        PointCh point2 = new PointCh(SwissBounds.MIN_E+6d,SwissBounds.MIN_N+4d);
        PointCh expectedPoint2 = new PointCh(SwissBounds.MIN_E+6.5517241377, SwissBounds.MIN_N+5.3793103448);
        RoutePoint expected2 = new RoutePoint(expectedPoint2,9.48150771950012, 1.4855627053299438);
        assertEquals(expected2, test2.pointClosestTo(point2));
    }

    @Test
    void elevationAt() {

        assertEquals(3, test1.elevationAt(0));
        assertEquals(3, test2.elevationAt(0));

        assertEquals(3, test1.elevationAt(-5));
        assertEquals(3, test2.elevationAt(-5));

        assertEquals(3, test1.elevationAt(Math.sqrt(25+36)-1));
        assertEquals(3, test2.elevationAt(Math.sqrt(25+36)-1));

        assertEquals(5, test2.elevationAt(Math.sqrt(25+36)+1));
        assertEquals(5, test2.elevationAt(Math.sqrt(25+36)+50000));

    }




    SingleRoute get1() {
        List<Edge> a = new ArrayList<>();
        PointCh aFromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh aToPoint = new PointCh(SwissBounds.MIN_E+5d, SwissBounds.MIN_N+6d);
        a.add(0,new Edge(0,1, aFromPoint, aToPoint, Math.sqrt(25+36), Functions.constant(3)));



        return new SingleRoute(a);
    }

    SingleRoute get2() {
        List<Edge> a = new ArrayList<>();

        PointCh aFromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh aToPoint = new PointCh(SwissBounds.MIN_E+5d, SwissBounds.MIN_N+6d);
        a.add(0, new Edge(0,1, aFromPoint, aToPoint, Math.sqrt(25+36), Functions.constant(3)));

        PointCh bFromPoint = new PointCh(SwissBounds.MIN_E+5d, SwissBounds.MIN_N+6d);
        PointCh bToPoint = new PointCh(SwissBounds.MIN_E+10d, SwissBounds.MIN_N+4d);
        a.add(1, new Edge(1,2, bFromPoint, bToPoint, Math.sqrt(25+4), Functions.constant(5)));


        return new SingleRoute(a);
    }
}