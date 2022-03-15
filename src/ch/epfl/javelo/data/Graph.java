package ch.epfl.javelo.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Sébastien Boo (345870)
 * @author Edgar Gonzales (328095)
 */
public final class Graph {

    /**
     *  construit le graphe JaVelo obtenu à partir des fichiers du répertoire spécifié par basePath
     * @param basePath le chemin du répertoire des fichiers
     */
    public static void loadFrom(Path basePath) throws IOException {

    }

    private GraphNodes nodes;
    private GraphSectors sectors;
    private GraphEdges edges;
    private List<AttributeSet> attributes;

    /**
     * construit un Graph, utilisé de facon interne par la méthode {@link ch.epfl.javelo.data.Graph#loadFrom(Path) loadFrom}
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributes) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributes = attributes;
    }
}
