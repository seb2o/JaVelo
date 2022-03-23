package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SingleRoute implements Route{
    private final List<Edge> edges;
    private final double[] lengthList;
    private double totalLength;

    public SingleRoute(List<Edge> edges){
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges); //Pas besoin de copie profonde car Edge est immuable car Graph l'est aussi.
        this.lengthList = new double[edges.size() + 1];
        this.lengthList[0] = 0;

        for (int i = 1; i < this.lengthList.length; i++) {
            this.lengthList[i] = this.lengthList[i-1] + edges.get(i-1).length();
        }

        this.totalLength = 0;
        for (Edge edge : edges) {
            this.totalLength += edge.length();
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, this.length());
        double totalDistance = 0;
        int index = 0;
        for (Edge segment : edges) {
            totalDistance += segment.length();
            if (totalDistance > position){
                return index;
            }
            index++;
        }
        return -1; //Cette valeur ne devrait jamais être retournée grâce au clamp.
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
        pointList.add(edges.get(0).fromPoint());
        for (Edge edge: edges) {
            pointList.add(edge.toPoint());
        }
        return pointList;
    }

    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0,position,length());
        double relativePosition;
        int index = Arrays.binarySearch(lengthList, position);
        if(index >= 0){ //Si l'index correspond à un noeud.
            relativePosition = 0;
            if(index == lengthList.length - 1){ // Si c'est le dernier noeud, on prend la position à la fin de la dernière arête
                relativePosition = edges().get(edges.size()).length();
            }
        }
        else{
            index = -(index + 1); //Donne l'index de l'arête correspondante.
            relativePosition = position - lengthList[index];
        }

        return edges.get(index).pointAt(relativePosition);
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0,position,length());
        double relativePosition;
        int index = Arrays.binarySearch(lengthList, position);
        if(index >= 0){ //Si l'index correspond à un noeud.
            relativePosition = 0;
            if(index == lengthList.length - 1){ // Si c'est le dernier noeud, on prend la position à la fin de la dernière arête
                relativePosition = edges().get(edges.size()).length();
            }
        }
        else{
            index = - (index + 2) ; //Donne l'index de l'arête correspondante.
            relativePosition = position - lengthList[index];
        }
        return edges.get(index).elevationAt(relativePosition);
    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0,position,length());
        double relativePosition;
        int index = Arrays.binarySearch(lengthList, position);
        if(index >= 0){ //Si l'index correspond à un noeud.
            if(index == lengthList.length - 1){ // Si c'est le dernier noeud, on prend la position à la fin de la dernière arête
                return edges.get(index).toNodeId();
            }
            return edges.get(index).fromNodeId();
        }
        else{
            index = -(index + 2); //Donne l'index de l'arête correspondante.
            relativePosition = position - lengthList[index];
        }
        if(relativePosition / edges.get(index).length() > .5){
            ++index; //On prend le noeud d'après si la position est à plus de la moitié de l'arête.
        }
        return edges.get(index).fromNodeId();
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double position = Double.NaN;
        double squaredDistanceToReference = Double.POSITIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < edges.size(); i++) {
            double loopPosition = edges.get(i).positionClosestTo(point);
            PointCh pointOnEdge = edges.get(i).pointAt(loopPosition);
            double loopDistance = point.squaredDistanceTo(pointOnEdge);

            if(loopDistance < squaredDistanceToReference){
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