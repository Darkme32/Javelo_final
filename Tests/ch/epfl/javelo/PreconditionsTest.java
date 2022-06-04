package ch.epfl.javelo;

import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PreconditionsTest {
    @Test
    void checkArgumentSucceedsForTrue() {
        assertDoesNotThrow(() -> {
            Preconditions.checkArgument(true);
        });
    }

    @Test
    void checkArgumentThrowsForFalse() {
        assertThrows(IllegalArgumentException.class, () -> {
            Preconditions.checkArgument(false);
        });
    }

    @Test
    void offSide() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(-0.1, 0.2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(0, -1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(3, 0.2);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(0, 9);
        });
        //Normal qui lance une erreur, il la lance au mauvais endroit

    }
}
