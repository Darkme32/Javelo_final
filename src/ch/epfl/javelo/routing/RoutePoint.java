package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
/**
 * Represents the point on a route closest
 * to a given reference point,
 * which is in the vicinity of the route
 * @author Imade Bouhamria (339659)
 *
 **/
public record RoutePoint(PointCh point, double position, double distanceToReference) {

    /**
     * Creates a default empty RoutePoint
     */
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     * @param positionDifference distance that we want
     *                           to shift the point by
     * @return a new point with the new position
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(this.point,this.position + positionDifference,this.distanceToReference);
    }

    /**
     *
     * @param that compared point
     * @return closest point to
     *         reference
     */
    public RoutePoint min(RoutePoint that) {
        return this.distanceToReference <= that.distanceToReference ?
                this :
                that;
    }


    /**
     *
     * @param thatPoint PointCh of the
     *                  new point
     * @param thatPosition position of
     *                     the new point
     * @param thatDistanceToReference
     *        distance to reference from
     *        the new point
     * @return closest point to
     *         reference
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return this.distanceToReference <= thatDistanceToReference ?
                this :
                new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);

    }

}
