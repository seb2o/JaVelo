package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public Image getImageOf(TileId tile)  {


        if (!TileId.isValid(tile)) {
            System.out.println("invalid tile");
            return null;
        }


        //si la tuile est stockée en cache, on la retourne et la fonction se termine
        Image tileImage = cache.get(tile);
        if (tileImage!=null) {
            System.out.println("image was in cache");
            return tileImage;
        }


        Path zoomDirectory = cachePath
                .resolve(Path.of(String.valueOf(tile.zoomLevel())));
        Path xDirectory = zoomDirectory
                .resolve(Path.of(String.valueOf(tile.x())));
        Path imagePath = xDirectory.resolve(String.valueOf(tile.y())+".png");

        //si la tuile est stockée sur le disque, on la stocke en cache et on la retourne
        try (FileInputStream f = new FileInputStream(imagePath.toString())) {
            System.out.println("image was in disk");
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
                System.out.println("image was in remote database");
                InputStream i = c.getInputStream();

                if (Files.exists(xDirectory)) {
                    writeStream(i,imagePath);
                }

                else {
                    Files.createDirectories(xDirectory);
                    writeStream(i,imagePath);
                }


                try (FileInputStream is = new FileInputStream(imagePath.toFile())){
                    tileImage = cacheAndReturnTile(tile,is);
                }

                return tileImage;

            } catch (IOException ex) { //si erreur, alors souci de programmation
                throw new Error(ex);
            }
        }
    }

    //méthode interne pour gérer la mise à jour du cache
    private Image cacheAndReturnTile(TileId id, Image i) {
        Iterator<Map.Entry<TileId, Image>> iterator = cache.entrySet().iterator();
        if (iterator.hasNext()) {iterator.remove();}
        cache.put(id,i);
        return i;
    }

    //méthode interne pour gérer la mise à jour du cache
    private Image cacheAndReturnTile(TileId id, InputStream f) {
        return cacheAndReturnTile(id, new Image(f));
    }


    private void writeStream(InputStream i, Path path) throws IOException {
        FileOutputStream o = new FileOutputStream(path.toFile());
        i.transferTo(o);
        i.close();
        o.close();
    }

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

