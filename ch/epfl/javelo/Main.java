package ch.epfl.javelo;

public class Main
{
    public static void main(String[] args) {
        float[] samples = new float[]{-1f, 2f, -2.0f};
        System.out.println(0 + " " + Functions.sampled(samples,1).applyAsDouble(0));
        System.out.println( 1+ " " + Functions.sampled(samples,1).applyAsDouble(.1));
        System.out.println( 2+ " " + Functions.sampled(samples,1).applyAsDouble(.2));
        System.out.println( 3+ " " + Functions.sampled(samples,1).applyAsDouble(.3));
        System.out.println( 4+ " " + Functions.sampled(samples,1).applyAsDouble(.4));
        System.out.println( 5+ " " + Functions.sampled(samples,1).applyAsDouble(.5));
        System.out.println( 6+ " " + Functions.sampled(samples,1).applyAsDouble(.6));
        System.out.println( 7+ " " + Functions.sampled(samples,1).applyAsDouble(.7));
        System.out.println( 8+ " " + Functions.sampled(samples,1).applyAsDouble(.8));
        System.out.println( 9+ " " + Functions.sampled(samples,1).applyAsDouble(.9));
        System.out.println( 10+ " " + Functions.sampled(samples,1).applyAsDouble(1));

    }
}
