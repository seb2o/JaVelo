package ch.epfl.javelo;

public class Main
{
    public static void main(String[] args) {
        int value = 2120947847;
        System.out.println(Integer.toBinaryString(value));
        System.out.println(Integer.toBinaryString(Bits.extractUnsigned(value,5,3)));
    }
}
