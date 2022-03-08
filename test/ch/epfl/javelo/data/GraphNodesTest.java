package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphNodesTest {

    @Test
    public void countTest() {
        GraphNodes gn = new GraphNodes(IntBuffer.wrap(new int[]{}));
        var actual = gn.count();
        var expected = 0;
        assertEquals(expected,actual);
    }

    @Test
    public void nodeETest() {
        int e = Q28_4.ofInt(533633);
        int n = Q28_4.ofInt(150893);
        int a = 0b10100000000000000000000000000001;
        GraphNodes gn = new GraphNodes(IntBuffer.wrap(new int[]{e,n,a}));
        var actualE = gn.nodeE(0);
        var actualN = gn.nodeN(0);
        var actualAn = gn.outDegree(0);
        var actualId = gn.edgeId(0,0);
        assertEquals(533633,actualE);
        assertEquals(150893,actualN);
        assertEquals(10,actualAn);
        assertEquals(1,actualId);

    }
}
