package ch.epfl.javelo.data;
import ch.epfl.javelo.data.AttributeSet;
import org.junit.jupiter.api.Test;
import static ch.epfl.javelo.data.Attribute.HIGHWAY_TRACK;
import static ch.epfl.javelo.data.Attribute.TRACKTYPE_GRADE1;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeSetTest {

    @Test
    public void toStringTest() {
        AttributeSet set = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }

}
