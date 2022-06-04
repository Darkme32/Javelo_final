package ch.epfl.javelo.routing;
import ch.epfl.javelo.Functions;

import java.util.DoubleSummaryStatistics;

import static ch.epfl.javelo.Preconditions.checkArgument;
/**
 * Elevation profile
 *
 * @author Imade Bouhamria (339659)
 */
public class ElevationProfile {

    private final double length;
    private final float[] elevationSamples;
    private final DoubleSummaryStatistics statistics;

    /**
     * Creates an elevation profile from
     * a list of altitudes
     *
     * @param length           distance between the first
     *                         and last sample
     * @param elevationSamples list of altitude
     *                         samples
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        checkArgument(elevationSamples.length >= 2 && length > 0);

        this.length = length;
        this.elevationSamples = elevationSamples.clone();
        statistics = new DoubleSummaryStatistics();

        for (float eS : elevationSamples) statistics.accept(eS);
    }


    /**
     * @return the length of the
     * sample
     */
    public double length() {
        return length;
    }

    /**
     * @return the lowest point
     * in the profile
     */
    public double minElevation() {
        return statistics.getMin();
    }

    /**
     * @return the highest point
     * in the profile
     */
    public double maxElevation() {
        return statistics.getMax();
    }

    /**
     * @return the total ascent of the
     * profile
     */
    public double totalAscent() {
        return isAscent(true);
    }

    /**
     * @return the total descent of the
     * profile
     */
    public double totalDescent() {
        return isAscent(false);
    }

    /**
     * @param position the relative position
     *                 on the profile, with 0
     *                 being the first point
     * @return the altitude at the given point
     */
    public double elevationAt(double position) {
        return new Functions.
                Sampled(elevationSamples, length).
                applyAsDouble(position);
    }


    /**
     * returns the total ascent or descent
     *
     * @param isAscent true for ascent
     *                 false for descent
     * @return the ascent/descent
     */
    private double isAscent(boolean isAscent) {
        double total = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            double delta = elevationSamples[i] - elevationSamples[i - 1];
            if ( isAscent && delta > 0) total += delta;
            if (!isAscent && delta < 0) total += delta;
        }
        return Math.abs(total);
    }
}
