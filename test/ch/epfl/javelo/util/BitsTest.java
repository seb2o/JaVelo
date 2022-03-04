package ch.epfl.javelo.util;
import ch.epfl.javelo.Bits;
import org.junit.jupiter.api.Test;
import static ch.epfl.randomizer.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 */
public class BitsTest {

    final int testBit = Integer.MIN_VALUE;

    @Test
    public void signedExtractionTestFirstBit(){
        var actual = Bits.extractSigned(testBit,31,1);
        var expected = -1;
        assertEquals(expected, actual);
    }
    @Test
    public void signedExtractionTestLastBit(){
        var actual = Bits.extractSigned(1,0,2);
        var expected = 1;
        assertEquals(expected, actual);
    }

    @Test
    public void signedExtractionTestWholeChain(){
        int i = newRandom().nextInt();
        var actual = Bits.extractSigned(i,0,32);
        assertEquals(i,actual);
    }

    @Test
    public void signedExtractionTestClassicIntervals(){
        var actual = Bits.extractSigned(1<<10,9,3);
        var expected = 0b010;
        assertEquals(expected,actual);
    }

    @Test
    public void unsignedExtractionTestFirstBit(){
        var actual = Bits.extractUnsigned(testBit>>>1,30,1);
        var expected = 1;
        assertEquals(expected,actual);
    }
    @Test
    public void unsignedExtractionTestLastBit(){
        var actual = Bits.extractUnsigned(1,0,1);
        var expected = 1;
        assertEquals(expected,actual);
    }

    @Test
    public void unsignedExtractionTestWholeChain(){
        int i = newRandom().nextInt(1);
        var actual = Bits.extractUnsigned(i,0,31);
        assertEquals(i,actual);
    }

    //pas de Randomizer avec lenght=0 comme spécifié sur piazza
}
