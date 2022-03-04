package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class PointWebMercatorTest {
    public static final double DELTA = 1e-7;

    @Test
    void ofPointChWorksWithKnownValues(){
        double e = Ch1903.e(Math.toRadians(6.5790772),Math.toRadians(46.5218976));
        double n = Ch1903.n(Math.toRadians(6.5790772),Math.toRadians(46.5218976));
        PointCh pointCh = new PointCh(e,n);
        PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(pointCh);
        double actualX = pointWebMercator.x();
        double actualY = pointWebMercator.y();
        double expectedX = 0.518275214444;
        double expectedY = 0.353664894749;
        assertEquals(expectedX,actualX,DELTA);
        assertEquals(expectedY,actualY,DELTA);
    }
}
