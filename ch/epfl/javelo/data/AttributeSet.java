package ch.epfl.javelo.data;
import ch.epfl.javelo.Preconditions;
import java.util.StringJoiner;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 */
public record AttributeSet(long bits) {

    /**
     * construit un ensemble d'attributs en vérifiant que le paramètre donné correspond bien à un attributeSet valide
     * @param bits le set encodé sous la forme d'un vecteur de 62 bits, stocké dans un long
     */
    public AttributeSet{
        Preconditions.checkArgument(bits >>> 62 == 0);
    }

    /**
     * méthode de construction pour un AttributeSet qui encode un nombre indéfini d'attributs dans un vecteur de 62 bits
     * @param attributes un nombre indéfini d'attributes a encoder
     * @return un long contenant les attributs encodé
     */
    public static AttributeSet of(Attribute ... attributes){
        long bits = 0;
        if (attributes != null) {
            for (Attribute attribute : attributes) {
                if ((maskOf(attribute) & bits) == 0) {
                    bits += (1L << attribute.ordinal());
                }
            }
        }
        return new AttributeSet(bits);
    }

    /**
     * vérifie l'appartenance d'un attribut à l'instance courante
     * @param attribute l'attribut à chercher
     * @return vrai si l'attribut appartient déja à l'instance, faux sinon
     */
    public boolean contains(Attribute attribute){
        return (bits & maskOf(attribute))!=0;
    }

    /**
     * vérifie qu'un attributeSet partage des attributs avec l'instance de la classe
     * @param that le set d'attribut à vérifier
     * @return vrai si au moins un attribut est partagé
     */
    public boolean intersects(AttributeSet that){
        return (this.bits & that.bits)!=0;
    }

    /*
     * méthode interne utile à la gestion des attributeSets
     */
    private static long maskOf(Attribute attribute){
        return 1L << attribute.ordinal();
    }

    @Override
    public String toString(){
        StringJoiner str = new StringJoiner(",","{","}");
        for (Attribute attribute : Attribute.ALL) {
            if(this.contains(attribute)){
                str.add(attribute.toString());
            }
        }
        return str.toString();
    }
}
