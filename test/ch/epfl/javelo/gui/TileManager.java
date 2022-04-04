package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;

/**
 * @author Edgar Gonzalez (328095)
 * @author Sébastien Boo (345870)
 */
public final class TileManager {

    private final Path tileStoragePath;
    private final String servName;
    private final LinkedHashMap<TileId,Image> loadedImages = new LinkedHashMap<>(100,0,true);

    public TileManager(Path tilePath, String servName) {
        this.servName = servName;
        this.tileStoragePath = tilePath;
    }

    public Image getImageOf(TileId tileId) throws IOException {

        Image imageOfTile = loadedImages.get(tileId);

        if (imageOfTile!=null) {
            return imageOfTile;
        }

        //todo Use String.Format() je crois c'est plus propre
        Path imagePath = Path.of(tileStoragePath.toString()+tileId.zoomLevel()+tileId.y()+tileId.x+".png");

        if (exists(imagePath)) {//todo try catch a la place ? permet de virer le "throws filenotfound mais est plus lourd que le if
            FileInputStream imageStream = new FileInputStream(imagePath.toString());
            return new Image(imageStream);
        }

        URL u = new URL(servName+"/"+tileId.zoomLevel()+"/"+tileId.x()+"/"+tileId.y()+".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        InputStream i = c.getInputStream();
        //todo check if zoom level directory is created, if it is then check if x coord directory is created, if it is, add the Image
        //todo if not, create the missing elements and add the image
        //todo then add it to the memory cache, normally it will remove the less used entry

        return null;
    }

    record TileId(int zoomLevel, int x, int y) {
        public static boolean isValid(int zoomLevel, int xIndex, int yIndex){
            int numberOfTiles = (int) Math.pow(2,zoomLevel);
            return (0<=zoomLevel && 0<=xIndex && 0<=yIndex && zoomLevel<=19 && xIndex<=numberOfTiles-1 && yIndex<=numberOfTiles-1);
        }
    }

}

