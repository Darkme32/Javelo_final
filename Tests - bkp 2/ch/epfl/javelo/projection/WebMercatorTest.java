package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebMercatorTest {

    @Test
    void x() {
        assertEquals(0.659155,WebMercator.x(1),1e-6);
    }

    @Test
    void y() {
        assertEquals(0.304846,WebMercator.y(1),1e-6);
    }

    @Test
    void lon() {
        assertEquals(1,WebMercator.lon(0.659155),1e-6);
    }

    @Test
    void lat() {
        assertEquals(1,WebMercator.lat(0.304846),1e-5);
    }
}