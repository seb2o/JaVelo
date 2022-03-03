package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Attribute;

import java.util.StringJoiner;

public record AttributeSet(long bits) {
    public AttributeSet{
        Preconditions.checkArgument(bits >>> 62 == 0);
    }
    public static AttributeSet of(Attribute... attributes){ //todo 2 fois le même attribut -> error ?
        long bits = 0;
        for (Attribute attribute:attributes) {
            bits += Math.scalb(1,attribute.ordinal());
        }
        return new AttributeSet(bits);
    }
    public boolean contains(Attribute attribute){
        int shift = attribute.ordinal();
        long bits = this.bits;
        bits = bits << (63 - attribute.ordinal());
        bits = bits >>> 63;
        return bits == 1;
    }

    private long maskOf(Attribute attribute){   //todo utiliser maskOf pour contains ? (voir conseil de programmation)
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
