package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * @author Sébastien Boo (345870)
 * @author Edgar Gonzales (328095)
 */

public record RoutePoint(PointCh point, double position, double distanceToReference) {
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     * retourne un point identique a l'instance de position décalée de la différence donnée
     * @param positionDifference le décalage de position a appliquer
     * @return un point identique a l'instance de position décalée de la différence donnée
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(point,position + positionDifference, distanceToReference);
    }

    /**
     * retourne le plus proche du point de référence entre l'instance de la classe et une autre instance donnée en paramètres
     * @param that le routePoint a comparer a l'instance
     * @return le plus proche du point de référence entre l'instance de la classe et une autre instance donnée en paramètres
     */
    public RoutePoint min(RoutePoint that){
        return (this.distanceToReference <= that.distanceToReference) ? this : that;
    }

    /**
     * retourne un routePoint determiné comme le point le plus proche entre l'instance de la classe et un point dont
     * la distance a la référence et la position sont données en paramètres
     * @param thatPoint le point a comparer
     * @param thatPosition la position du point par rapport au 0 de l'itinéraire
     * @param thatDistanceToReference la distance du point a la référence
     * @return le route point le plus proche entre l'instance et celui déterminé par les attributs passé en paramètres
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return (this.distanceToReference <= thatDistanceToReference) ? this : new RoutePoint(thatPoint ,thatPosition, thatDistanceToReference);
    }
}
