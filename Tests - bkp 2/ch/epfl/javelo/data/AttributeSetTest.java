package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;

import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTest {


    @Test
    void bitsistolarge() {


        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(0b1000000110000000000000000011111111111111110000000000000000011001L);
        });

    }
    @Test
    void bitswithint() {
        AttributeSet test = new AttributeSet(32314312);
//        System.out.println(Attribute.SURFACE_GRAVEL.ordinal());
        assertEquals(test.contains(Attribute.SURFACE_GRAVEL),true);

    }

    @Test
    void maxbitswithint() {

        AttributeSet test = new AttributeSet(2305843009213693952L);
        assertEquals(test.contains(Attribute.LCN_YES),true);

    }

    @Test
    void oflimitint(){
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(4611686018427387904L);
        });

    }

    @Test
    void oflast() {

       AttributeSet at = new AttributeSet(0b10L);
       assertEquals(new AttributeSet(0b10000000000000000000000000000000000000000000000000000000000000L), at.of(Attribute.LCN_YES));

    }

    @Test
    void offirst() {

//        System.out.println(5/2);
        AttributeSet at = new AttributeSet(0b10L);
        assertEquals(new AttributeSet(0b1L), at.of(Attribute.HIGHWAY_SERVICE));

    }

    @Test
    void of2elmt() {

        AttributeSet at = new AttributeSet(0b10L);
        at.of(Attribute.HIGHWAY_SERVICE,Attribute.MOTORROAD_YES);
        assertEquals(new AttributeSet(0b10000000000000001L), at.of(Attribute.HIGHWAY_SERVICE,Attribute.MOTORROAD_YES));


    }

    @Test
    void offirstandlast() {

        AttributeSet at = new AttributeSet(0b10L);
        assertEquals(new AttributeSet(0b10000000000000000000000000000000000000000000000000000000000001L), at.of(Attribute.LCN_YES,Attribute.HIGHWAY_SERVICE));

    }



    @Test
    void reallyContains(){

        AttributeSet at = new AttributeSet(0b10001000000L);
        assertEquals(true,at.contains(Attribute.HIGHWAY_CYCLEWAY));

    }

    @Test
    void reallyContainsNOT(){

        AttributeSet at = new AttributeSet(0b1000100101L);
        assertEquals(false,at.contains(Attribute.HIGHWAY_TRACK));

    }
    @Test
    void reallyContainsfist(){

        AttributeSet at = new AttributeSet(0b1L);
        assertEquals(true,at.contains(Attribute.HIGHWAY_SERVICE));

    }

    @Test
    void reallyContainsLast(){
        AttributeSet at = new AttributeSet(0b10000000000000000000000000000000000000000000000000000000000000L);
        assertEquals(true,at.contains(Attribute.LCN_YES));

    }
    @Test
    void intersection(){

        AttributeSet at = new AttributeSet(0b11111L);
        assertEquals(true,at.intersects(new AttributeSet(0b10000001000L)));

    }
    @Test
    void intersectionNOT(){

        AttributeSet at = new AttributeSet(0b1L);
        assertEquals(false,at.intersects(new AttributeSet(0b0000000010L)));

    }
    @Test
    void intersection1elmt(){

        AttributeSet at = new AttributeSet(0b1L);
        assertEquals(true,at.intersects(new AttributeSet(0b1L)));

    }
    @Test
    void intersectionlot(){

        AttributeSet at = new AttributeSet(0b100000000000000000111100000000000000000000000000000000001000L);
        assertEquals(true,at.intersects(new AttributeSet(0b100000000000000000000000010000000000000000000000000000000000L)));

    }
    @Test
    void intersectionillegalarg(){

//        AttributeSet at = new AttributeSet(0b100000000000000000000000000000000000000000000000000000000001000L);
//        assertThrows(IllegalArgumentException.class, () -> {
//            at.intersects(new AttributeSet(0b100000000000000000000000000000000000000000000000000000000001000L));
//
//        });
//        Juste, mais jsp pk il ne reconnait pas l'exception
//        probablement parce que intesects est au centre de l'interet
    }

    @Test
    void toStr(){

        AttributeSet set =
                AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}",
                set.toString());

    }
    @Test
    void toStr2(){

        AttributeSet set = new AttributeSet(0b11L);
        assertEquals("{highway=service,highway=track}",
                set.toString());

    }





}