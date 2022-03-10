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

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {


    private static final int OFFSET_EDGE_DIRECTION_AND_END_NODE_ID = 0;
    private static final int OFFSET_EDGE_LENGTH = OFFSET_EDGE_DIRECTION_AND_END_NODE_ID + Integer.BYTES;
    private static final int OFFSET_EDGE_HEIGHT_DIFF = OFFSET_EDGE_LENGTH + Short.BYTES;
    private static final int OFFSET_EDGE_ATTRIBUTES_ID = OFFSET_EDGE_HEIGHT_DIFF + Short.BYTES;
    private static final int EDGE_BYTES = OFFSET_EDGE_ATTRIBUTES_ID + Short.BYTES;


    /**
     * retourne vrai si l'arête d'identité donnée va dans le sens inverse de la voie OSM dont elle provient
     * @param edgeId l'identité de l'arête étudiée
     * @return booléen indiquant si l'arête va dans le sens de sa voie osm
     */
    public boolean isInverted(int edgeId){
        return edgesBuffer.getInt(EDGE_BYTES * edgeId + OFFSET_EDGE_DIRECTION_AND_END_NODE_ID) < 0;
    }

    /**
     * retourne l'identité du nœud destination de l'arête d'identité donnée
     * @param edgeId l'identité de l'arête étudiée
     * @return l'identité du nœud destination de l'arête d'identité donnée
     */
    public int targetNodeId(int edgeId){
        int id = edgesBuffer.getInt(EDGE_BYTES * edgeId + OFFSET_EDGE_DIRECTION_AND_END_NODE_ID);
        if(isInverted(edgeId)){return ~id;}
        else{return id;}
    }

    /**
     * retourne la longueure de l'arête d'identité donnée, en mètres
     * @param edgeId l'identité de l'arête étudiée
     * @return longueure de l'arête d'identité donnée, en mètres
     */
    public double length(int edgeId){
        return Q28_4.asDouble(edgesBuffer.getShort(EDGE_BYTES * edgeId + OFFSET_EDGE_LENGTH));
        //La conversion depuis le format Q28.4 convient pour le format Q12.4 car il y a autant de bits après la virgule, et moins avant la virgule (12<28).
    }

    //retourné au format Q12.4
    public double elevationGain(int edgeId){
        return Q28_4.asDouble(edgesBuffer.getShort(EDGE_BYTES * edgeId + OFFSET_EDGE_HEIGHT_DIFF));
        //La conversion depuis le format Q28.4 convient pour le format Q12.4 car il y a autant de bits après la virgule, et moins avant la virgule (12<28).
    }

    public boolean hasProfile(int edgeId){
        int profile = Bits.extractUnsigned(profileIds.get(edgeId),30,2);
        return profile != 0;
    }

    /**
     *  qui retourne le tableau des échantillons du profil de l'arête d'identité donnée, vide si l'arête ne possède pas de profil
     * @param edgeId l'identité de l'arête étudiée
     * @return tableau des échantillons du profil de l'arête d'identité donnée, vide si pas de profil
     */
    public float[] profileSamples(int edgeId){

        if(!hasProfile(edgeId)){
            return new float[0];
        }


        int firstProfileId = Bits.extractUnsigned(profileIds.get(edgeId),0,30);
        int compressionLevel = Bits.extractUnsigned(profileIds.get(edgeId),30,2);

        int numberOfProfiles = 1 + (int) Math.ceil(this.length(edgeId) / 2);
        float[] profileSamples = new float[numberOfProfiles];
        profileSamples[0] = Q28_4.asFloat(elevations.get(firstProfileId));

        switch (compressionLevel) {
            case 1 -> {
                for (int i = 1; i < numberOfProfiles; i++) {
                    profileSamples[i] = Q28_4.asFloat(elevations.get(firstProfileId + i));
                }
            }
            case 2 -> {
                for (int i = 1; i < numberOfProfiles; i++) {
                    profileSamples[i] = profileSamples[i - 1] + Q28_4.asFloat( Bits.extractSigned(elevations.get(firstProfileId + 1 + (i-1)/2),16 - 8 * (((i-1) % 2) + 1), 8 ));
                }
            }

            case 3 -> {
                for (int i = 1; i < numberOfProfiles; i++) {
                    profileSamples[i] = profileSamples[i - 1] + Q28_4.asFloat( Bits.extractSigned(elevations.get(firstProfileId + 1 + (i-1)/4),16 - 4 * (((i-1) % 4) + 1), 4 ));
                }
            }
        }


        if(isInverted(edgeId)){
            float[] invertedProfileSamples = new float[numberOfProfiles];
            for (int i = 0; i < numberOfProfiles ; i++) {
                invertedProfileSamples[(numberOfProfiles - 1) - i] = profileSamples[i];
            }
            return invertedProfileSamples;
        }
        return profileSamples;
    }

    /**
     *retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     * @param edgeId l'identité de l'arête étudiée
     * @return l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     */
    public int attributesIndex(int edgeId){
        return edgesBuffer.getShort(EDGE_BYTES * edgeId + OFFSET_EDGE_ATTRIBUTES_ID);
    }


}
