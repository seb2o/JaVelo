package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

public record PointCh(double e, double n) {

    public PointCh{
        if (!SwissBounds.containsEN(e,n)){
            throw new IllegalArgumentException();
        }
    }

    public double squaredDistanceTo(PointCh that){
        return Math2.squaredNorm(this.e- that.e,this.n - that.n);
    }

    public double distanceTo(PointCh that){
        return Math2.norm(this.e-that.e,this.n-that.n);
    }

    public double lon(){
        return Ch1903.lon(e,n);
    }

    public double lat(){
        return Ch1903.lat(e,n);
    }
}
