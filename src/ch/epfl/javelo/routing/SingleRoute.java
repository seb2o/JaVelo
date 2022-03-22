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

    public SingleRoute(List<Edge> edges){
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges); //todo copie profonde ? Nécessite méthode .clone pour Edge car pas immuable.
        this.lengthList = new double[edges.size() + 1];
        this.lengthList[0] = 0;

        for (int i = 1; i < this.lengthList.length; i++) {
            this.lengthList[i] = this.lengthList[i-1] + edges.get(i-1).length();
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        if(position < 0){
            return 0;
        }

        double totalLength = 0;

        for (int i = 0; i < edges.size(); i++ ) {
            totalLength += edges.get(i).length();
            if (totalLength >= position){
                return i;
            }
        }
        //Retourne l'index le plus grand si la position dépasse la longueur max.
        return edges.size() - 1;
    }

    @Override
    public double length() {
        double totalLength = 0;
        for (Edge edge : edges) {
            totalLength += edge.length();
        }
        return totalLength;
    }

    @Override
    public List<Edge> edges() {
        return List.copyOf(edges); //todo même pb que pour le constructeur ?
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
        System.out.println("computed lenght : "+ length());
        position = Math2.clamp(0,position,length());
        double relativePosition;
        int index = Arrays.binarySearch(lengthList, position);
        System.out.println("index " + index);
        if(index >= 0){ //Si l'index correspond à un noeud.
            relativePosition = 0;
            if(index == lengthList.length - 1){ // Si c'est le dernier noeud, on prend la position à la fin de la dernière arête
                relativePosition = edges().get(edges.size()).length();
            }
        }
        else{
            index = - index - 2 ; //Donne l'index de l'arête correspondante.
            System.out.println(" shift :  "+ lengthList[index]);
            relativePosition = position - lengthList[index];
        }
        System.out.println(relativePosition);
        return edges.get(index).elevationAt(relativePosition);
    }

    @Override
    public int nodeClosestTo(double position) {
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
        if(relativePosition / edges.get(index).length() > .5){
            ++index; //On prend le noeud d'après si la position est à plus de la moitié de l'arête.
        }
        return index;
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
