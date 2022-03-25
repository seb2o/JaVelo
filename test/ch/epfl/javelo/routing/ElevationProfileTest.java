package ch.epfl.javelo.routing;

import ch.epfl.javelo.routing.ElevationProfile;
import org.junit.jupiter.api.Test;

public class ElevationProfileTest {

    @Test
    public void totalAscentTest() {

        var elevationProfile = new ElevationProfile(10,new float[]{0,2.5f,5,2.5f,0});
    }

}
