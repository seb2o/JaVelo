package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Sébastien Boo (345870)
 * @author Edgar Gonzalez (328095)
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    /**
     * méthode de construction d'une edge a partir d'un graph
     * @param graph le graph auquel apartient l'edge
     * @param edgeId l'id de l'edge dans le graph
     * @param fromNodeId le node initial de l'edge
     * @param toNodeId le node suivant le dernier node de l'edge
     * @return une nouvelle instance de l'edge
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * retourne la position le long de l'arête, en mètres, qui se trouve la plus proche du point donné
     * @param point le point à projeter sur l'arete
     * @return la distance au début de l'arete du projeté orthogonal du point sur l'arete
     */
    public double positionClosestTo(PointCh point){
        double aX = fromPoint.e();
        double aY = fromPoint.n();
        double bX = toPoint.e();
        double bY = toPoint.n();
        double pX = point.e();
        double pY = point.n();
        return Math2.projectionLength(aX,aY,bX,bY,pX,pY);
    }

    /**
     *  retourne le point se trouvant à la position donnée sur l'arête, exprimée en mètres
     * @param position la distance au début de l'arete du point étudié
     * @return le point se trouvant à la position donnée sur l'arête, exprimée en mètres
     */
    public PointCh pointAt(double position){
        position = Math2.clamp(0,position,this.length);
        double e = Math2.interpolate(fromPoint.e(),toPoint.e(),position/length);
        double n = Math2.interpolate(fromPoint.n(),toPoint.n(),position/length);
        return new PointCh(e,n);
    }

    /**
     * retourne l'altitude, en mètres, à la position donnée sur l'arête
     * @param position la distance au début de l'arete du point étudié
     * @return l'altitude du point de posotion donnée, en mètres
     */
    public double elevationAt(double position){
        position = Math2.clamp(0,position,this.length);
        return profile.applyAsDouble(position);
    }

}
