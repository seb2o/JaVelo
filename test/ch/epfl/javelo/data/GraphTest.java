package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.projection.PointCh;
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
        assertEquals(expectedLat,actualLat,1e-5);
        assertEquals(expectedLon,actualLon, 1e-5);
    }


    @Test
    public void nodeOutDegreeTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var actual = graph.nodeOutDegree(0);

        Path filePath = Path.of("lausanne/nodes.bin");
        IntBuffer nodesBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            nodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }
        System.out.println(Bits.extractUnsigned(nodesBuffer.get(2), 28, 4));
    }

}
