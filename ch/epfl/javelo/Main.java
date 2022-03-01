package ch.epfl.javelo;

public class Main
{
    public static void main(String[] args) {
        int value = -2147483648;
        System.out.println(Integer.toBinaryString(value));
        System.out.println(Q28_4.ofInt(value));
    }
}
