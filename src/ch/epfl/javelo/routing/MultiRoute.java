
package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;
/**
 * Calculates the route, that is
 * made of segments
 *
 * @author Menzo Bouaissi (340377)
 */
public final class MultiRoute implements Route {
    private final List<Route> segments;
    private final double length;
    private final double[] routeLength;//the length at a certain route
    private double tempLength;
    private final Route lastRoute;


    /**
     * The constructor of a multi route.
     * It makes a copy of the segments, in order
     * to ensure the immutability.
     * It also calculates the length, in order to
     * not have to call the method every single time
     *
     * @param segments the segments that compose the route
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        this.routeLength = new double[segments.size() + 1];
        this.lastRoute = segments.get(segments.size() - 1);

        length = length();
        routeLength[0] = 0;
        for (int i = 0; i < segments.size(); i++) {
            tempLength += segments.get(i).length();
            routeLength[i+1] = tempLength;
        }
    }

    /**
     * Returns the index, at a given position.
     * To do that, we check every segment,and
     * check whether the clamped position is
     * greater than the sum of the previous
     * segment, plus the length of the actual
     * segment.
     * If it is the case, we are done, else, we
     * redo the calculation
     *
     * @param position selected position
     * @return the index of the segment at the position
     */
    @Override
    public int indexOfSegmentAt(double position) {

        int index = 0;
        double segmentsize = 0;
        double clampedPosition = Math2.clamp(0, position, length);

        for (Route s : segments) {
            if (segmentsize + s.length() >= clampedPosition && segmentsize <= clampedPosition) {
                index += s.indexOfSegmentAt(clampedPosition - segmentsize);
                return index;
            }

            index += s.indexOfSegmentAt(clampedPosition - segmentsize) + 1;
            segmentsize += s.length();
        }
        return index;

    }

    /**
     * Calculates the length, by adding
     * the length of all the routes
     *
     * @return the total length
     */
    @Override
    public double length() {
        double length = 0;

        for (Route segment : segments) length += segment.length();

        return length;
    }

    /**
     * Returns all the edges, by fetching
     * the edges of all the given route
     *
     * @return all the edges
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();

        for (Route route : segments) edges.addAll(route.edges());

        return edges;
    }

    /**
     * Returns all the points, by looking at
     * each route, and fetching all the points,
     * except the last one, in order not to
     * have duplicates. And finally,
     * we add all the points of the last route,
     * including the last one this time
     *
     * @return all the points
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();

        for (int j = 0; j < segments.size() - 1; j++) {
            points.addAll(segments.get(j).points());
            points.remove(points.size() - 1);
        }
        points.addAll(lastRoute.points());

        return points;
    }

    /**
     * Returns the point at a certain position.
     * To do that we check for each
     * segment, if the length of all the previous
     * segments, plus the actual segment is greater
     * than the clamped position, and if it's the case
     * we calculate the point at this position
     *
     * @param position the position
     * @return the point at a certain position
     */
    @Override
    public PointCh pointAt(double position) {
        double clampedPosition = Math2.clamp(0, position, length);
        double totallength = 0;

        for (Route route : segments) {
            if (totallength + route.length() > clampedPosition) {
                return route.pointAt(clampedPosition - totallength);
            }
            totallength += route.length();
        }
        return lastRoute.pointAt(clampedPosition);
    }

    /**
     * Returns the elevation at a certain position.
     * To do that we check for each
     * segment, whether the length of all the previous
     * segments, plus the actual segment is greater
     * than the clamped position, and if it's the case
     * we calculate the elevation at this position
     *
     * @param position the position
     * @return the elevation at a the position
     */
    @Override
    public double elevationAt(double position) {
        double clampedPosition = Math2.clamp(0, position, length);
        double totalLength = 0;
        for (Route route : segments) {
            if (totalLength + route.length() > clampedPosition) {
                return route.elevationAt(clampedPosition - totalLength);
            }
            totalLength += route.length();
        }
        return lastRoute.elevationAt(clampedPosition);
    }

    /**
     * Returns the node closest to a certain position.
     * To do that we check for each
     * segment, if the length of all the previous
     * segments, plus the actual segment is greater
     * than the clamped position, and if it's the case
     * we calculate the node closest to this position
     *
     * @param position the position
     * @return the node closest to this position
     */
    @Override
    public int nodeClosestTo(double position) {
        double clampedPosition = Math2.clamp(0, position, length);
        double totalLength = 0;

        for (Route route : segments) {
            if (totalLength + route.length() > clampedPosition) {
                return route.nodeClosestTo(clampedPosition - totalLength);
            }
            totalLength += route.length();
        }
        return lastRoute.nodeClosestTo(clampedPosition);
    }

    /**
     * Returns the RoutePoint closest to a
     * certain point. In order to do that, we go
     * through the whole route, and use the
     * method min from RoutePoint, to check
     * whether a certain RoutePoint is smaller to
     * an other one
     *
     * @param point the point
     * @return the closest RoutePoint
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint route = RoutePoint.NONE;
        RoutePoint tempRoute;
        for (int i = 0; i < segments.size(); i++) {
            tempRoute = segments.get(i).pointClosestTo(point).withPositionShiftedBy(routeLength[i]);
            route = route.min(tempRoute);
        }
        return route;
    }

}
