package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions {

    private Functions() {
    }

    ;

    public static DoubleUnaryOperator constant(double y) {
        return t -> {
            return y;
        };
    }


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
