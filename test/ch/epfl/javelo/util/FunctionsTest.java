package ch.epfl.javelo.util;
import ch.epfl.javelo.utils.Functions;
import ch.epfl.Randomizer.TestRandomizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionsTest {

    @Test
    public void constantTest() {
        var expected = TestRandomizer.newRandom().nextDouble();
        var actual = Functions.constant(expected).applyAsDouble(TestRandomizer.newRandom().nextDouble());
        assertEquals(expected,actual);
    }

    //todo a completer

}
