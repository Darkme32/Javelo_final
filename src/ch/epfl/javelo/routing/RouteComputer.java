package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;
/**
 * Calculate the route between two
 * node on a graph
 *
 * @author Menzo Bouaissi (340377)
 * @author Imade Bouhamria (339659)
 */

public final class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;
    private static final Float INFINITY = Float.POSITIVE_INFINITY;

    /**
     * Instantiates a RouteComputer, with the
     * given graph and cost function
     *
     * @param graph        the graph
     * @param costFunction a function that associate a
     *                     cost factor to an edge
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * Calculates the best route between the start node
     * and the end node.
     * For this method, some specific part of the code,
     * have been explained, in order to be more precise.
     *
     * @param startNodeId  the first node's ID
     * @param endNodeId   the last node's ID
     * @return the best route between the two nodes, if it exists
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);
        float[] distance = new float[graph.nodeCount()];
        int[] previousNode = new int[graph.nodeCount()];
        int[] previousEdge = new int[graph.nodeCount()];
        int nodeId = 0;
        PriorityQueue<WeightedNode> beingExplored = new PriorityQueue<>();
        PointCh endPoint = graph.nodePoint(endNodeId);

        for (int i = 0; i < graph.nodeCount(); i++) {
            distance[i] = INFINITY;
            previousNode[i] = 0;

        }
        distance[startNodeId] = 0;
        beingExplored.add(new WeightedNode(startNodeId, 0f));


        while (!beingExplored.isEmpty()) {

            if (nodeId == endNodeId){return createSingleRoute(nodeId, startNodeId,
                    graph, previousEdge, previousNode);}

            /*
             *  We remove the node with the shortest
             *  distance, and check if it has already
             *  been visited. If that's the case, then we
             *  remove it again
             */
            do nodeId = beingExplored.remove().nodeId;
            while (distance[nodeId] == Float.NEGATIVE_INFINITY);

            /*
             *  Here, we go to all the edges of a node,
             *  and if the criteria are met, we
             *  store the node and its distance in the
             *  table
             */
            for (int i = 0; i < graph.nodeOutDegree(nodeId); i++) {
                int edgeId = graph.nodeOutEdgeId(nodeId, i);
                int targetNodeId = graph.edgeTargetNodeId(edgeId);

                if (distance[targetNodeId] != Float.NEGATIVE_INFINITY) {
                    double cost = costFunction.costFactor(targetNodeId, edgeId);
                    double tempDistance = distance[nodeId] + graph.edgeLength(edgeId) * cost;
                    double distanceStraight = graph.nodePoint(targetNodeId).distanceTo(endPoint);

                    if ((tempDistance) < distance[targetNodeId]) {
                        previousNode[targetNodeId] = nodeId;
                        previousEdge[targetNodeId] = edgeId;
                        distance[targetNodeId] = (float) (tempDistance);
                        beingExplored.add(new WeightedNode(targetNodeId,
                                (float) (distanceStraight+tempDistance)));
                    }
                }
            }
            distance[nodeId] = Float.NEGATIVE_INFINITY;
        }
        return null;
    }

    /**
     * The last step of the algorithm, if we
     * reach the last node, then we build the
     * route by looking through all the targeted
     * node of the edge, and if the targeted node
     * is stored in our table, then we instantiate
     * an edge. Finally we return the single
     * route element with the stored edges
     */
    private SingleRoute createSingleRoute(int nodeId, int startNodeId, Graph graph,
                                          int[] previousEdge, int[] previousNode) {

        Deque<Edge> edges = new ArrayDeque<>();

        while (nodeId != startNodeId) {
            edges.addFirst(Edge.of(graph, previousEdge[nodeId],
                    previousNode[nodeId], nodeId));
            nodeId = previousNode[nodeId];
        }
        return new SingleRoute(new ArrayList<>(edges));
    }

    record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
        /**
         * Method that allows the priority
         * queue to compare two nodes
         *
         * @param that the stored node
         * @return an indication on whether this node
         * is smaller to that node
         */
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance, that.distance);
        }
    }

}