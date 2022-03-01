package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class PointWebMercatorTest {
    public static final double DELTA = 1e-7;

    @Test
    void ofPointChWorksWithKnownValues(){
        PointCh pointCh = new PointCh(2500000,1100000);
        PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(pointCh);
        double actualX = pointWebMercator.x();
        double actualY = pointWebMercator.y();
        double expectedX = 684258.6180669012;
        double expectedY = 5787419.57036233;
        assertEquals(expectedX,actualX,DELTA);
        assertEquals(expectedY,actualY,DELTA);
    }
}
