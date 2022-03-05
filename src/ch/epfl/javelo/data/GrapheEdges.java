package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 * */

public record GrapheEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {


    private static final int OFFSET_EDGE_DIRECTION_AND_END_NODE_ID = 0;
    private static final int OFFSET_EDGE_LENGTH = OFFSET_EDGE_DIRECTION_AND_END_NODE_ID + Integer.BYTES;
    private static final int OFFSET_EDGE_HEIGHT_DIFF = OFFSET_EDGE_LENGTH + Short.BYTES;
    private static final int OFFSET_EDGE_ATTRIBUTES_ID = OFFSET_EDGE_HEIGHT_DIFF + Short.BYTES;
    private static final int EDGE_BYTES = OFFSET_EDGE_ATTRIBUTES_ID + Short.BYTES;


    public boolean isInverted(int edgeId){
        return edgesBuffer.getInt(EDGE_BYTES * edgeId + OFFSET_EDGE_DIRECTION_AND_END_NODE_ID) < 0;
    }

    int targetNodeId(int edgeId){
        return Bits.extractUnsigned(edgesBuffer.getInt(EDGE_BYTES * edgeId + OFFSET_EDGE_DIRECTION_AND_END_NODE_ID),0,31);
    }

    double length(int edgeId){
        return Q28_4.asDouble(edgesBuffer.getShort(EDGE_BYTES * edgeId + OFFSET_EDGE_LENGTH));
        //La conversion depuis le format Q28.4 convient pour le format Q12.4 car il y a autant de bits après la virgule, et moins avant la virgule (12<28).
    }

    //retourné au format Q12.4
    double elevationGain(int edgeId){
        return Q28_4.asDouble(edgesBuffer.getShort(EDGE_BYTES * edgeId + OFFSET_EDGE_HEIGHT_DIFF));
        //La conversion depuis le format Q28.4 convient pour le format Q12.4 car il y a autant de bits après la virgule, et moins avant la virgule (12<28).
    }

    boolean hasProfile(int edgeId){
        int profile = Bits.extractUnsigned(profileIds.get(edgeId),30,2);
        return profile == 0;
    }

    float[] profileSamples(int edgeId){
        int firstProfileId = Bits.extractUnsigned(profileIds.get(edgeId),0,30);
        int numberOfProfiles = 1 + (int) Math.floor(this.length(edgeId) / 2);
        float[] profileSamples = new float[numberOfProfiles];
        for (int i = 0; i < numberOfProfiles; i++) {
            profileSamples[i] = firstProfileId + i;
        }
        return profileSamples;
    }

    int attributesIndex(int edgeId){
        return edgesBuffer.getShort(EDGE_BYTES * edgeId + OFFSET_EDGE_ATTRIBUTES_ID);
    }


}
