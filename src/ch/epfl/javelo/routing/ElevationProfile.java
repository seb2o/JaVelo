package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * @author Sébastien Boo (345870)
 * @author Edgar Gonzales (328095)
 */
public class ElevationProfile {
    final double length;
    final float[] elevationSamples;

    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length > 1);
        this.length = length;
        this.elevationSamples = elevationSamples.clone();
    }

    /**
     * @return la longueure de l'instance
     */
    public double length(){
        return length;
    }

    /**
     * @return l'altitude minimum du profil, en mètres
     */
    public double minElevation(){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (Float elevation: elevationSamples) {
            s.accept(elevation);
        }
        return s.getMin();
    }

    /**
     * @return l'altitude maximum du profil, en mètres.
     */
    public double maxElevation(){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (Float elevation: elevationSamples) {
            s.accept(elevation);
        }
        return s.getMax();
    }

    /**
     * @return  le dénivelé positif total du profil, en mètres
     */
    public double totalAscent(){
        double totalAscent = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            double difference = elevationSamples[i] - elevationSamples[i-1];
            if(difference > 0) {
                totalAscent += difference;
            }
        }
        return  totalAscent;
    }

    /**
     * @return le dénivelé négatif total du profil, en mètres
     */
    public double totalDescent(){
        double totalDescent = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            double difference = elevationSamples[i] - elevationSamples[i-1];
            if(difference < 0) {
                totalDescent += difference;
            }
        }
        return  Math.abs(totalDescent);
    }

    /**
     * méthode permettant de calculer la hauteur d'un point d'une arête
     * @param position la postition du point dont l'altitude est recherchée
     * @return l'altitude du profil à la position donnée.
     * le premier échantillon est retourné lorsque la position est négative, le dernier lorsqu'elle est supérieure à la longueur
     */
    public double elevationAt(double position){
        position = Math2.clamp(0,position,length);
        DoubleUnaryOperator function = Functions.sampled(elevationSamples, length);
        return function.applyAsDouble(position);
    }

}
