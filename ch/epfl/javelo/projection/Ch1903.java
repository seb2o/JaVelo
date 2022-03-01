package ch.epfl.javelo.projection;

/**
 * @author Gonzalez Edgar (328095)
 */
public final class Ch1903 {
    private Ch1903(){

    }

    /**
     * Convertit des coordonnées de la norme WGS84 à CH1903.
     * @param lon la coordonnée longitudinale dans la norme WGS84.
     * @param lat la coordonnée latitudinale dans la norme WGS84.
     * @return la coordonnée e en degrés.
     */
    public static double e(double lon, double lat){
        double lon1 = Math.pow(10,-4)*(3600*Math.toDegrees(lon)-26782.5);
        double lat1 = Math.pow(10,-4)*(3600*Math.toDegrees(lat)-169028.66);;
        return (2600072.37
                +211455.93*lon1
                -10938.51*lon1*lat1
                -0.36*lon1*lat1*lat1
                -44.54*Math.pow(lon1,3));
    }

    /**
     * Convertit des coordonnées de la norme WGS84 à CH1903.
     * @param lon la coordonnée longitudinale dans la norme WGS84.
     * @param lat la coordonnée latitudinale dans la norme WGS84.
     * @return la coordonnée n en degrés.
     */
    public static double n(double lon, double lat){
        double lon1 = Math.pow(10,-4)*(3600*Math.toDegrees(lon)-26782.5);
        double lat1 = Math.pow(10,-4)*(3600*Math.toDegrees(lat)-169028.66);;
        return (1200147.07
                +308807.95*lat1
                +3745.25*lon1*lon1
                +76.63*lat1*lat1
                -194.56*lon1*lon1*lat1
                +119.79*Math.pow(lat1,3));
    }

    /**
     * Convertit des coordonnées de la norme CH1903 à WGS84.
     * @param e la coordonnée selon l'axe EST dans la norme CH1903.
     * @param n la coordonnée selon l'axe NORD dans la norme CH1903.
     * @return la coordonnée longitudinale.
     */
    public static double lon(double e, double n){
        double x = Math.pow(10,-6)*(e-2600000);
        double y = Math.pow(10,-6)*(n-1200000);
        double lon0 = 2.6779094
                    + 4.728982*x
                    + 0.791484*x*y
                    + 0.1306*x*y*y
                    - 0.0436*x*x*x;
        return Math.toRadians(lon0*100/36);
    }
    /**
     * Convertit des coordonnées de la norme CH1903 à WGS84.
     * @param e la coordonnée selon l'axe EST dans la norme CH1903.
     * @param n la coordonnée selon l'axe NORD dans la norme CH1903.
     * @return la coordonnée latitudinale.
     */
    public static double lat(double e, double n){
        double x = Math.pow(10,-6)*(e-2600000);
        double y = Math.pow(10,-6)*(n-1200000);
        double lat0 = 16.9023892
                + 3.238272*y
                - 0.270978*x*x
                - 0.002528*y*y
                - 0.0447*x*x*y
                - 0.0140*y*y*y;
        return Math.toRadians(lat0*100/36);
    }
}


