package ch.epfl.javelo;


import static java.lang.Math.scalb;

/**
 * Contains the following static methods to
 * convert numbers between the Q28.4
 * representation and other representations
 *
 * @author Imade Bouhamria (339659)
 */
public final class Q28_4 {

    private final static int DECIMAL_PART = 4;

    private Q28_4(){}


    /**
     * Converts an integer to Q28.4 form
     *
     * @param i integer to be converted
     * @return i in Q28.4 format
     */
    public static int ofInt(int i){
        return i << DECIMAL_PART;
    }

    /**
     * Converts a number in Q28.4 form
     * to a double
     *
     * @param q28_4 number to be converted
     * @return q28_4 as a double
     */
    public static double asDouble(int q28_4) {
        return scalb((double) q28_4, -DECIMAL_PART);
    }


    /**
     * Converts a number in Q28.4 form
     * to a float
     *
     * @param q28_4 number to be converted
     * @return q28_4 as a float
     */
    public static float asFloat(int q28_4){
        return scalb(q28_4, -DECIMAL_PART);
    }

}
