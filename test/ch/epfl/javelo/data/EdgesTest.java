package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdgesTest {

    @Test
    public void positionClosestTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var edge = Edge.of(graph,0,0,1);
        assertEquals(edge.length(),edge.positionClosestTo(new PointCh(2549212.9375,1166183.5625)),1e-1);
    }
    @Test
    public void pointAtTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        var edge = Edge.of(graph,0,0,1);
        assertEquals(new PointCh(2549212.9375,1166183.5625),edge.pointAt(edge.length()));
    }

}
