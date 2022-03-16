package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static ch.epfl.javelo.data.Graph.loadFrom;

public class GraphTest {

    @Test
    public void loadFromTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        System.out.println(graph.edgeProfile(0));

    }

}
