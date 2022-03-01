package ch.epfl.javelo;

public final class Q28_4 {

    private  Q28_4(){}

    public static int ofInt(int i) {
        Preconditions.checkArgument(i<=Math.pow(2,28)-1);
        return i << 4;
    }

    public static double asDouble(int i){
        return Math.scalb(i,-4);
    }

    public static float asFloat(int i){
        return Math.scalb(i,-4);
    }


}
