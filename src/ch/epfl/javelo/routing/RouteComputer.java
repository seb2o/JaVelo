//package ch.epfl.javelo.routing;
//
//import ch.epfl.javelo.Preconditions;
//import ch.epfl.javelo.data.Graph;
//
//import java.util.ArrayList;
//import java.util.List;
//
////IMMUABLE
//public class RouteComputer {
//
//    private final Graph graph;
//    private final CostFunction costFunction;
//
//    public RouteComputer(Graph graph, CostFunction costFunction){
//        this.graph = graph; //Immuable.
//        this.costFunction = costFunction;
//    }
//
//    /**
//     * Si le nœud de départ et d'arrivée sont identiques, lève IllegalArgumentException.
//     * @param startNodeId noeud de départ.
//     * @param endNodeId noeud d'arrivée.
//     * @return l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité endNodeId
//     * dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe.
//     */
//    public Route bestRouteBetween(int startNodeId, int endNodeId){
//
//        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
//            @Override
//            public int compareTo(WeightedNode that) {
//                //Retourne 0 si ils sont égaux, retourne un entier <0 si this<that, > 0 sinon.
//                return Float.compare(this.distance, that.distance);
//            }
//        }
//        Preconditions.checkArgument(startNodeId != endNodeId);
//
//        List<Integer> nodeList = List.of(5, 2, 17, 29, 33, 1, 8);
//
//        int nodeCount = graph.nodeCount();
//        float[] distance = new float[nodeCount]; //Todo : distance doit prendre en entrée une nodeId.
//        int[] predecessor = new int[nodeCount]; //Todo : idem
//
//        for (int i = 0; i < nodeCount; i++) { //Todo : remplacer par l'itération sur l'ensemble des nodes du Graph.
//            distance[i] = Float.POSITIVE_INFINITY;
//            predecessor[i] = 0;
//        }
//
//        distance[startNodeId] = 0;
//        List<Integer> inExploration = new ArrayList<>();
//        inExploration.add(startNodeId);
//        while(!inExploration.isEmpty()){
//            int N1 = inExploration.get(0);
//            for (Integer node : inExploration) {
//                if(distance[node] < distance[N]){
//                    N1 = node;
//                }
//                inExploration.remove((Object) N1);
//            }
//            if(N1 == endNodeId){
//                //Todo : end.
//            }
//            List<Integer> edgeIds = new ArrayList<>();
//            for (int i = 0; i < graph.nodeOutDegree(N1); i++) {
//                edgeIds.add(graph.nodeOutEdgeId(N1,i));
//            }
//            for (Integer edgeId : edgeIds) {
//                int N2 = graph.nodeOutEdgeId(N1,edgeId);
//                float d = distance[N1] + (float) graph.edgeLength(edgeId); //Todo : cast ici en float ou après ?
//                if(d < distance[N2]){
//                    distance[N2] = d;
//                    inExploration.add(N2);
//                }
//            }
//        }
//        //Todo : end.
//
//    }
//}
