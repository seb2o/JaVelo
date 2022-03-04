package ch.epfl.javelo;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 */
public final class Preconditions {
    private Preconditions(){

    }

    /**
     * Vérifie une condition.
     *
     * @param shouldBeTrue la condition à vérifier.
     *
     * @throws IllegalArgumentException si la condition n'est pas vérifiée.
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }

    }
}
