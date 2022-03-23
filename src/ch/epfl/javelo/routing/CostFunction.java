package ch.epfl.javelo.routing;

public interface CostFunction {

    /**
     * Retourne le facteur par lequel la longueur de l'arête d'identité edgeId, partant du nœud d'identité nodeId, doit être multipliée.
     * Ce facteur doit impérativement être supérieur ou égal à 1.
     * @param nodeId noeud de départ de l'arrête.
     * @param edgeId arête dont la lonhueur va être multipilée par le coût.
     * @return le facteur multiplicateur.
     */
    double costFactor(int nodeId, int edgeId);

}
