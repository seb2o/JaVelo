package ch.epfl.javelo;

public final class Q28_4 {

    private Q28_4(){}

    public static int ofInt(int i){
        return (int)Math.scalb(i,4);
    }

}
