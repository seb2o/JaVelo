package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.Edge;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {
    double DELTA = 1e-7;
    Edge test1 = getEdge1();
    Edge test2 = getEdge2();
    Edge test3 = getEdge3();


    @Test
    void of() {
        Graph graph = graph();

        //Edge test = new Edge(,,,,,graph.edgeProfile(5));

        PointCh a = new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N), b = new PointCh(SwissBounds.MIN_E+5,SwissBounds.MIN_N+5);
        Edge expected = new Edge(1,2,a,b,5*Math.sqrt(2),Functions.constant(5));
        assertEquals(expected, Edge.of(graph, 5, 5, 6));
    }


    @Test
    void positionClosestTo() {
        PointCh a = new PointCh(SwissBounds.MIN_E+3d, SwissBounds.MIN_N+1d);
        assertEquals(3.130495168, test2.positionClosestTo(a), DELTA);
        a = new PointCh(SwissBounds.MIN_E+3,SwissBounds.MIN_N+4);
        assertEquals(Math.sqrt(5)*2, test2.positionClosestTo(a), DELTA);
    }

    @Test
    void pointAt() {
        PointCh a = new PointCh(SwissBounds.MIN_E+1.5, SwissBounds.MIN_N+1d);
        double c = Math.sqrt(13)/2d;
        assertEquals(a,test3.pointAt(c));

        a = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N+1.5);
        c = Math.sqrt(45)/2d;
        assertEquals(a, test2.pointAt(c));
    }

    @Test
    void elevationAt() {
        assertEquals(315.25, test1.elevationAt(1.5));
        assertEquals(315.25, test1.elevationAt(5));

        assertEquals(5, test2.elevationAt(3), DELTA);
        assertEquals(7, test2.elevationAt(6), DELTA);
        assertEquals(6, test2.elevationAt(4.5), DELTA);
        assertEquals(6+1d/3d, test2.elevationAt(5), DELTA);

    }

    Edge getEdge1() {
        PointCh a = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh b = new PointCh(SwissBounds.MIN_E+6, SwissBounds.MIN_N+3);
        double norm = Math2.norm(6,3);
        Functions.constant(315.25);

        return new Edge(1, 2, a, b, norm,Functions.constant(315.25));
    }

    Edge getEdge2() {
        PointCh a = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh b = new PointCh(SwissBounds.MIN_E+6, SwissBounds.MIN_N+3);
        double norm = Math2.norm(6,3);
        float[] sample = new float[]{
                1f, 5f, 7f
        };

        return new Edge(1, 2, a, b, norm,Functions.sampled(sample, 6));
    }

    Edge getEdge3() {
        PointCh a = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh b = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N+2);
        double norm = Math2.norm(3,2);

        return new Edge(1, 2, a, b, norm,Functions.constant(315.25));
    }

    Graph graph() {
        Graph graph = null;
        Path basePath = Path.of("lausanne");
        Path osmIdPath = basePath.resolve("nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(osmIdPath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();


            graph = Graph.loadFrom(Path.of("lausanne"));
            PointCh point = graph.nodePoint(2022);
        } catch (IOException e) {
            System.out.println("io exception");
        }
        return graph;
    }
}