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

/**
 * @author Edgar Gonzalez (345870)
 * @author Sébastien Boo (328095)
 */
public class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * constructeur utilisé uniquement par la méthode statique {@link ch.epfl.javelo.data.Graph#loadFrom(Path)}
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * construit une instance de graph a partir de fichiers binaires stocké au niveau de BAsePath
     * @param basePath le chemin d'accè du répèrtoire contenant les fichiers binaires des données du graphe
     * @return un nouveau graph initialisé avec les donnes des fichiers binaires
     * @throws IOException si le répertoire dont le chemin d'accès est spécifié ne fournis pas les fichiers suffisants.
     */
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

    /**
     * @return le nombre total de noeuds dans l'instance de graphe
     */
    public int nodeCount(){
        return nodes.count();
    }

    /**
     * @param nodeId l'identité du noeud dont la position est recherchée
     * @return la position du noeud d'identité donnée
     */
    public PointCh nodePoint(int nodeId){
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * @param nodeId l'identité du noeud dont le nombre d'aretes est recherché
     * @return  le nombre d'arêtes sortant du noeud d'identité donnée
     */
    public int nodeOutDegree(int nodeId){
        return nodes.outDegree(nodeId);
    }

    /**
     * @param nodeId l'identité du noeud dont l'identité de la ième arete est recherchée
     * @param edgeIndex la position danas le tableau des aretes sortantes de l'arete recherchée
     * @return l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex){
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * retourne l'identité du noeud se trouvant le plus proche du point donné, à la distance maximale donnée (en mètres),
     *  -1 si aucun nœud ne correspond à ces critères
     * @param point : le pointCh autour duquel s'effectue la recherche
     * @param searchDistance la distance maximale de recherche d'un noued autour du point
     * @return l'identité du noeud se trouvant le plus proche du point donné, à unedistance maximale donnée , -1 si pas de noeud
     */
    public int nodeClosestTo(PointCh point, double searchDistance){
        double squaredSearchDistance = searchDistance * searchDistance;
        int closestNodeId = -1;
        double squaredDistanceFromClosestNode = -1;
        List<GraphSectors.Sector> sectorList = sectors.sectorsInArea(point, searchDistance);

        for (GraphSectors.Sector sector : sectorList) {
            for (int nodeId = sector.startNodeId(); nodeId < sector.endNodeId() ; nodeId++) {
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

    /**
     * @param edgeId l'identité de l'arete dont on cherche le noeud d'arrivée
     * @return l'identité du noeud destination de l'arête d'identité donnée
     */
    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    /**
     * retourne vrai si l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient
     * @param edgeId l'arete concernée
     * @return vrai si l'arete va dans le meme sens que la voie OSM associée
     */
    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);
    }

    /**
     * @param edgeId l'identité de l'arete concernée
     * @return l'ensemble des attributs OSM attachés à l'arête d'identité donnée
     */
    public AttributeSet edgeAttributes(int edgeId){
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * @param edgeId l'identité de l'arete concernée
     * @return la longueur, en mètres, de l'arête d'identité donnée
     */
    public double edgeLength(int edgeId){
        return edges.length(edgeId);
    }

    /**
     * retourne le dénivelé positif total de l'arête d'identité donnée
     * @param edgeId l'identité de l'arete recherchée
     * @return le dénivelé positif de l'arete
     */
    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    /**
     * retourne le profil en long de l'arête d'identité donnée, sous la forme d'une fonction;
     * si l'arête ne possède pas de profil, la fonction retournée retourne Double.NaN pour tout arguement.
     * @param edgeId l'identité de l'arete dont on veut obtenir le profil
     * @return une fonction qui retourne la hauteur d'un point en fonction de sa distance par rapport au début de l'arete
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        if (!edges.hasProfile(edgeId)){
            return Functions.constant(Double.NaN);
        }
        return Functions.sampled(edges.profileSamples(edgeId), edges.length(edgeId));
    }
}
