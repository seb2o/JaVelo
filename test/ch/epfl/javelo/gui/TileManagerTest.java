package ch.epfl.javelo.gui;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class TileManagerTest {

    @Test
    public void getImageOf(){
        TileManager m = new TileManager(Path.of("tiles"),"https://tile.openstreetmap.org");
        System.out.println(m.getImageOf(new TileManager.TileId(19,555,716)));
        System.out.println(m.getImageOf(new TileManager.TileId(19,555,716)));
    }


}
