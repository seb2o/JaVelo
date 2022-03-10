package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 */

public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;


    /**
     * retourne le nombre total de noeuds de l'instance de GraphNode
     * @return le nombre total de noeuds
     */
    public int count(){
        return buffer().capacity() / NODE_INTS;
    }

    /**
     * retourne la coordonnée E du noeud d'identité donnée
     * @param nodeId l'index dans le buffer des noeuds du noeud dont la coordonnée est recherchée
     * @return la coordonnée E du noeud
     */
    public double nodeE(int nodeId){
        return Q28_4.asDouble(buffer.get(NODE_INTS * nodeId + OFFSET_E));
    }

    /**
     * retourne la coordonnée N du noeud d'identité donnée
     * @param nodeId l'index dans le buffer des noeuds du noeud dont la coordonnée est recherchée
     * @return la coordonnée N du noeud
     */
    public double nodeN(int nodeId){
        return Q28_4.asDouble(buffer.get(NODE_INTS * nodeId + OFFSET_N));
    }

    /**
     * retourne le nombre d'arêtes sortant du noeud d'identité donné
     * @param nodeId l'identité du noeud concerné
     * @return  le nombre d'arêtes sortant du nœud d'identité donné
     */
    public int outDegree(int nodeId){
        return Bits.extractUnsigned((buffer.get(NODE_INTS * nodeId + OFFSET_OUT_EDGES)),28,4);
    }

    /**
     * retourne l'identité de l'arête à l'indice edgeIndex dans les arêtes sortant du noeud d'identité nodeId
     * @param nodeId le noeud dont l'identité est recherché
     * @param edgeIndex l'index dans le nombre d'arêtes sortant de l'arête dont l'identité est cherchée
     * @return l'identité de l'arête à l'indice edgeIndex dans les arêtes sortant du noeud d'identité nodeId
     */
    public int edgeId(int nodeId, int edgeIndex){
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return Bits.extractUnsigned((buffer.get(NODE_INTS * nodeId + OFFSET_OUT_EDGES)),0,28) + edgeIndex;
    }
}
