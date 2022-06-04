package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.Bits.extractSigned;
import static ch.epfl.javelo.Bits.extractUnsigned;
import static org.junit.jupiter.api.Assertions.*;

class BitsTest {
    @Test
    void signedBitsWorks(){

        int[] values = {1202,1712,44,63,120202};
        int[] expected = {12,12,11,15,2};
        int start = 2;
        int length = 4;

        for(int i = 0; i<values.length; i++) {
            assertEquals(expected[i],extractSigned(values[i], start, length));
        }
    }


    @Test
    void unsignedBitsWorks(){

        int[] values = {1202,1712,44,63,120202};
        int[] expected = {12,12,11,15,2};
        int start = 2;
        int length = 4;

        for(int i = 0; i<values.length; i++) {
            assertEquals(expected[i],extractUnsigned(values[i], start, length));
        }
    }


    @Test
    void unsignedBitsOutOfBounds1(){

        int value = 1202;
        int start = 2;
        int length = 33;

        assertThrows(IllegalArgumentException.class, () -> {
            extractUnsigned(value, start, length);
        });
    }

    @Test
    void unsignedBitsOutOfBounds2(){

        int value = 1202;
        int start = 32;
        int length = 3;

        assertThrows(IllegalArgumentException.class, () -> {
            extractUnsigned(value, start, length);
        });
    }

    @Test
    void unsignedBitsOutOfBounds3(){

        int value = 1202;
        int start = 32;
        int length = 1;

        assertThrows(IllegalArgumentException.class, () -> {
            extractUnsigned(value, start, length);
        });
    }


    @Test
    void signedBitsOutOfBounds1(){

        int value = 1202;
        int start = 2;
        int length = 33;

        assertThrows(IllegalArgumentException.class, () -> {
            extractSigned(value, start, length);
        });
    }

    @Test
    void signedBitsOutOfBounds2(){

        int value = 1202;
        int start = 32;
        int length = 0;

        assertThrows(IllegalArgumentException.class, () -> {
            extractSigned(value, start, length);
        });
    }

    @Test
    void signedBitsOutOfBounds3(){

        int value = 1202;
        int start = 33;
        int length = 1;

        assertThrows(IllegalArgumentException.class, () -> {
            extractSigned(value, start, length);
        });
    }
}