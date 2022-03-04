package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 */
public final class SwissBounds {
    private SwissBounds(){

    }

    /**
     * La valeur minimale pouvant caractériser un point en suisse selon l'axe EST selon la norme CH1903.
     */
    public static final double MIN_E = 2485000;
    /**
     * La valeur maximale pouvant caractériser un point en suisse selon l'axe EST selon la norme CH1903.
     */
    public static final double MAX_E = 2834000;
    /**
     * La valeur minimale pouvant caractériser un point en suisse selon l'axe NORD selon la norme CH1903.
     */
    public static final double MIN_N = 1075000;
    /**
     * La valeur maximale pouvant caractériser un point en suisse selon l'axe NORD selon la norme CH1903.
     */
    public static final double MAX_N = 1296000;
    /**
     * La largeur en mètres de la Suisse.
     */
    public static final double WIDTH = MAX_E - MIN_E;
    /**
     * La hauteur en mètres de la Suisse.
     */
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Dis si un point est dans le rectangle qui inscrit la Suisse.
     * @param e coordonnée du point selon l'axe EST.
     * @param n coordonnée du point selon l'axe NORD.
     * @return true si le point est dans le rectangle qui inscrit la Suisse.
     */
    public static boolean containsEN(double e, double n){
        return Math2.clamp(MIN_E, e, MAX_E) == e && Math2.clamp(MIN_N, n, MAX_N) == n;
    }
}
