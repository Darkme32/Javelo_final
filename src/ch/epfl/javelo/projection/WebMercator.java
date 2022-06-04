package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
/**
 * Formulas that convert from WGS 84 to Web Mercator
 *
 * @author Menzo Bouaissi (340377)
 */
public final class WebMercator {

    private WebMercator() {
    }

    /**
     * Calculates the coordinate x, with the longitude
     *
     * @param lon the longitude
     * @return the x coordinate
     */
    public static double x(double lon) {
        return (lon + Math.PI) / (2 * Math.PI);
    }
    /**
     * Calculates the coordinate y, with the latitude
     *
     * @param lat the latitude
     * @return the y coordinate
     */
    public static double y(double lat) {
        return (Math.PI - Math2.asinh(Math.tan(lat))) / (2 * Math.PI);
    }
    /**
     * Calculates the longitude, with the x coordinate
     *
     * @param x the x coordinate
     * @return the longitude
     *
     * */
    public static double lon(double x) {
        return Math.PI * 2 * x - Math.PI;
    }
    /**
     * Calculates the latitude, with the y coordinate
     *
     * @param y the y coordinate
     * @return the latitude
     *
     * */
    public static double lat(double y) {
        return Math.atan(Math.sinh(Math.PI - 2 * Math.PI * y));
    }
}
