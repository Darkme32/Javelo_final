package ch.epfl.javelo.data;


import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;


/**
 * Represents a JaVelo graph
 *
 * @author Menzo Bouaissi (340377)
 */
public final class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * Loads a graph from the repository
     *
     * @param nodes         the nodes
     * @param sectors       the sectors
     * @param edges         the edges
     * @param attributeSets the set of attributes
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges
            , List<AttributeSet> attributeSets) {

        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * Opens the files where the data are,
     * and extracts the data.
     * Instantiates some Objects, in order to
     * have readable information.
     * Finally, it returns the JaVelo graph
     *
     * @param basePath the path to the files
     * @return a Graph object, with the data that were in the file
     * @throws IOException if the file doesn't exist
     */
    public static Graph loadFrom(Path basePath) throws IOException {
        GraphNodes nodes;
        GraphSectors sectors;
        GraphEdges edges;
        List<AttributeSet> attribute;

        try (FileChannel channelNodes
                     = FileChannel.open(basePath.resolve("nodes.bin"));
             FileChannel channelSectors
                     = FileChannel.open(basePath.resolve("sectors.bin"));
             FileChannel channelEdges
                     = FileChannel.open(basePath.resolve("edges.bin"));
             FileChannel channelProfile
                     = FileChannel.open(basePath.resolve("profile_ids.bin"));
             FileChannel channelElevations
                     = FileChannel.open(basePath.resolve("elevations.bin"));
             FileChannel channelAttribute
                     = FileChannel.open(basePath.resolve("attributes.bin"))) {

            final IntBuffer nodesBuffer = readFile(channelNodes)
                    .asIntBuffer();
            final ByteBuffer sectorsBuffer = readFile(channelSectors);
            final ByteBuffer edgesBuffer = readFile(channelEdges);
            final IntBuffer profileBuffer = readFile(channelProfile)
                    .asIntBuffer();
            final ShortBuffer elevationBuffer =readFile(channelElevations)
                    .asShortBuffer();
            final LongBuffer attribute_buffer = readFile(channelAttribute)
                    .asLongBuffer();

            nodes = new GraphNodes(nodesBuffer);

            sectors = new GraphSectors(sectorsBuffer);

            edges = new GraphEdges(edgesBuffer, profileBuffer, elevationBuffer);

            attribute = new ArrayList<>(attribute_buffer.capacity());

            for (int i = 0; i < attribute_buffer.remaining(); i++)
                attribute.add(new AttributeSet(attribute_buffer.get(i)));
            return new Graph(nodes, sectors, edges, attribute);

        }

    }

    /**
     * Returns the number of nodes in the graph
     *
     * @return the number of nodes in the graph
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * Returns the position of the node
     *
     * @param nodeId the id of the node
     * @return the position of the node
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * Returns the number of edges
     * coming out a certain node
     *
     * @param nodeId the id of the node
     * @return the number of outgoing edges of a certain node
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * returns the identity of the
     * edgeIndex-th edge coming out
     * of the node
     *
     * @param nodeId the id of the node
     * @return the id of an edge
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * Returns the id of the closest node
     * given a certain point. It's looking
     * in a limited section, and if it doesn't
     * find a node, it returns -1
     *
     * @param point          the reference point
     * @param searchDistance the distance where it's doing the research
     * @return the id of the closest node if it exists, otherwise -1
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        int id = -1;
        double distance = searchDistance*searchDistance;

        List<GraphSectors.Sector> sectorsInArea =
                sectors.sectorsInArea(point, searchDistance);

        for (GraphSectors.Sector actualSector : sectorsInArea) {
            for (int j = actualSector.startNodeId(); j < actualSector.endNodeId(); j++) {
                double tempDistance = point.squaredDistanceTo(nodePoint(j));
                if (distance >= tempDistance) {
                    id = j;
                    distance = tempDistance;
                }
            }
        }

        return id;
    }

    /**
     * Returns the id of the target node of the edge
     *
     * @param edgeId the id of the edge
     * @return the id of the target node of the edge
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * Returns a boolean concerning
     * the direction of the edge
     *
     * @param edgeId the id of the edge
     * @return true if the edge is inverted, otherwise it returns false
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * Returns the set of attribute
     * that are linked to an edge
     *
     * @param edgeId the id of the edge
     * @return the set of attribute
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * Returns the length of the edge
     *
     * @param edgeId the id of the edge
     * @return the length of the edge
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * Returns the elevation gain of a certain edge
     *
     * @param edgeId the id of the edge
     * @return the elevation gain of the edge
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * Checks if the edge has a profile, and
     * returns a sample function if it's the case,
     * or Double.Nan if not
     *
     * @param edgeId the id of the edge
     * @return a function depending on whether the edge has a profile
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        return edges.hasProfile(edgeId) ?
                Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId)) :
                Functions.constant(Double.NaN);
    }

    /**
     * Return a bytebuffer that contains the elements from a file
     * @param channel the channel in which the data are
     * @return a bytebuffer that contains the elements from a file
     * @throws IOException if the channel is wrong
     */
    private static ByteBuffer readFile(FileChannel channel) throws IOException {
      return   channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
    }

}
