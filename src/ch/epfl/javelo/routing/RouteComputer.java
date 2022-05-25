package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

/**
 * @author Edgar Gonzalez (328095)
 * @author Sébastien Boo (345870)
 */public final class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph; //Immuable
        this.costFunction = costFunction;//Immuable
    }

    /**
     * Si le noeud de départ et d'arrivée sont identiques, lève IllegalArgumentException.
     * @param startNodeId noeud de départ.
     * @param endNodeId noeud d'arrivée.
     * @return l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité endNodeId
     * dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe.
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        Preconditions.checkArgument(startNodeId != endNodeId);

        //création des variables nécessaires au calcul de la mailleure route
        //nodeCount : le nombre de noeuds dans le graph
        //distance[] le tableau des distances de chaque noeud de l'itinéraire par rapport a son prédécésseur
        //predecessor[] la liste des noeuds dans le chemin
        //inExploration la liste des noeuds atteignables a partir d'un noued via une seule arête
        int nodeCount = graph.nodeCount();
        double[] distance = new double[nodeCount];
        int[] predecessor = new int[nodeCount];
        PriorityQueue<WeightedNode> inExploration = new PriorityQueue<>();

        //initialisation des variables
        //les distances avant calcul sont trop grandes pour être atteintes
        //les noeuds a explorer sont le noeud de départ
        //la distance entre le départ et le départ vaut 0
        //pas de prédécesseur pour le noeud de départ
        Arrays.fill(distance, Double.POSITIVE_INFINITY);
        inExploration.add(new WeightedNode(startNodeId, 0f));
        distance[startNodeId] = 0;
        predecessor[startNodeId] = -1;

        //on boucle tant qu'il reste des noeuds du graph à explorer
        while (!inExploration.isEmpty()) {

            int N1 = inExploration.remove().nodeId;

            //si ce noeud n'a pas déja été rejeté
            if (distance[N1] != Float.NEGATIVE_INFINITY) {


                if (N1 == endNodeId) {

                    /*construction de la liste des aretes de la route par le parcours dans le sens inverse des noeuds de
                    la route:  recherche des aretes sortantes du dernier noeud sans arete jusqu'à que l'une ,
                    qui existe forcément puisqu'elle a été empruntée pendant la création de la liste des nodes,
                    pointe vers le noeud d'avant, qui devient alors le dernier noeud sans arete*/
                    List<Edge> routeEdges = new ArrayList<>();
                    int currentNode = N1;
                    boolean edgeHasBeenFound;

                    while (predecessor[currentNode] != -1) {//tant que le noeud actuel est différent du noeud de départ
                        edgeHasBeenFound = false;
                        for (int i = 0; i < graph.nodeOutDegree(currentNode) && !edgeHasBeenFound; i++) {
                            int edgeId = graph.nodeOutEdgeId(currentNode, i);
                            if (predecessor[currentNode] == graph.edgeTargetNodeId(edgeId)) {//si l'edge considérée
                                //pointe sur le noeud précédent, on sait que c'est une edge de la route a constuire
                                edgeHasBeenFound = true;
                                routeEdges.add(Edge.of(graph, edgeId, predecessor[currentNode], currentNode));
                                currentNode = predecessor[currentNode];//on peut passer au node d'après, nécéssairement
                                //atteint par l'edge ajoutée à la route.
                            }
                        }
                    }
                    Collections.reverse(routeEdges);//la route est construite a partir de son arrivée, il faut la reverse
                    //pour l'avoir dans le bon sens
                    return new SingleRoute(routeEdges);
                }


                //parcours des edges sortant d'un noeud pour leurs associer leurs poid
                for (Integer edgeId : OutEdgeIds(N1)) {
                    int N2 = graph.edgeTargetNodeId(edgeId);
                    double d = distance[N1] + (graph.edgeLength(edgeId) * costFunction.costFactor(N2, edgeId));
                    if (d < distance[N2]) {
                        distance[N2] = d;
                        predecessor[N2] = N1;
                        inExploration.add(
                                new WeightedNode(
                                        N2,
                                        (float)(d + graph.nodePoint(N2).distanceTo(graph.nodePoint(endNodeId)))
                                )
                        );
                    }
                }
                //tout les noeuds sortant de N1 ont été pesé, il ne sert plus a rien de le considérer
                distance[N1] = Float.NEGATIVE_INFINITY;
            }
        }
        return null;
    }

    record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            //Retourne 0 si ils sont égaux, retourne un entier <0 si this<that, > 0 sinon.
            return Float.compare(this.distance, that.distance);
        }
    }

    /**
     * méthode interne retournant les id des aretes sortantes d'un noeud
     * @param nodeId l'id du noeud
     * @return une liste d'identité d'arêtes
     */
    private List<Integer> OutEdgeIds(int nodeId){
        List<Integer> edgeIds = new ArrayList<>();
        for (int i = 0; i < graph.nodeOutDegree(nodeId); i++) {
            edgeIds.add(graph.nodeOutEdgeId(nodeId, i));
        }
        return edgeIds;
    }

}
