package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

//IMMUABLE
public final class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph; //Immuable
        this.costFunction = costFunction;//Immuable
    }

    /**
     * Si le nœud de départ et d'arrivée sont identiques, lève IllegalArgumentException.
     * @param startNodeId noeud de départ.
     * @param endNodeId noeud d'arrivée.
     * @return l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité endNodeId
     * dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe.
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {



        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                //Retourne 0 si ils sont égaux, retourne un entier <0 si this<that, > 0 sinon.
                return Float.compare(this.distance, that.distance);
            }
        }

        Preconditions.checkArgument(startNodeId != endNodeId);

        int nodeCount = graph.nodeCount();
        double[] distance = new double[nodeCount];
        int[] predecessor = new int[nodeCount];
        PriorityQueue<WeightedNode> inExploration = new PriorityQueue<>();

        Arrays.fill(distance, Double.POSITIVE_INFINITY);
        inExploration.add(new WeightedNode(startNodeId, 0f));
        distance[startNodeId] = 0;
        predecessor[startNodeId] = -1;//pas de prédécesseur pour le noeud de départ



        while (!inExploration.isEmpty()) {
            int N1 = inExploration.remove().nodeId;

            if (distance[N1] != Float.NEGATIVE_INFINITY) {

                if (N1 == endNodeId) {

                    /*construction de la liste des aretes de la route par le parcours dans le sens inverse des noeuds de
                    la route:  recherche des aretes sortantes du dernier noeud sans arete jusqu'à que l'une ,
                    qui existe forcément puisqu'elle a été empruntée pendant la création de la liste des nodes,
                    pointe vers le noeud d'avant, qui devient alors le dernier noeud sans arete*/
                    List<Edge> route = new ArrayList<>();
                    int currentNode = N1;
                    boolean edgeHasBeenFound;

                    while (predecessor[currentNode] != -1) {//tant que le noeud actuel est différent du noeud de départ
                        edgeHasBeenFound = false;
                        for (int i = 0; i < graph.nodeOutDegree(currentNode) && !edgeHasBeenFound; i++) {
                            int edgeId = graph.nodeOutEdgeId(currentNode, i);
                            if (predecessor[currentNode] == graph.edgeTargetNodeId(edgeId)) {//si l'edge considérée
                                //pointe sur le noeud précédent, on sait que c'est une edge de la route a constuire
                                edgeHasBeenFound = true;
                                route.add(Edge.of(graph, edgeId, predecessor[currentNode], currentNode));
                                currentNode = predecessor[currentNode];//on peut passer au node d'après, nécéssairement
                                //atteint par l'edge ajoutée à la route.
                            }
                        }
                    }
                    Collections.reverse(route);//la route est construite a partir de son arrivée, il faut la reverse
                    //pour l'avoir dans le bon sens
                    return new SingleRoute(route);
                }

                List<Integer> edgeIds = new ArrayList<>();
                for (int i = 0; i < graph.nodeOutDegree(N1); i++) {
                    edgeIds.add(graph.nodeOutEdgeId(N1, i));
                }
                for (Integer edgeId : edgeIds) {
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
             distance[N1] = Float.NEGATIVE_INFINITY;
            }
        }
        return null;
    }
}
