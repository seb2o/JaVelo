package ch.epfl.javelo.util;
import ch.epfl.javelo.utils.Functions;
import ch.epfl.randomizer.TestRandomizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionsTest {
    public static final double DELTA = 1e-7;

    @Test
    public void constantTest() {
        var expected = TestRandomizer.newRandom().nextDouble();
        var actual = Functions.constant(expected).applyAsDouble(TestRandomizer.newRandom().nextDouble());
        assertEquals(expected,actual);
    }

    @Test
    public void sampledTest(){
        float[] samples = new float[]{-15f, 4f, 9f, 9f};
        double xMax = TestRandomizer.newRandom().nextDouble(.1,Double.MAX_VALUE);
        var value0  = Functions.sampled(samples,xMax).applyAsDouble(0 * xMax);
        var value1  = Functions.sampled(samples,xMax).applyAsDouble(.1 * xMax);
        var value2  = Functions.sampled(samples,xMax).applyAsDouble(.2 * xMax);
        var value3  = Functions.sampled(samples,xMax).applyAsDouble(.3 * xMax);
        var value4  = Functions.sampled(samples,xMax).applyAsDouble(.4 * xMax);
        var value5  = Functions.sampled(samples,xMax).applyAsDouble(.5 * xMax);
        var value6  = Functions.sampled(samples,xMax).applyAsDouble(.6 * xMax);
        var value7  = Functions.sampled(samples,xMax).applyAsDouble(.7 * xMax);
        var value8  = Functions.sampled(samples,xMax).applyAsDouble(.8 * xMax);
        var value9  = Functions.sampled(samples,xMax).applyAsDouble(.9 * xMax);
        var value10 = Functions.sampled(samples,xMax).applyAsDouble(1 * xMax);
        assertEquals(-15, value0, DELTA);
        assertEquals(-9.3, value1, DELTA);
        assertEquals(-3.6, value2, DELTA);
        assertEquals(2.1, value3, DELTA);
        assertEquals(5, value4, DELTA);
        assertEquals(6.5, value5, DELTA);
        assertEquals(8, value6, DELTA);
        assertEquals(9, value7, DELTA);
        assertEquals(9, value8, DELTA);
        assertEquals(9, value9, DELTA);
        assertEquals(9, value10, DELTA);

    }

    @Test
    public void sampledTestExtremValues(){
        float[] samples = new float[]{-15f, 4f, 9f, 9f};
        double xMax = TestRandomizer.newRandom().nextDouble(.1,Double.MAX_VALUE);
        double extremMinValue = TestRandomizer.newRandom().nextDouble(-.1,Double.MIN_VALUE);
        double extremMaxValue = TestRandomizer.newRandom().nextDouble(xMax,Double.MAX_VALUE);
        var value0  = Functions.sampled(samples,xMax).applyAsDouble(extremMinValue);
        var value1  = Functions.sampled(samples,xMax).applyAsDouble(extremMaxValue);
        assertEquals(-15, value0, DELTA);
        assertEquals(9, value1, DELTA);
    }
}
