package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphSectorTest {


    @Test
    public void sectorsInAreaTestFirstSector() {
        ByteBuffer b = ByteBuffer.wrap(new byte[128 * 128 * 6]);
        for (int i = 0; i < 128*128; i++) {
            b.putInt(i);
            b.putShort(((short) 1));
        }
        GraphSectors gs = new GraphSectors(b);
        var actual = gs.sectorsInArea(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 1);
        var expected = new ArrayList<GraphSectors.Sector>();
        expected.add(new GraphSectors.Sector(0, 1));
        assertEquals(expected, actual);
    }
    @Test
    public void sectorsInAreaTestLastSector() {
        ByteBuffer b = ByteBuffer.wrap(new byte[128 * 128 * 6]);
        for (int i = 0; i < 128*128; i++) {
            b.putInt(i);
            b.putShort(((short) 1));
        }
        GraphSectors gs = new GraphSectors(b);
        var actual = gs.sectorsInArea(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 0);
        var expected = new ArrayList<GraphSectors.Sector>();
        expected.add(new GraphSectors.Sector(128*128-1, 128*128));
        assertEquals(expected, actual);
    }

    @Test
    public void testSeveralSectors() {

        ByteBuffer b = ByteBuffer.wrap(new byte[128 * 128 * 6]);
        for (int i = 0; i < 128*128; i++) {
            b.putInt(i);
            b.putShort(((short) 1));
        }
        GraphSectors gs = new GraphSectors(b);

        var actual = gs.sectorsInArea(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 1740);
        var expected = new ArrayList<GraphSectors.Sector>();
        expected.add(new GraphSectors.Sector(0, 1));
        expected.add(new GraphSectors.Sector(128, 129));
        assertEquals(expected, actual);

    }

    @Test
    public void testAllSectors() {

        ByteBuffer b = ByteBuffer.wrap(new byte[128 * 128 * 6]);
        for (int i = 0; i < 128*128; i++) {
            b.putInt(i);
            b.putShort(((short) 2));
        }
        GraphSectors gs = new GraphSectors(b);

        var actual = gs.sectorsInArea(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 2730*129);
        var expected = new ArrayList<GraphSectors.Sector>();
        for (int i = 0; i < 128*128; i++) {
            expected.add(new GraphSectors.Sector(i, i+2));
        }
        assertEquals(expected, actual);

    }
}
