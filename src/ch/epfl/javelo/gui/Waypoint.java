package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.projection.PointCh;

/**
 * @author Edgar Gonzalez (328095)
 * @author Sébastien Boo (345870)
 */
public record Waypoint(PointCh waypoint, int closestNodeId) {
}
