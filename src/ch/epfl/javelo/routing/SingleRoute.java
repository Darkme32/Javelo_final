

package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Represents a route from a starting
 * point to an ending point,
 * without intermediate points
 *
 * @author Menzo Bouaissi (340377)
 */

public final class SingleRoute implements Route {

    private final List<Edge> edges;
    private final double[] edgeLength;//the length at a certain edge
    private  double length;

    /**
     * The constructor of a single route.
     * It makes a copy of the edges, in order
     * to ensure the immutability.
     * It also creates a table with the
     * correct position at the index of
     * the point. And it calculates the length
     * at the same time.
     *
     * @param edges the edges that compose the route
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());

        this.edges = List.copyOf(edges);
        this.edgeLength = new double[edges.size() + 1];
        edgeLength[0] = 0;

        for (int i = 0; i < edges.size(); i++) {
            length += edges.get(i).length();
            edgeLength[i+1] = length;
        }
    }

    /**
     * Returns the number of the segment, in this case 0
     *
     * @return 0
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * {@inheritDoc}
     * return the length of the route
     *
     * @return the length of the route
     */
    @Override
    public double length() {
        return length;
    }

    /**
     * return the edges of the route
     *
     * @return the edges of the route
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * Returns the points that make up the route.
     * To do that, we take the first node of
     * each edge, and the last node of the last edge,
     * in order to be sure to have no duplicate.
     *
     * @return the points that compose the route
     */
    @Override
    public List<PointCh> points() {
        ArrayList<PointCh> point = new ArrayList<>();
        for (Edge edge : edges) {
            point.add(edge.fromPoint());
        }
        point.add(edges.get(edges.size() - 1).toPoint());

        return point;
    }

    /**
     * Return the point at a certain position on
     * the route. First, we make sure that the
     * position is in the range, then we perform a
     * search of the index, to have the edge at a
     * certain position. Finally, we look at the
     * point at the position minus the position of the
     * first node, to be sure to not exceed the length
     * of the edge.
     *
     * @param position the position in the route
     * @return the point at the given position on the route
     */
    @Override
    public PointCh pointAt(double position) {
        double clampedPosition = Math2.clamp(0, position, this.length());
        int index = search(clampedPosition);

        return edges.get(index).
                pointAt(clampedPosition - edgeLength[index]);
    }

    /**
     * Returns the elevation at a certain position in
     * the route. First, we make sure that the
     * position is in the range, then we are doing
     * the search of the index, to have the edge at a
     * certain position. We are looking at the
     * elevation at the position minus the position of the
     * first node, to be sure to not exceed the length
     * of the edge.
     *
     * @param position on the route
     * @return the elevation at the given position
     */
    @Override
    public double elevationAt(double position) {
        double clampedPosition = Math2.clamp(0, position, this.length());
        int index = search(clampedPosition);

        return edges.get(index).
                elevationAt(clampedPosition - edgeLength[index]);
    }

    /**
     * Returns the closest node of a certain position in
     * the route. First, we make sure that the
     * position is in the range, then we are doing
     * the search of the index, to have the edge at a
     * certain position. Then we are looking at, whether
     * the point is closer to the first or last node of the edge
     *
     * @param position on the route
     * @return the closest node to the position
     */
    @Override
    public int nodeClosestTo(double position) {
        double clampedPosition = Math2.clamp(0, position, this.length());
        int index = search(clampedPosition);

        return Math.abs(edgeLength[index + 1] - clampedPosition) >
                Math.abs(edgeLength[index] - clampedPosition) ?
                edges.get(index).fromNodeId() :
                edges.get(index).toNodeId();

    }


    /**
     * For this method, we look through all
     * the edges, and we use the RoutePoint.min
     * method, in order to have the closest
     * RoutePoint to the point.
     *
     * @param point the given point
     * @return the closest RoutePoint
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint route = RoutePoint.NONE;
        double totalLength = 0;

        for (Edge edge : edges) {
            double position = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            PointCh tempPoint = edge.pointAt(position);
            route = route.min(tempPoint, position + totalLength, tempPoint.distanceTo(point));
            totalLength += edge.length();
        }
        return route;
    }


    /**
     * Method that finds the index of an edge,
     * given a position.
     *
     * @param position the postion
     * @return the index of the edge
     */
    private int search(double position) {
        int index = Arrays.binarySearch(edgeLength, position);

        if ((index) >= 0) {
            if (index == 0) return index;
            return index - 1;
        } else {
            return -(index) - 2;
        }
    }
}
