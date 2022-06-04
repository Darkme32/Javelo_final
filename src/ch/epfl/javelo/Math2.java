package ch.epfl.javelo;
/**
 * Math formulas
 *
 * @author Menzo Bouaissi (340377)
 */
public final class Math2 {


    private Math2() {
    }

    /**
     * Returns the integer part by excess
     * of the division of x by y
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return the ceil division of x and y
     * @throws IllegalArgumentException if
 *             there is a negative number or
 *             a division by 0
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(x >= 0 && y > 0);
        return (x + y - 1) / y;
    }

    /**
     * Calculates the y-coordinate of the point
     * on the line passing through (0,y0)
     * and (1,y1) and of given x-coordinate
     *
     * @param x  x coordinate
     * @param y0 one of the coordinate of the line
     * @param y1 an other coordinate on a line
     * @return the value of a certain point on a
     * line at a certain coordinate x
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1 - y0, x, y0);
    }

    /**
     * limits the value v to the interval from min to max,
     * returning min if v is less than min,
     * max if v is greater than max, and v otherwise
     *
     * @param min set a minimal value
     * @param max set a maximal value
     * @param v   it's the value we are looking
     * @return return the value, if it's in the range
     * @throws IllegalArgumentException if the min
*              value is strictly greater than the max
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(!(min > max));
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    /**
     * limits the value v to the interval from min to max,
     * returning min if v is less than min,
     * max if v is greater than max, and v otherwise
     *
     * @param min set a minimal value
     * @param max set a maximal value
     * @param v   it's the value we are looking
     * @return return the value, if it's in the range
     * @throws IllegalArgumentException if the min
     *              value is strictly greater than the max
     */
    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(!(min > max));
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    /**
     * Calculates the arcsin of a value
     *
     * @param x the value we want to convert
     * @return the arcsin value
     */
    public static double asinh(double x) {
        return Math.log(x + Math.hypot(1, x));
    }

    /**
     * Calculates the dot product between u and v
     *
     * @param uX x coordinate of the u vector
     * @param uY y coordinate of the u vector
     * @param vX x coordinate of the v vector
     * @param vY y coordinate of the v vector
     * @return dot product between the u and v vector
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return uX * vX + uY * vY;
    }

    /**
     * Calculates the square of the norm of a vector
     *
     * @param uX x coordinate of the vector
     * @param uY y coordinate of the vector
     * @return the length of the squared vector
     */
    public static double squaredNorm(double uX, double uY) {
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * Calculates the square of the norm of a vector
     *
     * @param uX x coordinate of the vector
     * @param uY y coordinate of the vector
     * @return the length of the vector
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX,uY));
    }

    /**
     * Calculates length of a projected vector
     *
     * @param aX x coordinate of the vector a
     * @param aY y coordinate of the vector a
     * @param bX x coordinate of the vector b
     * @param bY y coordinate of the vector b
     * @param pX x coordinate of the vector p
     * @param pY y coordinate of the vector p
     * @return the length of the vector AP, projected to the vector AB
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {

        double uX = bX - aX;
        double uY = bY - aY;
        double vX = pX - aX;
        double vY = pY - aY;

        return dotProduct(uX, uY, vX, vY) / norm(uX, uY);

    }
}