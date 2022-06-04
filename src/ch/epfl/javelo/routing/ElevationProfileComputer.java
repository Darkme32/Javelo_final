package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;

import java.util.Arrays;

import static ch.epfl.javelo.Preconditions.checkArgument;
/**
 * Represents the longitudinal
 * profile of a route
 *
 * @author Imade Bouhamria (339659)
 */
 public final class ElevationProfileComputer {


    private ElevationProfileComputer() {
    }


    /**
     * Creates an elevation profile for
     * a given route
     *
     * @param route         the route whose profile
     *                      we want
     * @param maxStepLength number of elevation
     *                      points desired
     * @return an elevation profile
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        int samplesNb = (int) Math.ceil(route.length() / maxStepLength) + 1;
        checkArgument(samplesNb > 0);
        float[] elevationSamples = new float[samplesNb];
        boolean nbQ = false;

        for (int i = 0; i < samplesNb; i++) {
            elevationSamples[i] = (float) route.elevationAt(i * maxStepLength);
            if (!Float.isNaN(elevationSamples[i])) nbQ = true;
        }

        if (!nbQ) Arrays.fill(elevationSamples, 0);


        if (Float.isNaN(elevationSamples[0])) {
            startFill(elevationSamples, route, maxStepLength);
        }

        if (Float.isNaN(elevationSamples[elevationSamples.length - 1])) {
            endFill(elevationSamples, route, samplesNb, maxStepLength);
        }

        if (checkForNan(elevationSamples)) {
            midFill(elevationSamples);
        }

        return new ElevationProfile(route.length(), elevationSamples);
    }

    /**
     * Checks for NaNs in the middle of the list
     *
     * @param elevationSamples the profile that
     *                         is evaluated
     * @return true if there are NaNs in the list
     */
    private static boolean checkForNan(float[] elevationSamples) {

        for (double d : elevationSamples) {
            if (Float.isNaN((float) d)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Replaces the NaNs at the start of the list by
     * values
     *
     * @param elevationSamples the profile currently
     *                         evaluated
     * @param route            the corresponding route
     * @param maxStepLength    the corresponding maximum
     *                         step length
     */
    private static void startFill(float[] elevationSamples, Route route, double maxStepLength) {
        int nanNumber = 0;
        while (Float.isNaN((float) route.
                elevationAt(nanNumber * maxStepLength))) {
            nanNumber++;
        }
        for (int k = 0; k < nanNumber; k++) elevationSamples[k] = elevationSamples[nanNumber];

    }

    /**
     * Replaces the NaNs at the end of the list by
     * values
     *
     * @param elevationSamples the profile currently
     *                         evaluated
     * @param route            the corresponding route
     * @param samplesNb        the corresponding sample number
     * @param maxStepLength    the corresponding maximum
     *                         step length
     */
    private static void endFill(float[] elevationSamples, Route route, int samplesNb, double maxStepLength) {
        int nanNumber = 0;
        while (Float.isNaN((float) route.
                elevationAt((samplesNb - nanNumber) * maxStepLength))) {
            nanNumber++;
        }

        for (int k = 0; k < nanNumber; k++) {
            elevationSamples[elevationSamples.length - 1 - k] =
                    elevationSamples[elevationSamples.length - nanNumber];
        }
    }

    /**
     * Replaces the NaNs in the middle of the list by
     * values
     *
     * @param elevationSamples the profile currently
     *                         evaluated
     */
    private static void midFill(float[] elevationSamples) {
        for (int i = 0; i < elevationSamples.length; i++) {

            if (Double.isNaN(elevationSamples[i])) {
                int finalIndex = i;
                while (Double.isNaN(elevationSamples[finalIndex])) finalIndex++;
                int length = finalIndex - i;
                for (int j = 1; j < length + 1; j++) {
                    elevationSamples[i + j - 1] =
                            (float) Math2.interpolate(elevationSamples[i - 1],
                                    elevationSamples[finalIndex], (double) j / (double) (length + 1));

                }
            }
        }

    }

}
