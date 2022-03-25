package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ElevationProfileComputerTest {

    @Test
    public void elevationProfileTestOneEdge() {

    }

    @Test
    public void elevationProfileTestSeveralEdge() {

        SingleRoute route = null;


        ElevationProfileComputer.elevationProfile(route,0);
    }


}
