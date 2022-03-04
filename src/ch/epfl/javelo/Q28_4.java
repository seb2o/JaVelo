package ch.epfl.javelo;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 * Permet la représenation de nombres décimaux avec une précison de 1/16 stocké dans un int
 */
public final class Q28_4 {

    private  Q28_4(){}//classe non instanciable

    /**
     * retourne la représentation Q28_4 d'un entier positif ou nul inférieur à 2^28-1
     * @param i l'entier a convertir
     * @return représentation Q28_4 de l'entier passé en paramètres
     */
    public static int ofInt(int i) {
        Preconditions.checkArgument(i>= 0 && i<=Math.pow(2,28)-1);
        return i << 4;
    }

    /**
     * convertit un nombre représenté en Q28_4 en représentation de type double
     * @param i le nombre représenté en Q28_4
     * @return la valeure stockée dans le paramètre représentée en type double
     */
    public static double asDouble(int i){
        return Math.scalb(i,-4);
    }

    /**
     * convertit un nombre représenté en Q28_4 en représentation de type float
     * @param i le nombre représenté en Q28_4
     * @return la valeure stockée dans le paramètre représentée en type float
     */
    public static float asFloat(int i){
        return Math.scalb(i,-4);
    }

}
