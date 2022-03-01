package ch.epfl.javelo;

public final class Q28_4 {
    private  Q28_4(){
    }
    public static int ofInt(int i){
        int q = (int)-Math.scalb(Bits.extractUnsigned(i,0,1),28);
        for (int j = 1; j < 30; j++) {
            q += (int)Math.scalb(Bits.extractUnsigned(i,j,1),28-j);
        }
        return q;
    }
    /**public static double asDouble(int q28_4){

    }
    public static float asFloat(int q28_4){

    }*/
}
