package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 */
public record PointCh(double e, double n) {
    public PointCh{
        Preconditions.checkArgument(SwissBounds.containsEN(e,n));
    }

    /**
     * @param that l'autre point.
     * @return la distance entre le point considéré et l'autre au carré.
     */
    public double squaredDistanceTo(PointCh that){
        return Math2.squaredNorm(this.e- that.e,this.n - that.n);
    }

    /**
     * @param that l'autre point.
     * @return la distance entre le point considéré et l'autre.
     */
    public double distanceTo(PointCh that){
        return Math2.norm(this.e-that.e,this.n-that.n);
    }

    /**
     * @return la coordonnée longitudinale dans la norme WGS84, en radians
     */
    public double lon(){
        return Ch1903.lon(e,n);
    }

    /**
     * @return la coordonnée latitudinale dans la norme WGS84, en radians
     */
    public double lat(){
        return Ch1903.lat(e,n);
    }
}
