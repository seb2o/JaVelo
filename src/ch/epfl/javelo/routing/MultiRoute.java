package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MultiRoute implements Route{
    private final List<Route> segments;
    private final double totalLength;
    private final List<Edge> edges;

    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        int tLenght = 0;
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
            index += route.indexOfSegmentAt(position) + 1;
            position -= route.length();
            if(position <= 0){
                return index;
            }
        }
        return index;
    }

    @Override
    public double length() {
        return totalLength;
    }

    @Override
    public List<Edge> edges() {
        return edges;
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
        int index = this.indexOfSegmentAt(position);
        for (int i = 0; i < index; i++) {
            relativePosition -= segments.get(i).length();
        }
        return segments.get(index).pointAt(relativePosition);
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0,position,totalLength);
        double relativePosition = position;
        int index = this.indexOfSegmentAt(position);
        for (int i = 0; i < index; i++) {
            relativePosition -= segments.get(i).length();
        }
        return segments.get(index).elevationAt(relativePosition);
    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0,position,totalLength);
        double relativePosition = position;
        int index = this.indexOfSegmentAt(position);
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
            double loopPosition = edges.get(i).positionClosestTo(point);
            PointCh pointOnEdge = edges.get(i).pointAt(loopPosition);
            double loopDistance = point.squaredDistanceTo(pointOnEdge); //Todo : plutôt faire comme ça ou comme pour la première méthode ? Question sur piazza/assistants.

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
        return new RoutePoint(point, totalPosition + position, Math.sqrt(squaredDistanceToReference));
    }
}
