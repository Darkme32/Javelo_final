package ch.epfl.javelo;

import static ch.epfl.javelo.Preconditions.checkArgument;
/**
 * methods to extract a sequence of bits
 * from a vector of 32 bits
 *
 * @author Imade Bouhamria (339659)
 */
public final class Bits {


    private Bits() {
    }

    /**
     * Extracts a sequence of bits from an integer
     *
     * @param value  initial integer
     * @param start  starting point of the sequence
     * @param length length of the sequence
     * @return extracted bits sequence
     */
    public static int extractSigned(int value, int start, int length) {
        checkArgument(start >= 0 &&
                start < Integer.SIZE &&
                length > 0 &&
                length <= Integer.SIZE);

        int v1 = value << (Integer.SIZE - start - length);

        return v1 >> (Integer.SIZE - length);
    }

    /**
     * Extracts a sequence of bits from an integer
     *
     * @param value  initial integer
     * @param start  starting point of the sequence
     * @param length length of the sequence
     * @return extracted unsigned bits sequence
     */
    public static int extractUnsigned(int value, int start, int length) {
        checkArgument(start >= 0 &&
                start < Integer.SIZE &&
                length > 0 &&
                length < Integer.SIZE);

        int v1 = value << (Integer.SIZE - start - length);

        return v1 >>> (Integer.SIZE - length);

    }

}