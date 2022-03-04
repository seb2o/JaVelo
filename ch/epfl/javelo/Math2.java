package ch.epfl.javelo;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 */
public final class Math2 {
    private Math2() {

    }

    /**
     * @return la partie entière par excès de la division de x par y
     * @throws IllegalArgumentException si x est négatif ou si y est négatif ou nul
     */
    public static int ceilDiv(int x, int y){
        Preconditions.checkArgument (x >= 0 && y> 0);
        return (x+y-1)/y;
    }

    /**
     * @return la coordonnée y du point se trouvant sur la droite passant par (0,y0) et (1,y1) et de coordonnée x donnée
     */
    public static double interpolate(double y0, double y1, double x){
        double a = y1 - y0;
        return Math.fma(a,x, y0);
    }

    /**
     * Limite la valeur v à l'intervalle allant de min à max, en retournant min si v est inférieure à min, max si v est supérieure à max, et v sinon.
     * @param v valeur à vérifier
     * @throws IllegalArgumentException si min est plus grand que le max
     */
    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(min <= max);
        if(v < min){
            return min;
        }
        if(v > max){
            return max;
        }
        return v;
    }

    /**
     * Limite la valeur v à l'intervalle allant de min à max, en retournant min si v est inférieure à min, max si v est supérieure à max, et v sinon.
     * @param v valeur à vérifier
     * @throws IllegalArgumentException si min est plus grand que le max
     */
    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument(min <= max);
        if(v < min){
            return min;
        }
        if(v > max){
            return max;
        }
        return v;
    }

    /**
     * @return l'arcsin de x
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1+Math.pow(x,2)));
    }

    /**
     * @param uX la composante selon X de u.
     * @param uY la composante selon Y de u.
     * @param vX la composante selon X de v.
     * @param vY la composante selon Y de v.
     * @return le porduit scalaire des vecteurs u et v.
     */
    public static double dotProduct(double uX, double uY, double vX, double vY){
        return Math.fma(uX,vX,uY*vY);
    }

    /**
     * @param uX la composante selon X de u.
     * @param uY la composante selon Y de u.
     * @return le carré de la norme du vecteur u, uX et uY étant les composantes de ce vecteur
     */
    public static double squaredNorm(double uX, double uY){
        return dotProduct(uX,uY,uX,uY);
    }

    /**
     * @param uX la composante selon X de u.
     * @param uY la composante selon Y de u.
     * @return la norme du vecteur u, uX et uY étant les composantes de ce vecteur
     */
    public static double norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX,uY));
    }

    /**
     * @return la longueur de la projection du vecteur allant du point A (de coordonnées aX et aY) au point P (de coordonnées pX et pY) sur le vecteur allant du point A au point B (de composantes bY et bY)
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        double uX = pX-aX;
        double uY = pY-aY;
        double vX = bX-aX;
        double vY = bY-aY;
        return dotProduct(uX,uY,vX,vY)/norm(vX,vY);
    }

}
