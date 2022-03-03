package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * @author Edgar Gonzales (32809)
 */
public record PointWebMercator(double x, double y) {

    /**
     * constructeur d'un point dans le système WebMarcator
     * @param x coordonnée x du point. compris entre 0 et 1
     * @param y coordonnée y du point. compris entre 0 et 1
     */
    public PointWebMercator{
        Preconditions.checkArgument(x >=0 & x <= 1 & y >=0 & y <= 1 );
    }

    public static PointWebMercator of(int zoomLevel, double x, double y){
        return new PointWebMercator(Math.scalb(x,-(8+zoomLevel)),Math.scalb(y,-(8+zoomLevel)));
    }

    public static PointWebMercator ofPointCh(PointCh pointCh){
        double lon = Ch1903.lon(pointCh.e(),pointCh.n());
        double lat = Ch1903.lat(pointCh.e(),pointCh.n());
        double x = WebMercator.x(lon);
        double y = WebMercator.y(lat);
        return new PointWebMercator(x,y);
    }

    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x,8+zoomLevel);
    }

    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y,8+zoomLevel);
    }

    public double lon(){
        return WebMercator.lon(x);
    }

    public double lat(){
        return WebMercator.lat(y);
    }

    public PointCh toPointCh(){
        double e = Ch1903.e(lon(),lat());
        double n = Ch1903.n(lon(),lat());
        if(SwissBounds.containsEN(e,n)){
            return new PointCh(e,n);
        }
        else {return null;}
    }
}
