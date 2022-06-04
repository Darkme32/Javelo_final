package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;
import static ch.epfl.javelo.projection.SwissBounds.containsEN;
import static org.junit.jupiter.api.Assertions.*;

class SwissBoundsTest {
    @Test
    void containsENNot(){
        double e = 0;
        double n = 0;
        assertEquals(false,containsEN(e,n));
    }

    @Test
    void containsENTrue(){
        double e = 2578247;
        double n = 1183956;
        assertEquals(true,containsEN(e,n));
    }

    @Test
    void containsENInf(){
        double e = Double.POSITIVE_INFINITY;
        double n = Double.NEGATIVE_INFINITY;
        assertEquals(false,containsEN(e,n));
    }
}