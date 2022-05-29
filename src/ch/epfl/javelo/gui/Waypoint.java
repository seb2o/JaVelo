package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * @author Edgar Gonzalez (328095)
 * @author Sébastien Boo (345870)
 */

/*
* Enregistrement qui associe a une coordonnée son point le plus proche
 *  */
public record Waypoint(PointCh coordinates, int nodeId) {
}
