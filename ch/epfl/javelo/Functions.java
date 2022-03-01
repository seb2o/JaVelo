package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions {

    private Functions(){};

//    public static Constant constant(double y){
//        return;
//    }

    private static final class Constant implements DoubleUnaryOperator{


        public Constant(double y){}

        @Override
        public double applyAsDouble(double y) {
            return y;
        }
    }

}
