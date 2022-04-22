package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Edgar Gonzalez (328095)
 * @author Sébastien Boo (345870)
 */
public final class TileManager {

    private final Path cachePath;
    private final String servName;
    private final LinkedHashMap<TileId, Image> cache = new LinkedHashMap<>(100, 0.75f, true);

    public TileManager(Path tilePath, String servName) {
        this.servName = servName;
        this.cachePath = tilePath;
    }

    public Image getImageOf(TileId tile) throws IOException {

        //on vérifie que la tuile est valide
        if (!TileId.isValid(tile)) {
            throw  new IOException();
        }


        //si la tuile est stockée en cache, on la retourne et la fonction se termine
        Image tileImage = cache.get(tile);
        if (tileImage!=null) {
            return tileImage;
        }


        Path zoomDirectory = cachePath
                .resolve(Path.of(String.valueOf(tile.zoomLevel())));
        Path xDirectory = zoomDirectory
                .resolve(Path.of(String.valueOf(tile.x())));
        Path imagePath = xDirectory.resolve(tile.y() +".png");

        //si la tuile est stockée sur le disque, on la stocke en cache et on la retourne
        try (FileInputStream f = new FileInputStream(imagePath.toString())) {
            tileImage = cacheAndReturnTile(tile,f);
            return tileImage;
        }
        catch (IOException e) {//si erreur, alors le fichier n'existe pas dans la base de données
            try {
                URL u = new URL(String.format("%s/%d/%d/%d.png",
                        servName,
                        tile.zoomLevel(),
                        tile.x(),
                        tile.y()));
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "JaVelo");
                InputStream i = c.getInputStream();

                if (Files.exists(xDirectory)) {
                    writeStream(i,imagePath);
                }
                else {
                    Files.createDirectories(xDirectory);
                    writeStream(i,imagePath);
                }

                //on retourne le fichier depuis le disque
                try (FileInputStream is = new FileInputStream(imagePath.toFile())){
                    tileImage = cacheAndReturnTile(tile,is);
                }
                return tileImage;

            } catch (IOException ex) { //si erreur, alors souci de programmation
                ex.printStackTrace();
                throw new IOException();
            }
        }
    }

    //méthode interne pour gérer la mise à jour du cache
    private Image cacheAndReturnTile(TileId id, Image i) {
        Iterator<Map.Entry<TileId, Image>> iterator = cache.entrySet().iterator();
        if ( cache.size()>=100 && iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        cache.put(id,i);
        return i;
    }

    //méthode interne pour gérer la mise à jour du cache
    private Image cacheAndReturnTile(TileId id, InputStream f) {
        return cacheAndReturnTile(id, new Image(f));
    }

    //méthode interne permettant d'écrire dans un fichier depuis un inputStream
    private void writeStream(InputStream i, Path path) throws IOException {
        FileOutputStream o = new FileOutputStream(path.toFile());
        i.transferTo(o);
        i.close();
        o.close();
    }

    /**
     * représente une tile avec son zoom level et ses coordonnées par rapport
     * au coin haut gauche de la carte
     * fournis également une méthode permettant de vérifier la validité d'une tuile
     * @param zoomLevel le zoomLevel de la tile
     * @param x son décalage vers la droite
     * @param y son décalage vers le bas
     */
    record TileId(int zoomLevel, int x, int y) {

        public static boolean isValid(int zoomLevel, int xIndex, int yIndex){
            long numberOfTiles = 1L<<zoomLevel*2;
            return (0<=zoomLevel && 0<=xIndex && 0<=yIndex && zoomLevel<=19 && xIndex<numberOfTiles && yIndex<numberOfTiles);
        }

        public static boolean isValid(TileId tileId) {
            return isValid(tileId.zoomLevel(), tileId.x(), tileId.y());
        }
    }

}

