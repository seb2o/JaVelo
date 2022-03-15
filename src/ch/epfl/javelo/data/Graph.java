package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/*
Définition de classe immuable :
Pour qu'une classe soit immuable, il faut qu'elle satisfasse les conditions suivantes :
- Tous ses attributs sont finaux (final), initialisés lors de la construction et jamais modifiés par la suite,
- Toute valeur non immuable fournie à son constructeur est copiée en profondeur avant d'être stockée dans un de ses attributs,
- Aucune valeur non immuable stockée dans un de ses attributs n'est fournie à l'extérieur, p.ex. par un accesseur : soit chacune de ces valeurs non immuable est rendue non modifiable avant d'être fournie à l'extérieur, soit seule une copie profonde est fournie.
 */
public class Graph {

    final GraphNodes nodes;
    final GraphSectors sectors;
    final GraphEdges edges;
    final List<AttributeSet> attributeSets;

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = attributeSets; //todo copie en profondeur à faire car immuable ? Ajouter une méthode clone aux classes ?
    }

    public static Graph loadFrom(Path basePath) throws IOException {
        Path nodesPath, sectorsPath, edgesPath, attributeSetsPath, profilesIdsPath, elevationsPath;

        try{
            nodesPath = basePath.resolve("nodes.bin");
            sectorsPath = basePath.resolve("sectors.bin");
            edgesPath = basePath.resolve("edges.bin");
            attributeSetsPath = basePath.resolve("attributes.bin");
            profilesIdsPath = basePath.resolve("profile_ids.bin");
            elevationsPath = basePath.resolve("elevations.bin");
        }
        catch (Exception e){
            throw new IOException();
        }
        GraphNodes nodes ;
        GraphSectors sectors;
        GraphEdges edges;
        List<AttributeSet> attributeSets;

        try (FileChannel nodesChannel = FileChannel.open(nodesPath);
             FileChannel sectorsChannel = FileChannel.open(sectorsPath);
             FileChannel edgesChannel = FileChannel.open(edgesPath);
             FileChannel attributesSetChannel = FileChannel.open(attributeSetsPath);
             FileChannel profilesIdsChannel = FileChannel.open(profilesIdsPath);
             FileChannel elevationsChannel = FileChannel.open(elevationsPath)) {

            nodes = new GraphNodes(nodesChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, nodesChannel.size())
                    .asIntBuffer());

            sectors = new GraphSectors(sectorsChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, sectorsChannel.size()));

            edges = new GraphEdges(
                    edgesChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, edgesChannel.size()),
                    profilesIdsChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, profilesIdsChannel.size())
                    .asIntBuffer(),
                    elevationsChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, elevationsChannel.size())
                    .asShortBuffer());

            LongBuffer attributeSetsBuffer = attributesSetChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, attributesSetChannel.size())
                    .asLongBuffer();

            attributeSets = new ArrayList<>();
            for (int i = 0; i < attributeSetsBuffer.capacity(); i++) {
                attributeSets.add(new AttributeSet(attributeSetsBuffer.get(i)));
                }
            }

        return new Graph(nodes,sectors,edges,attributeSets);
        }

    public int nodeCount(){
        return nodes.count();
    }

    public PointCh nodePoint(int nodeId){
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    public int nodeOutDegree(int nodeId){
        return nodes.outDegree(nodeId);
    }

    public int nodeOutEdgeId(int nodeId, int edgeIndex){
        return nodes.edgeId(nodeId, edgeIndex);
    }

    public int nodeClosestTo(PointCh point, double searchDistance){
        double squaredSearchDistance = searchDistance * searchDistance;
        int closestNodeId = -1;
        double squaredDistanceFromClosestNode = -1;
        List<GraphSectors.Sector> sectorList = sectors.sectorsInArea(point, searchDistance);

        for (GraphSectors.Sector sector : sectorList) {
            for (int nodeId = sector.startNodeId(); nodeId <= sector.endNodeId() ; nodeId++) {
                double squaredDistance = point.squaredDistanceTo(nodePoint(nodeId));
                if(squaredDistance <= squaredSearchDistance &&
                  (closestNodeId == -1 || squaredDistance <= squaredDistanceFromClosestNode)){

                    squaredDistanceFromClosestNode = squaredDistance;
                    closestNodeId = nodeId;
                }
            }
        }
        return closestNodeId;
    }

    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);
    }

    public AttributeSet edgeAttributes(int edgeId){
        return attributeSets.get(edges.attributesIndex(edgeId)); //todo pas sûr de moi mais je pense que c'est ça.
    }

    public double edgeLength(int edgeId){
        return edges.length(edgeId);
    }

    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    public DoubleUnaryOperator edgeProfile(int edgeId){
        if (!edges.hasProfile(edgeId)){
            return Functions.constant(Double.NaN);
        }
        return Functions.sampled(edges.profileSamples(edgeId), edges.length(edgeId)); //todo la façon de choisir xMax est pas précisée dans le pdf mais ça me semble logique de faire ça comme ça.
    }
}
