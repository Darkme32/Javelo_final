package ch.epfl.javelo.projection;
/**
 * Provides static methods to convert
 * between WGS 84 and Swiss coordinates.
 *
 * @author Imade Bouhamria (339659)
 */
public final class Ch1903 {

    private Ch1903() {
    }


    /**
     * Calculates the East coordinate
     * in CH1903+ of a point given its
     * longitude and latitude in WGS 84
     *
     * @param lon longitude in radians
     * @param lat latitude in radians
     * @return the East coordinate of the point
     */
    public static double e(double lon, double lat) {
        lon = Math.toDegrees(lon);
        lat = Math.toDegrees(lat);

        double lon1 = 1e-4 * (3600 * lon - 26782.5);
        double lat1 = 1e-4 * (3600 * lat - 169028.66);

        return 2600072.37 + 211455.93 * lon1
                - 10938.51 * lon1 * lat1
                - 0.36 * lon1 * Math.pow(lat1, 2)
                - 44.54 * Math.pow(lon1, 3);
    }

    /**
     * Calculates the North coordinate
     * in CH1903+ of a point given its
     * longitude and latitude in WGS 84
     *
     * @param lon longitude in radians
     * @param lat latitude in radians
     * @return the North coordinate of the point
     */
    public static double n(double lon, double lat) {

        lon = Math.toDegrees(lon);
        lat = Math.toDegrees(lat);

        double lon1 = 1e-4 * Math.fma(3600, lon, -26782.5);
        double lat1 = 1e-4 * Math.fma(3600, lat, -169028.66);

        return 1200147.07
                + 308807.95 * lat1
                + 3745.25 * Math.pow(lon1, 2)
                + 76.63 * Math.pow(lat1, 2)
                - 194.56 * Math.pow(lon1, 2) * lat1
                + 119.79 * Math.pow(lat1, 3);
    }

    /**
     * Calculates the longitude in
     * WGS 84 of a point given its
     * East and North coordinates
     * in CH1903+
     *
     * @param e East coordinate
     * @param n North coordinate
     * @return the longitude of the point
     */
    public static double lon(double e, double n) {
        double x = 1e-6 * (e - 2600000);
        double y = 1e-6 * (n - 1200000);

        double lon0 = 2.6779094
                + 4.728982 * x
                + 0.791484 * x * y
                + 0.1306 * x * Math.pow(y, 2)
                - 0.0436 * Math.pow(x, 3);

        double lon = lon0 * 100 / 36;

        return Math.toRadians(lon);
    }

    /**
     * Calculates the latitude in
     * WGS 84 of a point given its
     * East and North coordinates
     * in CH1903+
     *
     * @param e East coordinate
     * @param n North coordinate
     * @return the latitude of the point
     */
    public static double lat(double e, double n) {
        double x = Math.pow(10, -6) * (e - 2600000);
        double y = Math.pow(10, -6) * (n - 1200000);

        double lat0 = 16.9023892
                + 3.238272 * y
                - 0.270978 * Math.pow(x, 2)
                - 0.002528 * Math.pow(y, 2)
                - 0.0447 * Math.pow(x, 2) * y
                - 0.0140 * Math.pow(y, 3);

        double lat = lat0 * 100 / 36;

        return Math.toRadians(lat);
    }


}

