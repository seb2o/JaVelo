package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * @author Sébastien Boo (345870)
 * @author Edgar Gonzales (328095)
 */
public interface Route {

    /**
     * retourne l'index du segment à la position donnée (en mètres)
     * @param position la position du segment dont la position est recherchée
     * @return l'index
     */
    int indexOfSegmentAt(double position);

    /**
     * @return la longueur de l'itinéraire, en mètres
     */
    double length();

    /**
     * @return les aretes de l'itinéraire
     */
    List<Edge> edges();

    /**
     * @return les points situées aux extrémités des arêtes de l'itinéraire
     */
    List<PointCh> points();

    /**
     * retourne le point se trouvant à la position donnée le long de l'itinéraire
     * @param position la distance à l'origine de l'itinéraire du point dont l'altitude est recherchée
     * @return le point se trouvant à la position donnée le long de l'itinéraire
     */
    PointCh pointAt(double position);

    /**
     * retourne l'altitude à la position donnée le long de l'itinéraire
     * @param position la distance à l'origine de l'itinéraire du point dont l'altitude est recherchée
     * @return l'altitude à la position donnée le long de l'itinéraire
     */
    double elevationAt(double position);

    /**
     * retourne l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     * @param position la distance à l'origine de l'itinéraire du point dont le noeud correspondant est recherché
     * @return l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     */
    int nodeClosestTo(double position);

    /**
     * retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné
     * @param point le point de référence
     * @return le routePoint le plus proche du point de référence
     */
    RoutePoint pointClosestTo(PointCh point);
}
