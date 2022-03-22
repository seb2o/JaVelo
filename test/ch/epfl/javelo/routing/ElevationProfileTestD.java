package ch.epfl.javelo.routing;

import ch.epfl.javelo.routing.ElevationProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevationProfileTestD {

    float[] floatArray = {0.0f,2.0f,4.0f,6.0f,8.0f,10.0f};
    float[] floatArray2 = {5.0f,2.0f,4.0f,6.0f,8.0f,9.0f};
    float[] floatArray3 = {0.0f,3.0f,0.0f,3.0f,0.0f,3.0f,0.0f,3.0f};

    ElevationProfile elevationProfile = new ElevationProfile(10.0,floatArray);
    ElevationProfile elevationProfile2 = new ElevationProfile(10.0,floatArray2);
    ElevationProfile elevationProfile3 = new ElevationProfile(10.0,floatArray3);
    ElevationProfile elevationProfile4 = new ElevationProfile(20.0,floatArray);
    @Test
    void lengthTestD(){
        assertEquals(10.0,elevationProfile.length());
    }

    @Test
    void minElevationTestD(){
        assertEquals(0.0,elevationProfile.minElevation());
        assertEquals(2.0, elevationProfile2.minElevation());
    }

    @Test
    void maxElevationTestD(){
        assertEquals(10.0,elevationProfile.maxElevation());
        assertEquals(9.0, elevationProfile2.maxElevation());
    }

    @Test
    void totalAscentTestD(){
        assertEquals(10.0,elevationProfile.totalAscent());
        assertEquals(7.0,elevationProfile2.totalAscent());
        assertEquals(4.0*3.0, elevationProfile3.totalAscent());
    }


    @Test
    void elevationAtTestD(){
        assertEquals(0.0,elevationProfile.elevationAt(-1.0));
        assertEquals(10.0,elevationProfile.elevationAt(11.0));
        assertEquals(0.0,elevationProfile.elevationAt(0.0));
        assertEquals(10.0,elevationProfile.elevationAt(10.0));
        assertEquals(1.0,elevationProfile.elevationAt(1.0),1e-3);
        assertEquals(2.0,elevationProfile.elevationAt(2.0),1e-3);
        assertEquals(7.0,elevationProfile.elevationAt(7.0),1e-3);
        assertEquals(0.5,elevationProfile4.elevationAt(1.0),1e-4);

    }
}