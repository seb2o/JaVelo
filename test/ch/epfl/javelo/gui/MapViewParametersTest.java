package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

public class MapViewParametersTest {

    @Test
    public void pointAtAndViewTest(){
        MapViewParameters mapView = new MapViewParameters(0,128,128);
        System.out.println(mapView.topLeft());
        System.out.println(mapView.pointAt(127,0).xAtZoomLevel(0));
        System.out.println(mapView.viewY(new PointWebMercator(1,0.5)));
    }

}
