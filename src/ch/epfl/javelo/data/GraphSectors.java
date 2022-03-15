package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 */

public record GraphSectors(ByteBuffer buffer) {
    public static final double SECTOR_E_SIZE = (SwissBounds.WIDTH)/128;
    public static final double SECTOR_N_SIZE = (SwissBounds.HEIGHT)/128; //(128x128) secteurs.
    private static final int OFFSET_FIRST_NODE_ID = 0;
    private static final int OFFSET_NUMBER_OF_NODES = OFFSET_FIRST_NODE_ID + Integer.BYTES;
    private static final int SECTOR_BYTES = OFFSET_NUMBER_OF_NODES + Short.BYTES;

    /**
     * retourne la liste de tous les secteurs superposés avec le carré centré au point donné, de côté distance*2;
     * @param center le centre du carré a considérer
     * @param distance la distance au centre du carré des cotés du carré
     * @return liste de tous les secteurs superposés avec le carré centré au point donné, de côté distance*2;
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){

        ArrayList<Sector> sectors = new ArrayList<>();
        ArrayList<Integer> sectorsId = new ArrayList<>();

        //coins du carré où sont recherchés les secteurs. ramené aux limites de la suisse si ils dépassent
        double eMin = Math.max(center.e() - distance,SwissBounds.MIN_E);
        double eMax = Math.min(center.e() + distance,SwissBounds.MAX_E);
        double nMin = Math.max(center.n() - distance, SwissBounds.MIN_N);
        double nMax = Math.min(center.n() + distance,SwissBounds.MAX_N);
        int bottomLeftId = pointChSectorId(new PointCh(eMin,nMin));
        int bottomRightId = pointChSectorId(new PointCh(eMax,nMin));
        int topLeftId = pointChSectorId(new PointCh(eMin,nMax));

        int c = 0;
        for (int n = bottomLeftId; n <= topLeftId ; n+=128) {
            for (int e = bottomLeftId; e <= bottomRightId; e++) {
                sectorsId.add(e + 128*c);
            }
            c++;
        }

        for (int id : sectorsId) {
            sectors.add(new Sector(startNodeId(id), startNodeId(id) + numberOfNode(id)));
        }

        return sectors;
    }

    //methode interne permettant de trouver l'identité du secteur d'un point ch1903
    private int pointChSectorId(PointCh point){
        double eToBorder = point.e() - SwissBounds.MIN_E;
        double nToBorder = point.n() - SwissBounds.MIN_N;

        int eIndex = Math.min( (int) Math.floor(eToBorder / SECTOR_E_SIZE), 127);
        //Remarque : utilisation de la fonction min ici, car pour le cas extrème où eToBorder vaut Swissbounds.WIDTH, on aurait 128.
        // (on veut une valeur entre 0 et 127 pour le prochain calcul.)
        int nIndex = Math.min( (int) Math.floor(nToBorder / SECTOR_N_SIZE), 127);

        return eIndex + 128 * nIndex;
    }

    //methode interne retourant l'identité du premier noeud d'un secteur
    private int startNodeId(int sectorId){
        return buffer.getInt(sectorId * SECTOR_BYTES + OFFSET_FIRST_NODE_ID);
    }

    //methode interne retournant le nombre de noeuds d'un secteur donné
    private int numberOfNode(int sectorId){
        return Short.toUnsignedInt( buffer.getShort(sectorId * SECTOR_BYTES + OFFSET_NUMBER_OF_NODES));
    }

    public record Sector(int startNodeId,int endNodeId){}
}

