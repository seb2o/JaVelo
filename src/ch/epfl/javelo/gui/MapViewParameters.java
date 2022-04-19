package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * @author Edgar Gonzalez (328095)
 * @author Sébastien Boo (345870)
 */
public record MapViewParameters(int zoomLevel, double originX, double originY) {
//todo attribut constant hors des parametres du record ?
    //todo methodes pointAt, viewX et viewY en pixels  ?
    //todo vérifier les zoom levels ?
    /**
     * permet d'obtenir les coordonnées du coin haut-gauche de la map
     * @return les coordonnées du coin haut-gauche de la map
     */
    public Point2D topLeft() {
        return new Point2D(this.originX(),this.originY());
    }


    /**
     * crée une nouvelle instance de mapViewParameter de meme zoom que l'instance
     * courante mais d'origine différente
     * @param x la coordonnée X de la nouvelle origine,
     *          au zoomLevel de l'instance courante
     * @param y la coordonnée Y de la nouvelle origine,
     *          au zoomLevel de l'instance courante
     * @return un MapViewParameter de zoom level identique à l'instance courante
     * et d'origine spécifiée par les paramètres
     */
    public MapViewParameters withMinXY(double x, double y) {
        return new MapViewParameters(this.zoomLevel(),x,y);
    }

    /**
     * permet de convertir des coordonnées d'image en coordonnées mercator
     * @param x le nombre de pixels vers la droite de l'image du point
     * @param y le nombre de pixels vers le bas de l'image du point
     * @return le pointWebMercator correspondant a celui des coordonnées spécifiées pour l'image
     */
    public PointWebMercator pointAt(double x, double y) {
        double pixelsWidth = 1 << (8 + this.zoomLevel());
        return PointWebMercator.of(this.zoomLevel(), x/pixelsWidth+this.originX(), y/pixelsWidth+this.originY());
    }

    /**
     * permet la conversion des coordonnées mercator dans les coordonnées de l'image
     * @param p le point a convertir
     * @return le nombre de pixels vers la droite de l'image du point p
     */
    public double viewX(PointWebMercator p) {
        double pixelsWidth = 1 << (8 + this.zoomLevel());
        return (p.xAtZoomLevel(this.zoomLevel())-this.originX())*pixelsWidth;
    }

    /**
     * permet la conversion des coordonnées mercator dans les coordonnées de l'image
     * @param p le point a convertir
     * @return le nombre de pixels vers le bas de l'image du point p
     */
    public double viewY(PointWebMercator p) {
        double pixelsWidth = 1 << (8 + this.zoomLevel());
        return (p.yAtZoomLevel(this.zoomLevel())-this.originY())*pixelsWidth;
    }
}
