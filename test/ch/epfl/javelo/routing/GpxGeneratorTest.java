package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

public class GpxGeneratorTest {
    
    @Test
    public void gpxCreatorTest() throws IOException {

        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(2046055, 2694240);
        assert r != null;
        System.out.printf("point haut : %f,\npoint bas : %f,\nlongueur : %f \n",
                ElevationProfileComputer.elevationProfile(r,0.1).maxElevation(),
                ElevationProfileComputer.elevationProfile(r,0.1).minElevation(),
                ElevationProfileComputer.elevationProfile(r,0.1).length());
        GpxGenerator.writeGpx("testGpx1.gpx",r,ElevationProfileComputer.elevationProfile(r,1));
    }
}
