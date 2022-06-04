package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Represents an edge
 *
 * @author Menzo Bouaissi (340377)
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    /**
     * Method that helps to instantiate an edge
     * @param graph the graph where the edge is
     * @param edgeId the id of the edge
     * @param fromNodeId the id of the first node of the edge
     * @param toNodeId the id of the second node of the edge
     * @return an Edge object that correspond to the given argument
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId,
                graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * Returns the position, in meters, along the edge
     * that is closest to the given point
     * To do that, we use the projectionLength method
     * from Math2, that calculates the distance between
     * a line and a point
     * @param point the point
     * @return the position along the edge
     */
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(),
                toPoint.n(), point.e(), point.n());
    }

    /**
     * Calculates the point at a certain position on the edge.
     * To do that, we interpolate on the x and y axes,
     * since we know that the edge is a line
     * @param position the position on the edge
     * @return the point at the given position on the edge
     */
    public PointCh pointAt(double position) {
        double positionRatio = position / length;
        if (Double.isNaN(positionRatio))return new PointCh(fromPoint().e(), fromPoint().n());
        double x = Math2.interpolate(fromPoint().e(),toPoint.e(), positionRatio);
        double y = Math2.interpolate(fromPoint.n(), toPoint().n(), positionRatio);
        return new PointCh(x, y);
    }
    /**
     * Calculates the elevation at a certain position on the edge.
     * In order to do that, we interpolate on the y axes,
     * because we know that the edge is a line
     * @param position the position on the edge
     * @return the elevation at the given position on the edge
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }

}
