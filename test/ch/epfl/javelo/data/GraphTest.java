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

import static ch.epfl.randomizer.TestRandomizer.newRandom;
import static javax.swing.UIManager.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GraphTest {

    @Test
    public void nodeCountTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        assertEquals(212_679,graph.nodeCount());
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
        assertEquals(1,actual);
    }
    @Test
    public void isInvertedTest2() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var actual = graph.edgeIsInverted(1);
        assertTrue(actual);
    }

    @Test
    public void edgeAttributeTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        assertEquals(new AttributeSet((1L << 17 ) | (1L << 1 )),graph.edgeAttributes(0));

    }

    @Test
    public void edgeLenghtTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var actual = graph.edgeLength(0);
        assertEquals(95.125,actual,1e-6);
    }

    @Test
    public void elevationTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var actual = graph.edgeElevationGain(0);
        var expected = 2.75;
        assertEquals(expected,actual);
    }

    @Test
    public void edgeProfileTest0() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        assertEquals(Double.NaN,graph.edgeProfile(3558).applyAsDouble(newRandom().nextDouble()));
    }
    @Test
    public void edgeProfileTest1() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        System.out.println(graph.edgeProfile(65).applyAsDouble(1));
    }
    @Test
    public void edgeProfileTest2() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        assertEquals(576.625,graph.edgeProfile(50).applyAsDouble(0));
        //todo ici décalage de 1 metre par rapport a la hauteur attendue que j'ai calulée :
        //todo  edge id = 50, l'id du premier elevation est stocké a la ligne index 12 décalé de 2 ints (8 char hexadecimaux/int)
        //todo valeure obtenue 93 base 16 = 147 base 10.
        //todo 147 valeure stockée dans elevations.bin ligne 144/8 + 3 shorts ( 4 char hex/short) = 240A = 9226/16 met
    }
    @Test
    public void edgeProfileTest3() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        graph.edgeProfile(1);
    }
}
