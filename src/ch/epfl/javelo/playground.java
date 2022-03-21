package ch.epfl.javelo;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class playground {
    public static void main(String[] args) throws IOException {
        Path basePath = Path.of("lausanne/");
        Graph graph = Graph.loadFrom(basePath);
        Path osmNodesPath = basePath.resolve("nodes_osmid.bin");
        LongBuffer nodesOsmBuffer;
        try(FileChannel nodeOsm = FileChannel.open(osmNodesPath)) {
             nodesOsmBuffer = nodeOsm
                    .map(FileChannel.MapMode.READ_ONLY, 0, nodeOsm.size())
                    .asLongBuffer();
        }
//        PointCh pt = new PointCh(graph.nodePoint(0).e(),graph.nodePoint(0).n());
//        System.out.println(Math.toDegrees(pt.lat()));
//        System.out.println(Math.toDegrees(pt.lon()));
//        System.out.println(pt);
//        System.out.println(pt);
        System.out.println(nodesOsmBuffer.get(211939));



    }






}
