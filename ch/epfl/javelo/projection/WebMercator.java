package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

public final class WebMercator {
    private WebMercator() {

    }

    public static double x(double lon){
        return (lon +Math.PI)/(2*Math.PI);
    }

    public static double y(double lat){
        return (Math.PI - Math2.asinh(Math.tan(lat)))/(2*Math.PI);
    }

    public static double lon(double x){
        return 2*Math.PI*x-Math.PI;
    }

    public static double lat(double y){
        return Math.atan(Math.sinh(Math.PI-2*Math.PI*y));
    }
}
