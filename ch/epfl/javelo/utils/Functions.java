package ch.epfl.javelo.utils;

import ch.epfl.javelo.verification.Preconditions;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Edgar Gonzales (328095)
 */
public final class Functions {

    private Functions() {} //classe non instanciable

    /**
     * retourne une fonction qui retourne une valeure constante
     * @param y la valeure de la constante que la fonction doit retourner
     * @return une fonction constante de valeure y pour n'importe quel argument
     */
    public static DoubleUnaryOperator constant(double y) {
        return t -> {
            return y;
        };
    }


    /**
     * retourne une fonction obtenue par interpolation linéaire entre des échantillons fournis en paramètres sur [0,xMax]
     * la fonction retourne sa valeure en 0 pour un paramètre inférieur à 0 et sa valeure en xMax pour une valeure > xMax
     * @param samples les échantillons de valeures de la fonction a interpoler, minimum 2
     * @param xMax la borne supérieure de l'intervalle où la fonction varie
     * @return la fonction approximant celle définié par les samples
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        int len = samples.length;
        Preconditions.checkArgument(xMax > 0 && len >= 2);
        double step = xMax / (len-1);
        return t -> {
            for (int i = 0; i < len - 1; i++) {
                if ((i + 1) * step >= t) {
                    return Math2.interpolate(samples[i], samples[i + 1], (t - i*(step))/step);
                }
            }
            if(t < 0){
                return (double)samples[0];
            }
            return (double)samples[len - 1];
        };
    }
}
