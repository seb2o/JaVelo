package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static javax.swing.UIManager.get;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class GraphTest {

    @Test
    public void nodeCountTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        assertEquals(212_179,graph.nodeCount());
    }

    @Test
    public void nodePointTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var expectedLon = 6.7761194;
        var expectedLat =  46.6455770;
        var actualLat = Math.toDegrees(graph.nodePoint(0).lat());
        var actualLon = Math.toDegrees(graph.nodePoint(0).lon());
        System.out.println(actualLat);
        System.out.println(actualLon);
        assertEquals(expectedLat,actualLat,1e-5);
        assertEquals(expectedLon,actualLon, 1e-5);
    }


    @Test
    public void nodeOutDegreeTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var actual = graph.nodeOutDegree(2);

        Path filePath = Path.of("lausanne/nodes.bin");
        IntBuffer nodesBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            nodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }
        var expected = Bits.extractUnsigned(nodesBuffer.get(5), 28, 4);
        assertEquals(expected,actual);
    }

    @Test
    public void nodeOutEdgeIdTest1() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var actual = graph.nodeOutEdgeId(0,0);
        Path filePath = Path.of("lausanne/nodes.bin");
        IntBuffer nodesBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            nodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }
        var expected = Bits.extractUnsigned(nodesBuffer.get(2), 0, 28);
        assertEquals(expected,actual);
    }

    @Test
    public void nodeOutEdgeIdTest2() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var actual = graph.nodeOutEdgeId(1,1);
        Path filePath = Path.of("lausanne/nodes.bin");
        IntBuffer nodesBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            nodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }
        var expected = Bits.extractUnsigned(nodesBuffer.get(5), 0, 28)+1;
        assertEquals(expected,actual);
    }

    @Test
    public void nodeCLosestToTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var actual = graph.nodeClosestTo(new PointCh(2_520_138.0,1_164_786.0),1);
        assertEquals(-1,actual);
    }
    @Test
    public void nodeCLosestToTest1() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var actual = graph.nodeClosestTo(new PointCh(2_520_138.0,1_164_786.0),SwissBounds.MAX_E-SwissBounds.MIN_E);
        assertEquals(211939,actual);
    }

    @Test
    public void edgeTargetNodeIdTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var actual = graph.edgeTargetNodeId(0);
        System.out.println(actual);
    }
    @Test
    public void isInverted() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var actual = graph.edgeTargetNodeId(0);
        System.out.println(actual);
    }

}
