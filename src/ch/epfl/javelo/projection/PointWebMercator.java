package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 */
public record PointWebMercator(double x, double y) {

    /**
     * constructeur d'un point dans le système WebMercator
     * @param x coordonnée x du point. compris entre 0 et 1
     * @param y coordonnée y du point. compris entre 0 et 1
     */
    public PointWebMercator{
        Preconditions.checkArgument(x >=0 & x <= 1 & y >=0 & y <= 1 );
    }

    /**
     * construit un point de coordonnées exprimées dans le système mercator zoomé
     * @param zoomLevel le niveau de zoom du système mercator
     * @param x la coordonnée x
     * @param y la coordonné y
     * @return un point de coordonnées exprimées dans un système mercator zoomé
     */
    public static PointWebMercator of(int zoomLevel, double x, double y){
        return new PointWebMercator(Math.scalb(x,-(8+zoomLevel)),Math.scalb(y,-(8+zoomLevel)));
    }

    /**
     * construit un point exprimé dans le système mercator à partir d'un point ch1903
     * @param pointCh le point exprimé dans le système ch1903
     * @return le même point de coordonnées exprimées dans le système webMercator
     */
    public static PointWebMercator ofPointCh(PointCh pointCh){
        double lon = Ch1903.lon(pointCh.e(),pointCh.n());
        double lat = Ch1903.lat(pointCh.e(),pointCh.n());
        double x = WebMercator.x(lon);
        double y = WebMercator.y(lat);
        return new PointWebMercator(x,y);
    }

    /**
     * retourne la coordonnée du point x exprimée dans un niveau de zoom donné
     * @param zoomLevel le niveau de zoom donné
     * @return la coordonée x du point dans le système webMercator au niveu de zoom donné
     */
    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x,8+zoomLevel);
    }

    /**
     * retourne la coordonnée du point y exprimée dans un niveau de zoom donné
     * @param zoomLevel le niveau de zoom donné
     * @return la coordonée y du point dans le système webMercator au niveu de zoom donné
     */
    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y,8+zoomLevel);
    }

    /**
     * retourne la longitude dans le système WGS de l'instance, en radians
     * @return la longitude dans le système WGS de l'instance
     */
    public double lon(){
        return WebMercator.lon(x);
    }

    /**
     * retourne la latitude dans le système WGS de l'instance, en radians
     * @return la latitude dans le système WGS de l'instance
     */
    public double lat(){
        return WebMercator.lat(y);
    }

    /**
     * retourne l'instance du point sous forme de point en coordonnées ch1903
     * @return l'instance du point sous forme de point en coordonnées ch1903
     */
    public PointCh toPointCh(){
        double e = Ch1903.e(lon(),lat());
        double n = Ch1903.n(lon(),lat());
        if(SwissBounds.containsEN(e,n)){
            return new PointCh(e,n);
        }
        else {return null;}
    }
}
