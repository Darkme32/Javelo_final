package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.Q28_4.ofInt;
import static org.junit.jupiter.api.Assertions.*;

class Q28_4Test {

    @Test
    void ofIntWorks() {
        int[] values = {1202,1712,44,63,120202,2,2147483647,-12,-1712,-2147483648};
        int[] expected = {19232,27392,704,1008,1923232,32,-16,-192,-27392,0};

        for(int i = 0; i<values.length; i++)
            assertEquals(expected[i], ofInt(values[i]));
    }

    @Test
    void ofIntInBinaryWorks() {
        int[] values = {0b00000000000000000000010010110010};
        int[] expected = {19232};

        for(int i = 0; i<values.length; i++)
            assertEquals(expected[i], ofInt(values[i]));
    }


    @Test
    void asDouble() {
    }

    @Test
    void asFloat() {
    }
}