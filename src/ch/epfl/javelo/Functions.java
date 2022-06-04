
package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;
import static ch.epfl.javelo.Math2.interpolate;
import static ch.epfl.javelo.Preconditions.checkArgument;
/**
 * Contains methods to create objects
 * representing mathematical functions
 *
 * @author Imade Bouhamria (339659)
 */
public final class Functions implements DoubleUnaryOperator {
    public Functions() {
    }


    /**
     * Creates a constant function
     *
     * @param y function's variable
     * @return a new constant function
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }


    /**
     * Creates a linear interpolation function
     *
     * @param samples list of values corresponding to
     *                the y coordinate of points on a
     *                graph
     * @param xMax    highest x coordinate of aforementioned
     *                points
     * @return a new function using linear interpolation
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        checkArgument((samples.length >= 2 && xMax > 0));

        return new Sampled(samples, xMax);
    }

    @Override
    public double applyAsDouble(double operand) {
        return 0;
    }

    public static final class Constant implements DoubleUnaryOperator {
        private final double y;

        public Constant(double y) {
            this.y = y;
        }

        /**
         * Returns the function's value
         * at a given point
         *
         * @param x function's input
         * @return corresponding function's value
         */
        @Override
        public double applyAsDouble(double x) {
            return y;
        }
    }

    public static final class Sampled implements DoubleUnaryOperator {
        private final float[] samples;
        private final double xMax;
        private final double step;

        public Sampled(float[] samples, double xMax) {
            this.samples = samples;
            this.xMax = xMax;
            this.step = xMax / (samples.length - 1);
        }

        /**
         * returns the function's value
         * at a given point
         *
         * @param x function's input
         * @return corresponding function's value
         */
        @Override
        public double applyAsDouble(double x) {

            if (x <= 0) return samples[0];
            if (x >= xMax) return samples[samples.length - 1];

            int lowerBound = (int) Math.floor(x / step);
            double lowerBoundValue = samples[lowerBound];
            double upperBoundValue = samples[lowerBound + 1];
            double tempX = x / step - lowerBound;

            return interpolate(lowerBoundValue, upperBoundValue, tempX);
        }
    }
}