package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions {

    private Functions(){};

    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }

//    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
//
//    }

    private static final class Constant implements DoubleUnaryOperator{

        private double constantValue;

        public Constant(double y){
            constantValue = y;
        }

        @Override
        public double applyAsDouble(double y) {
            return this.constantValue;
        }//todo what is the point of the argument of apply as double ?


    }

}
