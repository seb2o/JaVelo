package ch.epfl.javelo;

/**
 * @author Edgar Gonzales (328095)
 */
public final class Bits {

    private Bits(){}

    /**
     * extrait du vecteur de 32 bits value la plage de length bits commençant au bit d'index start
     * interprètée comme une valeur signée en complément à deux, IllegalArgumentException si la plage est invalide
     * @param value le récéptacle du vecteur de bits, sous forme d'int
     * @param start le début de la plage à extraire, supérieure à 0
     * @param length la longueure de la plage à extraire, inférieure a 31 lorsque sommé a l'indice de début de la plage
     * @return la valeure signée du vecteur de bit extrait
     */
    public static int extractSigned(int value, int start, int length){
        Preconditions.checkArgument(start >= 0 & length >= 0 & start+length <= 31);
        value = value << 32 - (start + length);
        value = value >> 32 - length;
        return value;
    }

    /**
     * extrait du vecteur de 32 bits value la plage de length bits commençant au bit d'index start
     * IllegalArgumentException si la plage est invalide ou si le vecteur de bit à extraire est de taille 32
     * @param value le récéptacle du vecteur de bits, sous forme d'int
     * @param start le début de la plage à extraire, supérieure à 0
     * @param length la longueure de la plage à extraire, inférieure a 31 lorsque sommée a l'indice de début de la plage
     * @return la valeure toujours positive du vecteur de bit extrait
     */
    public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(start >= 0 & start+length <= 31 & length > 0 & length != 32);
        value = value >>> 32 - length;
        return value;
    }
}
