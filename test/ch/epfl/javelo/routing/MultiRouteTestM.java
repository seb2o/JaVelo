package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTestM {
    private static final int ORIGIN_N = 1_200_000;
    private static final int ORIGIN_E = 2_600_000;
    private static final double EDGE_LENGTH = 100.25;

    // Sides of triangle used for "sawtooth" edges (shape: /\/\/\…)
    private static final double TOOTH_EW = 1023;
    private static final double TOOTH_NS = 64;
    private static final double TOOTH_LENGTH = 1025;
    private static final double TOOTH_ELEVATION_GAIN = 100d;
    private static final double TOOTH_SLOPE = TOOTH_ELEVATION_GAIN / TOOTH_LENGTH;
    private MultiRoute testRoute1 = getMultiRoute1();
    private MultiRoute testRoute2 = getMultiRoute2();

    @Test
    void indexOfSegmentAt() {
        for (int i = -1; i< 2*TOOTH_LENGTH; i++) {
            assertEquals(0, testRoute1.indexOfSegmentAt(i/TOOTH_LENGTH));
            assertEquals(0, testRoute2.indexOfSegmentAt(i/TOOTH_LENGTH));
        }

        for (int i = 1; i< 4*TOOTH_LENGTH; i++) {
            assertEquals(1, testRoute1.indexOfSegmentAt(2* TOOTH_LENGTH + i/TOOTH_LENGTH));
            assertEquals(1, testRoute2.indexOfSegmentAt(2* TOOTH_LENGTH + i/TOOTH_LENGTH));
        }

        for (int i = 1; i< 2*TOOTH_LENGTH; i++) {
            assertEquals(2, testRoute1.indexOfSegmentAt(6* TOOTH_LENGTH + i/TOOTH_LENGTH));
            assertEquals(2, testRoute2.indexOfSegmentAt(6* TOOTH_LENGTH + i/TOOTH_LENGTH));
        }

    }

    @Test
    void length() {
        assertEquals(testRoute1.length(), testRoute2.length());
        assertEquals(8*TOOTH_LENGTH, testRoute2.length());
    }

    @Disabled
    void edges() {
        //Test faux a cause des emplacement mémoire, sinon c'est juste.
        assertEquals(testRoute1.edges(), testRoute2.edges());
        assertEquals(sawToothEdges(8), testRoute1.edges());
    }

    @Test
    void points() {
        assertEquals(testRoute1.points(), testRoute2.points());

        List<PointCh> list = new ArrayList<>();
        list.add(0, new PointCh(ORIGIN_E, ORIGIN_N));
        list.add(1,new PointCh(ORIGIN_E+TOOTH_EW, ORIGIN_N+TOOTH_NS));
        list.add(2,new PointCh(ORIGIN_E+2*TOOTH_EW, ORIGIN_N));
        list.add(3,new PointCh(ORIGIN_E+3*TOOTH_EW, ORIGIN_N + TOOTH_NS));
        list.add(4,new PointCh(ORIGIN_E+4*TOOTH_EW, ORIGIN_N));
        list.add(5,new PointCh(ORIGIN_E+5*TOOTH_EW, ORIGIN_N + TOOTH_NS));
        list.add(6,new PointCh(ORIGIN_E+6*TOOTH_EW, ORIGIN_N));
        list.add(7,new PointCh(ORIGIN_E+7*TOOTH_EW, ORIGIN_N + TOOTH_NS));
        list.add(8,new PointCh(ORIGIN_E+8*TOOTH_EW, ORIGIN_N));

        assertEquals(list, testRoute1.points());

    }

    @Test
    void pointAt() {
        double EW_RATIO = TOOTH_EW/TOOTH_LENGTH;
        double NS_RATIO = TOOTH_NS/TOOTH_LENGTH;
        //test que routeTest1 et routeTest2 soit constant de l'un à l'autre
        for (int i = -5; i < 40*20000; i+= 5)
            assertEquals(testRoute1.pointAt(i), testRoute2.pointAt(i));
        assertEquals(testRoute1.pointAt(20000), testRoute2.pointAt(500000));
        
        for (int i = 0; i<65; i++) {
            PointCh expected = new PointCh(ORIGIN_E + i*EW_RATIO, ORIGIN_N + i* NS_RATIO);
            assertEquals(expected, testRoute1.pointAt(i));
        }

        for (int i = 0; i<65; i++) {
            PointCh expected = new PointCh(ORIGIN_E + TOOTH_EW + i*EW_RATIO, ORIGIN_N + TOOTH_NS - i* NS_RATIO);
            assertEquals(expected, testRoute1.pointAt(TOOTH_LENGTH + i));
        }

        for (int j = 0; j<8; j++) {
            for (int i = 0; i<65; i++) {
                PointCh expected = new PointCh(ORIGIN_E + j*TOOTH_EW + i*EW_RATIO,
                        (j%2 == 0 ) ?
                                ORIGIN_N + i* NS_RATIO
                                : ORIGIN_N + TOOTH_NS - i* NS_RATIO);
                assertEquals(expected, testRoute1.pointAt(j*TOOTH_LENGTH + i));
            }
        }
    }

    @Test
    void nodeClosestTo() {
        //test que routeTest1 et routeTest2 soit constant de l'un à l'autre
        for (int i = -5; i < 40*20000; i+= 5) {
            assertEquals(testRoute1.nodeClosestTo(i), testRoute2.nodeClosestTo(i));
        }
        assertEquals(testRoute1.nodeClosestTo(20000), testRoute2.nodeClosestTo(500000));

    for (int i = 1; i < 9; i++)
        assertEquals(i, testRoute1.nodeClosestTo(i*TOOTH_LENGTH-5));

    for (int i = 0; i < 9; i++)
        assertEquals(i, testRoute1.nodeClosestTo(i*TOOTH_LENGTH));

    for (int i = 1; i < 9; i++)
        assertEquals(i, testRoute1.nodeClosestTo(i*TOOTH_LENGTH-1d/2d*TOOTH_LENGTH +1));

    for (int i = 1; i < 9; i++)
        assertEquals(i-1, testRoute1.nodeClosestTo(i*TOOTH_LENGTH-1d/2d*TOOTH_LENGTH -1));

        assertEquals(0, testRoute1.nodeClosestTo( -5));
        assertEquals(8, testRoute1.nodeClosestTo(20000000));
    }

    @Test
    void pointClosestTo() {
        for (int i = 0; i<50; i++) {
            PointCh a =new PointCh(ORIGIN_E+ 15*i, ORIGIN_N+ 5*i);
            assertEquals(testRoute1.pointClosestTo(a), testRoute2.pointClosestTo(a));
        }

        PointCh pointExpected1 = new PointCh(ORIGIN_E+ TOOTH_EW, ORIGIN_N+ TOOTH_NS);
        for (int i = 1; i<5; i++) {
            RoutePoint expected1 = new RoutePoint(pointExpected1, TOOTH_LENGTH, (i-1)*TOOTH_NS);
            assertEquals(expected1, testRoute1.pointClosestTo(new PointCh(ORIGIN_E+ TOOTH_EW, ORIGIN_N+ i*TOOTH_NS)));
        }

        PointCh pointExpected2 = new PointCh(2603758.290462344, 1200020.8772340273);
        RoutePoint expected2 = new RoutePoint(pointExpected2,
                3765.6380487804877, 235.58243902449246);
        assertEquals(expected2, testRoute1.pointClosestTo(
                new PointCh(ORIGIN_E+3*TOOTH_EW+ (11)*TOOTH_NS,
                        ORIGIN_N + 4*TOOTH_NS)));
    }

    @Test
    void elevationAt() {
        //test que routeTest1 et routeTest2 soit constant de l'un à l'autre
        for (int i = -5; i < 40*20000; i+= 5)
            assertEquals(testRoute1.elevationAt(i), testRoute2.elevationAt(i));
        assertEquals(testRoute1.elevationAt(5000000), testRoute2.elevationAt(20000000));

        for (int i = 0; i < 9; i++)
            assertEquals(i*TOOTH_ELEVATION_GAIN, testRoute1.elevationAt(TOOTH_LENGTH*i));
        assertEquals(8*TOOTH_ELEVATION_GAIN, testRoute1.elevationAt(TOOTH_LENGTH*50));
    }

    MultiRoute getMultiRoute1() {
        List<Edge> edge1 = sawToothEdges(8);
        Route route1 = new SingleRoute(edge1.subList(0,2));
        Route route2 = new SingleRoute(edge1.subList(2,6));
        Route route3 = new SingleRoute(edge1.subList(6,8));
        List<Route> list = new ArrayList<>();
        list.add(0,route1);
        list.add(1,route2);
        list.add(2,route3);

        return new MultiRoute(list);
    }

    MultiRoute getMultiRoute2() {
        List<Edge> edge1 = sawToothEdges(8);
        Route route1 = new SingleRoute(edge1.subList(0,2));
        Route route2 = new SingleRoute(edge1.subList(2,6));
        Route route3 = new SingleRoute(edge1.subList(6,8));
        List<Route> list1 = new ArrayList<>();
        List<Route> list2 = new ArrayList<>();
        list1.add(0, route1);
        list2.add(0, route2);
        list2.add(1, route3);
        list1.add(new MultiRoute(list2));

        return new MultiRoute(list1);
    }

    private static List<Edge> sawToothEdges(int edgesCount) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = 0; i < edgesCount; i += 1) {
            var p1 = sawToothPoint(i);
            var p2 = sawToothPoint(i + 1);
            var startingElevation = i * TOOTH_ELEVATION_GAIN;
            edges.add(new Edge(i, i + 1, p1, p2, TOOTH_LENGTH, x -> startingElevation + x * TOOTH_SLOPE));
        }
        return Collections.unmodifiableList(edges);
    }

    private static PointCh sawToothPoint(int i) {
        return new PointCh(
                ORIGIN_E + TOOTH_EW * i,
                ORIGIN_N + ((i & 1) == 0 ? 0 : TOOTH_NS));
    }
}