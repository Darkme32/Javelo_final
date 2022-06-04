package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;
/**
 * Route
 *
 * @author Imade Bouhamria (339659)
 */
public interface Route {



    /**
     * Gives the index of the sector
     * containing the chosen point
     *
     * @param position selected position
     * @return index of a sector
     */
    int indexOfSegmentAt(double position);


    /**
     * @return the length of the
     * route
     */
    double length();


    /**
     * @return all of the edges on
     * the route
     */
    List<Edge> edges();


    /**
     * @return all of the points on
     * the route
     */
    List<PointCh> points();


    /**
     * Returns the point at a specific
     * position on the route
     *
     * @param position a position
     *                 on the route
     *                 in meters
     *
     * @return corresponding PointCh
     */
    PointCh pointAt(double position);


    /**
     * Returns the node closest to
     * the specified position
     *
     * @param position a position
     *                 on the route
     *                 in meters
     *
     * @return node ID
     */
    int nodeClosestTo(double position);


    /**
     * Returns the closest point on the
     * route relatively to a given point
     *
     * @param point the point we want to
     *              project on the route
     *
     * @return closest RoutePoint to point
     */
    RoutePoint pointClosestTo(PointCh point);


    /**
     * Returns the altitude of a given
     * point on the route
     *
     * @param position on the route
     *
     * @return altitude at that point
     */
    double elevationAt(double position);
}
