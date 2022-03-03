package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitsTest {


    int testBit = Integer.MIN_VALUE;

    @Test
    public void signedExtractionTest(){
        var actual = Bits.extractUnsigned(testBit,31,1);
        var expected = -1;
        assertEquals(expected, actual);
    }
}
