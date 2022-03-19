package ch.epfl;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphTestD {
    @Test
    void loadFromTestD() {
        Path basePath = Path.of("lausanne");
        Path osmIdPath = basePath.resolve("nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(osmIdPath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();

            System.out.println("#debug0");
            System.out.println(osmIdBuffer.get(2022));
            Graph graph = Graph.loadFrom(Path.of("lausanne"));
            PointCh point = graph.nodePoint(2022);
            System.out.println(Math.toDegrees(Ch1903.lon(point.e(), point.n())));
            System.out.println(Math.toDegrees(Ch1903.lat(point.e(), point.n())));
            System.out.println(graph.nodeCount());
            System.out.println(graph.nodeClosestTo(point, 100));
            System.out.println(graph.nodeOutDegree(2022));
            System.out.println(graph.nodeOutEdgeId(2022, 0));
            System.out.println(graph.edgeProfile(4095));
            //   PointCh point2 = graph.nodePoint(graph.nodeClosestTo(point,20));
            System.out.println(graph.nodePoint(2022).squaredDistanceTo(graph.nodePoint(2021)));
            System.out.println(graph.nodeClosestTo(point, 1));
        } catch (IOException e) {
            System.out.println("io exception");
        }
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

    LongBuffer osmId() {
        Graph graph = null;
        Path basePath = Path.of("lausanne");
        Path osmIdPath = basePath.resolve("nodes_osmid.bin");
        LongBuffer osmIdBuffer = null;
        try (FileChannel channel = FileChannel.open(osmIdPath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();


            graph = Graph.loadFrom(Path.of("lausanne"));
            PointCh point = graph.nodePoint(2022);
        } catch (IOException e) {
            System.out.println("io exception");
        }
        return osmIdBuffer;
    }

    @Test
    void nodeCountTestD() {
        Graph graph = graph();
        assertEquals(212679, graph().nodeCount());
    }

    @Test
    void nodePointTestD() {
        Graph graph = graph();
        PointCh point = graph.nodePoint(2022);
        assertEquals(46.632617453136255, Math.toDegrees(Ch1903.lat(point.e(), point.n())), 0.1);
        assertEquals(6.601302186406967, Math.toDegrees(Ch1903.lon(point.e(), point.n())), 0.1);
    }

    @Test
    void nodeOutDegreeTestD() {
        Graph graph = graph();
        PointCh point = graph.nodePoint(2022);
        assertEquals(2, graph.nodeOutDegree(2022));
    }


    @Test
    void nodeClosestToTestD() {
        Graph graph = graph();
        PointCh point = graph.nodePoint(2022);
        System.out.println("edge0:" + graph.nodeOutEdgeId(2022, 0));
        System.out.println("edge1:" + graph.nodeOutEdgeId(2022, 1));
        System.out.println(osmId().get(4095));
        System.out.println(osmId().get(4096));
        /*
        assertThrows(IllegalArgumentException.class, () -> {
            graph.nodeOutEdgeId(2022,3);
        });
        */
    }

    @Test
    void edgeTargetNodeIdTestD() {
        System.out.println("edgeTarget");
        System.out.println(graph().edgeTargetNodeId(2022));
    }

    @Test
    void edgeIsInvertedTestD() {
        System.out.println(graph().edgeIsInverted(2022));
    }

    @Test
    void edgeAttributesTestD() {
        System.out.println(graph().edgeAttributes(2022));
    }

    @Test
    void edgeLengthTestD() {
        System.out.println(graph().edgeLength(graph().nodeOutEdgeId(2022, 0)));
    }

    @Test
    void edgeElevationGainTestD() {
        System.out.println(graph().edgeElevationGain(graph().nodeOutEdgeId(2022, 0)));
    }

    @Test
    void edgeProfileTestD() {
        System.out.println(graph().edgeProfile(graph().nodeOutEdgeId(2022, 0)).applyAsDouble(2.5));
        System.out.println("wtf");
    }
}