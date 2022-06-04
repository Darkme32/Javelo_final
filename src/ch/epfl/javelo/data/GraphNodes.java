
package ch.epfl.javelo.data;


import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;
/**
 * Represents all nodes of the graph
 *
 * @author Menzo Bouaissi (340377)
 */
public record GraphNodes(IntBuffer buffer) {
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;//1
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;//2
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;//3
    private static final int NODE_SHIFT = 3;
    private static final int LENGTH_ID = 28;
    private static final int LENGTH_NBR_OF_OUTGOING_EDGES = 4;

    /**
     * Counts the number of nodes
     *
     * @return the number of nodes
     */
    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    /**
     * Calculates the east coordinate of a certain node
     *
     * @param nodeId the node we are looking at
     * @return the east coordinate of the node "nodeId"
     */
    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_SHIFT+ OFFSET_E));
    }

    /**
     * Calculates the north coordinate of a certain node
     *
     * @param nodeId the node we are looking at
     * @return the north coordinate of the node "nodeId"
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_SHIFT + OFFSET_N));
    }

    /**
     * Calculates the number of edges coming out of the node
     *
     * @param nodeId the node we are looking at
     * @return the number of edges
     */
    public int outDegree(int nodeId) {
        return Bits.extractUnsigned(
                buffer.get(nodeId * NODE_SHIFT + OFFSET_OUT_EDGES), LENGTH_ID, LENGTH_NBR_OF_OUTGOING_EDGES);
    }

    /**
     * calculates the id of a certain edge
     *
     * @param nodeId    the id of the node we are looking at
     * @param edgeIndex the index of the edge we want
     * @return the edge's ID
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert (0 <= edgeIndex && edgeIndex < outDegree(nodeId));
        return Bits.extractUnsigned(
                buffer.get(nodeId * NODE_SHIFT + OFFSET_OUT_EDGES), 0, LENGTH_ID)
                + edgeIndex;

    }

}

