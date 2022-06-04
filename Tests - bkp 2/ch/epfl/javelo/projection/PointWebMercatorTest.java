package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTest {

    PointWebMercator a = new PointWebMercator(0.518275214444, 0.353664894749);
    PointWebMercator b = new PointWebMercator(WebMercator.x(0.12217304764), WebMercator.y(0.802851455915));


//    @Test
//    void offSide() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            new PointWebMercator(-0.1, 0.2);
//        });
//        assertThrows(IllegalArgumentException.class, () -> {
//            new PointWebMercator(0, -1);
//        });
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            new PointWebMercator(3, 0.2);
//        });
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            new PointWebMercator(0, 9);
//        });
//    //Normal qui lance une erreur, il la lance au mauvais endroit(copier le code dans Preconditions Test)

//    }


    @Test
    void xAtZoomLevel() {
        assertEquals(69561722, a.xAtZoomLevel(19), 7);
    }
    @Test
    void xAtZoomLevel1() {
        assertEquals(69561722, a.xAtZoomLevel(19), 1);
    }

    @Test
    void yAtZoomLevel() {
        assertEquals(47468099, a.yAtZoomLevel(19), 7);
    }

    @Test
    void yAtZoomLevel1() {
        assertEquals(47468099, a.yAtZoomLevel(19), 1);
    }

    @Test
    void equalPoint(){
        assertEquals(PointWebMercator.of(19,69561722, 47468099),a);

    }

    @Test
    void notequalPoint(){
        assertNotEquals(PointWebMercator.of(1,0.25, 0.17),a);

    }

    @Test
    void pointWeb0() {
        assertEquals(new PointWebMercator(0, 0), PointWebMercator.of(1, 0, 0));

    }
    @Test
    void pointWeb8() {
        assertEquals(new PointWebMercator(0.5859375, 0.5859375), PointWebMercator.of(0, 150, 150));

    }
    @Test
    void pointWeb27() {
        assertEquals(new PointWebMercator(0.518275214444, 0.353664894749), PointWebMercator.of(19, 69561722, 47468099));

    }
    @Test
    void latCheck(){
        assertEquals(WebMercator.lat(WebMercator.y(0.802851455915)),b.lat());

    }
    @Test
    void lonCheck(){
        assertEquals(WebMercator.lon(WebMercator.x(0.12217304764)),b.lon());

    }
    @Test
    void toPointChCHECK(){
        PointCh test = new PointCh(Ch1903.e(0.12217304764,0.802851455915),Ch1903.n(0.12217304764,0.802851455915));
        assertEquals(test,b.toPointCh());//lol
    }

    @Test
    void toPointChnull(){

        PointWebMercator test = new PointWebMercator(WebMercator.x(2), WebMercator.y(0.802851455915));
        assertEquals(null,test.toPointCh());
    }
}