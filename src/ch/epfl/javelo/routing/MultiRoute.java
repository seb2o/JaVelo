package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edgar Gonzalez (328095)
 * @author Sébastien Boo (345870)
 */
public final class MultiRoute implements Route{
    private final List<Route> segments;
    private final double totalLength;
    private final List<Edge> edges;

    /**
     * @param segments une liste de route (peut etre single ou multi route)
     */
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        double tLenght = 0;
        for (Route segment : this.segments) {
            tLenght += segment.length();
        }
        this.totalLength = tLenght;
        this.edges = new ArrayList<>();
        for (Route segment : segments) {
            this.edges.addAll(segment.edges());
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, this.length());
        int index = 0;
        for (Route route : segments) {
            if (position < route.length()) {
                return index + route.indexOfSegmentAt(position);
            }
            index += route.indexOfSegmentAt(position) + 1;
            position -= route.length();
        }

        return index-1;
    }

    @Override
    public double length() {
        return totalLength;
    }

    @Override
    public List<Edge> edges() {
        return List.copyOf(edges);
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> pointList = new ArrayList<>();
        List<Edge> edges = this.edges();
        pointList.add(edges.get(0).fromPoint());
        for (Edge edge: edges) {
            pointList.add(edge.toPoint());
        }
        return pointList;
    }


    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0,position,totalLength);
        double relativePosition = position;
        int index = this.indexOfExternalSegmentAt(position);
        for (int i = 0; i < index; i++) {
            relativePosition -= segments.get(i).length();
        }
        return segments.get(index).pointAt(relativePosition);
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0,position,totalLength);
        double relativePosition = position;
        int index = this.indexOfExternalSegmentAt(position);
        for (int i = 0; i < index; i++) {
            relativePosition -= segments.get(i).length();
        }
        return  segments.get(index).elevationAt(relativePosition);
    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0,position,totalLength);
        double relativePosition = position;
        int index = this.indexOfExternalSegmentAt(position);
        for (int i = 0; i < index; i++) {
            relativePosition -= segments.get(i).length();
        }
        return segments.get(index).nodeClosestTo(relativePosition);
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double position = Double.NaN;
        double squaredDistanceToReference = Double.POSITIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < edges.size(); i++) {
            double loopPosition = Math2.clamp(0,edges.get(i).positionClosestTo(point),edges.get(i).length());
            PointCh pointOnEdge = edges.get(i).pointAt(loopPosition);
            double loopDistance = point.squaredDistanceTo(pointOnEdge);

            if (loopDistance < squaredDistanceToReference) {
                squaredDistanceToReference = loopDistance;
                position = loopPosition;
                index = i;
            }
        }
        double totalPosition = 0;
        for (int i = 0; i < index; i++) {
            totalPosition += edges.get(i).length();
        }
        return new RoutePoint(edges.get(index).pointAt(position), totalPosition + position, Math.sqrt(squaredDistanceToReference));
    }

    /**
     * méthode interne qui permet de calculer a quel route interne appartient une position
     */
    private int indexOfExternalSegmentAt(double position) {
        position = Math2.clamp(0,position,totalLength);
        double previousSegmentsLenght = 0;
        for (int i = 0; i < segments.size(); i++) {
            previousSegmentsLenght+=segments.get(i).length();
            if (position < previousSegmentsLenght) {
                return i;
            }
        }
        return segments.size()-1;
    }

}
