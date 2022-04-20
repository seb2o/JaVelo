package ch.epfl.javelo.gui;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class TileManagerTest {

    @Test
    public void getImageOf(){
        TileManager m = new TileManager(Path.of("tiles"),"https://tile.openstreetmap.org");
        for (int i = 1; i < 150 ; i++) {
            m.getImageOf(new TileManager.TileId(19,555,i));
        }
        for (int i = 50; i < 151 ; i++) {
            m.getImageOf(new TileManager.TileId(19,555,i));
        }

    }
    @Test
    public void getImageOfTest2(){
        TileManager m = new TileManager(Path.of("tiles"),"https://tile.openstreetmap.org");
        for (int i = 0; i < 100 ; i++) {
            m.getImageOf(new TileManager.TileId(19,555,i));
        }
        for (int i = 0; i < 100 ; i++) {
            m.getImageOf(new TileManager.TileId(19,555,i));
        }





    }


}
