package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static org.junit.jupiter.api.Assertions.*;

class PreconditionsTest {

    @Test
    void checkArgumentThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> {
            checkArgument(false);
        });
    }


}