package ch.epfl.javelo;

public final class Bits {
    private Bits(){
    }

    public static int extractSigned(int value, int start, int length){
        Preconditions.checkArgument(start >= 0 & length >= 0 & start+length <= 31);
        value = value << start;
        value = value >> 32 - length;
        return value;
    }

    public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(start >= 0 & start+length <= 31 & length > 0 & length != 32); //TODO "size" dans le pdf = length ?
        value = value << start;                                                                               //TODO ca parle aussi de complément à deux..
        value = value >>> 32 - length;
        return value;
    }
}
