package ch.epfl.javelo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class playground {
    public static void main(String[] args) throws IOException {
        Path basePath = Path.of("lausanne");
        Path edgesPath = basePath.resolve("edges.bin");
        Path profile_idsPath = basePath.resolve("profile_ids.bin");
        Path elevationsPath = basePath.resolve("elevations.bin");
        Path attributesPath = basePath.resolve("attributes.bin");
        Path nodesPath = basePath.resolve("nodes.bin");
        Path sectorsPath = basePath.resolve("sectors.bin");
        Path nodes_osmidPath = basePath.resolve("nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        ByteBuffer edgesdBuffer;
        IntBuffer profilesBuffer;
        ShortBuffer elevationsBuffer;
        LongBuffer attributesBuffer;
        IntBuffer nodesBuffer;
        ByteBuffer sectorsBuffer;





    }






}
