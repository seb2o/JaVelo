package ch.epfl.javelo;

public class Main
{
    public static void main(String[] args) {
        float[] samples = new float[]{-1f, 2f, -2.0f};
        System.out.println(Functions.sampled(samples,1).applyAsDouble(0));
        System.out.println(Functions.sampled(samples,1).applyAsDouble(.2));
        System.out.println(Functions.sampled(samples,1).applyAsDouble(.4));
        System.out.println(Functions.sampled(samples,1).applyAsDouble(.5));
        System.out.println(Functions.sampled(samples,1).applyAsDouble(.6));
        System.out.println(Functions.sampled(samples,1).applyAsDouble(.8));
        System.out.println(Functions.sampled(samples,1).applyAsDouble(1));

    }
}
