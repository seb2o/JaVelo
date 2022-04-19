package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Edgar Gonzalez (328095)
 * @author Sébastien Boo (345870)
 */
public final class TileManager {

    private final Path tilesPath;
    private final String servName;
    private final LinkedHashMap<TileId, Image> cache = new LinkedHashMap<>(100, 0.75f, true);

    public TileManager(Path tilePath, String servName) {
        this.servName = servName;
        this.tilesPath = tilePath;
    }

    public Image getImageOf(TileId tile)  {

        //si la tuile est stockée en cache, on la retourne et la fonction se termine
        Image tileImage = cache.get(tile);
        if (tileImage!=null) return tileImage;


        Path zoomDirectory = Path.of(String.valueOf(tile.zoomLevel()));
        Path xDirectory = Path.of(String.valueOf(tile.x()));
        Path imagePath = tilesPath
                .resolve(zoomDirectory)
                .resolve(xDirectory)
                .resolve(String.valueOf(tile.y()));

        //si la tuile est stockée sur le disque, on la stocke en cache et on la retourne
        try (FileInputStream f = new FileInputStream(imagePath.toString())) {
            Iterator<Map.Entry<TileId, Image>> i = cache.entrySet().iterator();
            if (i.hasNext()) {i.remove();}
            tileImage = new Image(f);
            cache.put(tile,tileImage);
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
            } catch (IOException ex) { //si erreur, alors souci de programmation
                throw new Error(ex);
            }
        }




        //todo check if zoom level directory is created, if it is then check if x coord directory is created, if it is, add the Image
        //todo if not, create the missing elements and add the image
        //todo then add it to the memory cache, normally it will remove the less used entry

        return null;
    }

    record TileId(int zoomLevel, int x, int y) {
        public static boolean isValid(int zoomLevel, int xIndex, int yIndex){
            int numberOfTiles = 1<<zoomLevel*2;
            return (0<=zoomLevel && 0<=xIndex && 0<=yIndex && zoomLevel<=19 && xIndex<numberOfTiles && yIndex<numberOfTiles);
        }
    }

}
