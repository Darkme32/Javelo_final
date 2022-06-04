package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;
/**
 * Representation of a point on a web Mercator system.
 *
 * @author Menzo Bouaissi (340377)
 */
public record PointWebMercator(double x, double y) {
    private final static int BASE_ZOOM_LEVEL = 8;

    /**
     * Represents a point on a web Mercator system and
     * checks if the x and y coordinates are in the range [0,1]
     */
    public PointWebMercator {
        Preconditions.checkArgument(!(x < 0 || x > 1 || y < 0 || y > 1));
    }

    /**
     * Returns the point on a web mercator system,
     * with the desired zoom level
     *
     * @param zoomLevel the desired zoom level
     * @param x         the x coordinate of the point
     * @param y         the y coordinate of the point
     * @return the point on a web mercator system
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -BASE_ZOOM_LEVEL - zoomLevel),
                Math.scalb(y, -BASE_ZOOM_LEVEL - zoomLevel));

    }

    /**
     * Returns the point on a web mercator system
     *
     * @param pointCh the point we want to convert
     * @return the point on a web mercator system
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()),
                WebMercator.y(pointCh.lat()));
    }

    /**
     * Returns the x coordinate at the desired zoom level
     *
     * @param zoomLevel the zoom level
     * @return x coordinate at the desired zoom level
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(this.x, BASE_ZOOM_LEVEL + zoomLevel);
    }

    /**
     * Returns the y coordinate at the desired zoom level
     *
     * @param zoomLevel the zoom level
     * @return y coordinate at the desired zoom level
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(this.y, BASE_ZOOM_LEVEL + zoomLevel);
    }

    /**
     * Transforms the x coordinate into a geographical coordinate
     *
     * @return the geographical coordinate of x in radians
     */
    public double lon() {
        return WebMercator.lon(this.x);
    }

    /**
     * Transforms the y coordinate into a geographical coordinate
     *
     * @return the geographical coordinate of y in radians
     */
    public double lat() {
        return WebMercator.lat(this.y);
    }

    /**
     * Transforms the point into a PointCh,
     * with the help of the lon() and lat() method
     *
     * @return the pointCh point at the x and y coordinate
     */
    public PointCh toPointCh() {
        double lon = this.lon();
        double lat = this.lat();
        double e = Ch1903.e(lon, lat);
        double n = Ch1903.n(lon, lat);
        return !SwissBounds.containsEN(e, n) ? null : new PointCh(e, n);
    }

}
