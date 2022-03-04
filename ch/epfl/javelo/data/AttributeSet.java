package ch.epfl.javelo.data;
import ch.epfl.javelo.verification.Preconditions;

import java.util.StringJoiner;

public record AttributeSet(long bits) {

    public AttributeSet{
        Preconditions.checkArgument(bits >>> 62 == 0);
    }

    public static AttributeSet of(Attribute ... attributes){
        long bits = 0;
        for (Attribute attribute:attributes) {
            if ((maskOf(attribute) & bits) == 0) {//vérifie l'absence de redondance dans la liste d'attribut
                bits += (1L << attribute.ordinal());
            }
        }
        return new AttributeSet(bits);
    }

    public boolean contains(Attribute attribute){
        return (bits & maskOf(attribute))!=0;
    }

    private static long maskOf(Attribute attribute){
        return 1L << attribute.ordinal();
    }

    public boolean intersects(AttributeSet that){
        for (Attribute attribute : Attribute.ALL) {
            if(that.contains(attribute) && this.contains(attribute)){
                return true;
            }
        }
        return false;
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