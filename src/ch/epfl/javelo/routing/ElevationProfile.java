package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

public class ElevationProfile {
    final double length;
    final float[] elevationSamples;

    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length > 1);
        this.length = length;
        this.elevationSamples = elevationSamples.clone();
    }

    public double length(){
        return length;
    }

    public double minElevation(){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (Float elevation: elevationSamples) {
            s.accept(elevation);
        }
        return s.getMin();
    }

    public double maxElevation(){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (Float elevation: elevationSamples) {
            s.accept(elevation);
        }
        return s.getMax();
    }

    public double totalAscent(){
        float totalAscent = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            float difference = elevationSamples[i] - elevationSamples[i-1];
            if(difference > 0) {
                totalAscent += difference;
            }
        }
        return  totalAscent;
    }

    public double totalDescent(){
        float totalDescent = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            float difference = elevationSamples[i] - elevationSamples[i-1];
            if(difference < 0) {
                totalDescent += difference;
            }
        }
        return  totalDescent;
    }

    public double elevationAt(double position){
        DoubleUnaryOperator function = Functions.sampled(elevationSamples, length);
        return function.applyAsDouble(position);
    }

}
