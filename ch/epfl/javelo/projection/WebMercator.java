package ch.epfl.javelo.projection;

import ch.epfl.javelo.utils.Math2;

/**
  * @author Edgar Gonzales (328095)
 */
public final class WebMercator {
    private WebMercator() {

    }

    /** retourne la coordonnée x dans la projection mercator d'un point WGS de longitude donnée
     * @param lon la longitude du point dans le système WGS
     * @return coordonnée x du point de longitude donnée dans le système mercator
     */
    public static double x(double lon){
        return (lon +Math.PI)/(2*Math.PI);
    }

    /** retourne la coordonnée x dans la projection mercator d'un point WGS de latitude donnée
     * @param lat la latitude du point dans le système WGS
     * @return coordonnée x du point de latitude donnée dans le système mercator
     */
    public static double y(double lat){
        return (Math.PI - Math2.asinh(Math.tan(lat)))/(2*Math.PI);
    }

    /**
     * retourne la longitude dans le système WGS d'un point de coordonée x dans le système mercator
     * @param x la coordonnée x du point dans le système mercator
     * @return la longitude du point dans le système WGS
     */
    public static double lon(double x){
        return 2*Math.PI*x-Math.PI;
    }

    /**
     * retourne la latitude dans le système WGS d'un point de coordonée x dans le système mercator
     * @param y la coordonnée x du point dans le système mercator
     * @return la latitude du point dans le système WGS
     */
    public static double lat(double y){
        return Math.atan(Math.sinh(Math.PI-2*Math.PI*y));
    }
}
