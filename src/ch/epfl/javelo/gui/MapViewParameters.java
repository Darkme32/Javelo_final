package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * Record that represents the parameters
 * of the map
 *
 * @param zoomLevel the zoom level
 * @param x         the x coordinate of the top left point
 * @param y         the y coordinate of the top left point
 * @author Menzo Bouaissi (340377)
 */
public record MapViewParameters(int zoomLevel, double x, double y) {

    /**
     * Returns the top left corner
     *
     * @return the top left corner
     */
    public Point2D topLeft() {
        return new Point2D(this.x, this.y);
    }

    /**
     * Returns a new MapViewParameters object
     * with the given x and y coordinates
     *
     * @param x the new x coordinate
     * @param y the new y coordinate
     * @return a new MapViewParameters object
     *         with the given x and y coordinates
     */
    public MapViewParameters withMinXY(double x, double y) {
        return new MapViewParameters(this.zoomLevel, x, y);
    }

    /**
     * Returns the point on the map at
     * a certain x and y distance from the
     * top left corner
     *
     * @param x the new x coordinate
     * @param y the new y coordinate
     * @return a PointWebMercator at a certain point
     */
    public PointWebMercator pointAt(double x, double y) {
        return PointWebMercator.of(zoomLevel, this.x + x, this.y + y);
    }

    /**
     * Returns the x position with
     * respect to the top left
     * corner
     *
     * @param pointWebMercator a PointWebMercator
     * @return the position x
     */
    public double viewX(PointWebMercator pointWebMercator) {
        return  pointWebMercator.xAtZoomLevel(zoomLevel)-this.x;
    }

    /**
     * Returns the y position
     * with respect to the top left
     * corner
     *
     * @param pointWebMercator a PointWebMercator
     * @return the position y
     */
    public double viewY(PointWebMercator pointWebMercator) {
        return pointWebMercator.yAtZoomLevel(zoomLevel)-this.y;
    }

    /**
     * Return a new MapViewParameters object
     * with a specified zoom level
     *
     * @param zoomLevel the zoom level
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @return a MapViewParameters object
     */
    public static MapViewParameters of(int zoomLevel, double x, double y) {
        return new MapViewParameters(zoomLevel, x, y);
    }
}
