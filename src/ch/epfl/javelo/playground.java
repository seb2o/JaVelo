package ch.epfl.javelo;

import ch.epfl.javelo.gui.TileManager;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class playground {

    public static void main(String[] args) {

        Stream<String> strings =
                Stream.of("un", "deux", "trois", "quatre");
        Stream<Integer> stringLengths =
                strings.map(String::hashCode);
        stringLengths.allMatch((a) -> a > 0);

    }


}

