package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

public final class SwissBounds {
    private SwissBounds(){

    }

    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double LENGTH = MAX_N - MIN_N;

    public static boolean containsEN(double e, double n){
        return Math2.clamp(MIN_E, e, MAX_E) == e && Math2.clamp(MIN_N, n, MAX_N) == n;
    }
}