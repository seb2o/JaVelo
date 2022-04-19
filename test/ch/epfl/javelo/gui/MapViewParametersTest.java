package ch.epfl.javelo.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapViewParametersTest {

    @Test
    public void pointAtAndViewTest(){
        MapViewParameters mapView = new MapViewParameters(10,135735,92327);
        assertEquals(mapView.viewX(mapView.pointAt(0,0)),0.0);
        assertEquals(mapView.viewY(mapView.pointAt(0,0)),0.0);
    }

}
