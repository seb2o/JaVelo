package ch.epfl.javelo;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 */
public final class Preconditions {
    private Preconditions(){

    }

    /**
     * permet de vérifier une condition, en général la validité d'un argument d'une fonction
     * @param shouldBeTrue la condition à vérifier.
     * @throws IllegalArgumentException si la condition n'est pas vérifiée.
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }

    }
}
