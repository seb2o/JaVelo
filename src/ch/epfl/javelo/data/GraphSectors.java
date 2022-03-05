package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * @author Gonzalez Edgar (328095)
 * @author Boo Sebastien (345870)
 */

public record GraphSectors(ByteBuffer buffer) {
    public static final double SECTOR_E_SIZE = (SwissBounds.WIDTH)/128;
    public static final double SECTOR_N_SIZE = (SwissBounds.HEIGHT)/128; //(128x128) secteurs.

    public ArrayList<Sector> sectorsInArea(PointCh center, double distance){ //todo arrayList ou List?

        ArrayList<Sector> sectors = new ArrayList<>();

        double eMinCorner = center.e() - distance;
        double eMaxCorner = center.e() + distance;
        double nMinCorner = center.n() - distance;
        double nMaxCorner = center.n() + distance;

        return null; //todo à completer
    }


    record Sector(int startNodeId,int endNodeId){

        private int pointChSectorId(PointCh point){
            double eToBorder = point.e() - SwissBounds.MIN_E;
            double nToBorder = point.n() - SwissBounds.MIN_N;

            int eIndex = Math.min( (int) Math.floor(eToBorder / SECTOR_E_SIZE), 127);
            //Remarque : utilisation de la fonction min ici, car pour le cas extrème où eToBorder vaut Swissbounds.WIDTH, on aurait 128.
            // (on veut une valeur entre 0 et 127 pour le prochain calcul.)
            int nIndex = Math.min( (int) Math.floor(nToBorder / SECTOR_N_SIZE), 127);

            return eIndex + 128 * nIndex;
        }
    }
}

