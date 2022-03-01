package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitsTest {

    private int generatingBit = (int)Math.pow(2,31);
    private int unsignedTestBit = generatingBit >>> 10;


    @Test
    public void signedExtractionTest(){
        var actual = Bits.extractUnsigned(generatingBit,29,1);
        var expected = 0b0;
        assertEquals(expected, actual);
    }
}
