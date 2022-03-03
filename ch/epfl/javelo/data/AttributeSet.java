package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

public record AttributeSet(long bits) {
    public AttributeSet{
        Preconditions.checkArgument(bits >>> 62 == 0);
    }
    public static AttributeSet of(Attribute... attributes){

    }
    public boolean contains(Attribute attribute){

    }

    public boolean intersects(AttributeSet that){

    }

    @Override
    public String toString(){

    }
}
