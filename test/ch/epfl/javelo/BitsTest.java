package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitsTest {


    int testBit = Integer.MIN_VALUE;

    @Test
    public void signedExtractionTestFirstBit(){
        var actual = Bits.extractSigned(testBit,31,1);
        var expected = -1;
        assertEquals(expected, actual);
    }
    @Test
    public void signedExtractionTestLastBit(){
        var actual = Bits.extractSigned(1,0,1);
        var expected = 1;
        assertEquals(expected, actual);
    }

    @Test
    public void signedExtractionTestWholeChain(){
        for (int i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE ; i+=(int)Math.pow(2,10)) {
            var actual = Bits.extractSigned(i,0,32);
            assertEquals(i,actual);
        }
    }

    @Test
    public void signedExtractionTestClassicIntervals(){
        System.out.println(Integer.toBinaryString(1<<10));
        var actual = Bits.extractSigned(1<<10,9,2);
        System.out.println(Integer.toBinaryString(actual));
        var expected = 0b010000000000;
        assertEquals(expected,actual);
    }
}
