package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

//IMMUABLE
public class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph; //Immuable.
        this.costFunction = costFunction;
    }

    /**
     * Si le nœud de départ et d'arrivée sont identiques, lève IllegalArgumentException.
     * @param startNodeId noeud de départ.
     * @param endNodeId noeud d'arrivée.
     * @return l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité endNodeId
     * dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe.
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId){

        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                //Retourne 0 si ils sont égaux, retourne un entier <0 si this<that, > 0 sinon.
                return Float.compare(this.distance, that.distance);
            }
        }
        Preconditions.checkArgument(startNodeId != endNodeId);

        int nodeCount = graph.nodeCount();
        float[] distance = new float[nodeCount];
        int[] predecessor = new int[nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            distance[i] = Float.POSITIVE_INFINITY;
            predecessor[i] = 0; //Todo : par défaut déjà 0 ?
        }
        distance[startNodeId] = 0;
        predecessor[startNodeId] = -1;

        PriorityQueue<WeightedNode> inExploration = new PriorityQueue<>();
        inExploration.add(new WeightedNode(startNodeId,0f));

        while(!inExploration.isEmpty()){
            int N1 = inExploration.remove().nodeId;
            if(N1 == endNodeId){

                List<Edge> route = new ArrayList<>();

                int currentNode = N1;
                while(predecessor[currentNode] != -1){

                    for (int i = 0; i < graph.nodeOutDegree(currentNode); i++) {
                        int edgeId = graph.nodeOutEdgeId(currentNode,i);
                        if(predecessor[currentNode] == graph.edgeTargetNodeId(edgeId)){
                            route.add(Edge.of(graph,edgeId,predecessor[currentNode],currentNode));
                            currentNode = predecessor[currentNode];
                            break;
                        }
                    }
                }
                Collections.reverse(route); //Todo : le reverse change rien au chemin affiché mais peut etre pas au chemin "réel".
                return new SingleRoute(route);
            }
            List<Integer> edgeIds = new ArrayList<>();
            for (int i = 0; i < graph.nodeOutDegree(N1); i++) {
                edgeIds.add(graph.nodeOutEdgeId(N1,i));
            }
            for (Integer edgeId : edgeIds) {
                int N2 = graph.edgeTargetNodeId(edgeId);
                if(distance[N2] == Float.NEGATIVE_INFINITY){
                    System.out.println("NOT");
                    float d = distance[N1] + (float) (graph.edgeLength(edgeId) * costFunction.costFactor(N2,edgeId)); //Todo : cast ici en float ou après ?
                    if(d < distance[N2]){
                        distance[N2] = d;
                        predecessor[N2] = N1;
                        inExploration.add(new WeightedNode(N2,d));
                    }
                }
            }
            distance[N1] = Float.NEGATIVE_INFINITY;
        }
        return null;
    }
}
