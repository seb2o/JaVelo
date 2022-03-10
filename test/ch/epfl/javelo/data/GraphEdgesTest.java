package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class GraphEdgesTest {

    ByteBuffer edges = ByteBuffer.allocate(40);
    IntBuffer profiles = IntBuffer.wrap(new int[4]);
    ShortBuffer elevations = ShortBuffer.wrap(new short[6]);
    GraphEdges ge;

    @Test
    public void graphEdgeInitializer() {

        //premeire arête d'élévation inexistante et de longueure 0
        edges.putInt(1<<31);
        edges.putShort((short) 0);
        edges.putShort((short) 0);
        edges.putShort((short) 0);

        //deuxieme arete d'elevation 1/16 et de longeure 1, deux echantillons
        edges.putInt(1);
        edges.putShort((short) 1);
        edges.putShort((short) 1);
        edges.putShort((short) 1);

        //troisieme arrete,sens inverse, elevztion 1+2/16, longeure 2
        edges.putInt((1<<31)|2);
        edges.putShort((short) 2);
        edges.putShort((short) 2);
        edges.putShort((short) 2);

        //quatrième arête, élévation 0, longueure 3.5
        edges.putInt(3);
        edges.putShort((short)(3*16+8));
        edges.putShort((short) 0);
        edges.putShort((short) 3);


        //pas de profil
        profiles.put(0);
        //profil non compressé
        profiles.put((1<<30));
        //profil compressé q4.4
        profiles.put((2<<30)|2);
        //profil compressé q0.4
        profiles.put((3<<30)|4);

        //deuxieme arrete, 2 echantillons, non compressé , alt 0 -> 0+1/16
        elevations.put((short)0);
        elevations.put((short)1);

        //troisième arête, 2 échantillons, compressé en q4.4 , alt 169 -> 169 + 2/16
        elevations.put((short)(169*16));
        elevations.put((short)(2<<8));

        //quatrième arête, 3 échantillons, compressé en q0.4, alt 12,25 -> 12.25+7/16 -> 12.25+7/16-8/16
        elevations.put((short)(12*16+4));
        elevations.put((short)((7<<12)|(8<<8)));

        ge = new GraphEdges(edges, profiles, elevations);

    }

    @Test
    public void isInvertedTest() {
        graphEdgeInitializer();
        var actual = ge.isInverted(0);
        assertTrue(actual);
    }

    @Test
    public void isNotInvertedTest() {
        graphEdgeInitializer();
        var actual = ge.isInverted(1);
        assertFalse(actual);
    }


    @Test
    public void targetNodeTestInverted() {
        graphEdgeInitializer();
        var actual = ge.targetNodeId(0);
        assertEquals(Math.pow(2,31)-1,actual);

    }

    @Test
    public void targetNodeTestNotInverted() {
        graphEdgeInitializer();
        var actual = ge.targetNodeId(1);
        assertEquals(1,actual);
    }

    @Test
    public void lengthTest1() {
        graphEdgeInitializer();
        var actual = ge.length(3);
        assertEquals(3.5,actual);
    }
    @Test
    public void lengthTest2() {
        graphEdgeInitializer();
        var actual = ge.length(0);
        assertEquals(0,actual);
    }

    @Test
    public void elevationGainTest1() {
        graphEdgeInitializer();
        var actual = ge.elevationGain(0);
        assertEquals(0, actual);
    }
    @Test
    public void elevationGainTest2() {
        graphEdgeInitializer();
        var actual = ge.elevationGain(1);
        assertEquals((double)1/(double)16, actual);
    }

    @Test
    public void hasProfileTest1() {
        graphEdgeInitializer();
        var actual = ge.hasProfile(0);
        assertFalse(actual);
    }
    @Test
    public void hasProfileTest2() {
        graphEdgeInitializer();
        var actual = ge.hasProfile(1);
        assertTrue(actual);
    }

    @Test
    public void profileSampleTest1() {
        graphEdgeInitializer();
        var actual = ge.profileSamples(0);
        assertArrayEquals(new float[]{}, actual);
    }
    @Test
    public void profileSampleTest2() {
        graphEdgeInitializer();
        var actual = ge.profileSamples(3);
        float[] expected = new float[]{12.25f,12.6875f,12.6875f-0.5f};
        assertArrayEquals(expected, actual);
    }
    @Test
    public void profileSampleTest3() {
        graphEdgeInitializer();
        var actual = ge.profileSamples(2);
        float[] expected = new float[]{169.1250f,169.f};
        assertArrayEquals(expected, actual);
    }


    @Test
    public void attributesIndexTest() {
        graphEdgeInitializer();
        var actual = ge.attributesIndex(3);
        assertEquals(3,actual);
    }

}
