
package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
/**
 * Represents all the edges of the graph
 *
 * @author Menzo Bouaissi (340377)
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int OFFSET_INDEX = 0;
    private static final int OFFSET_LENGTH = OFFSET_INDEX + 4;//4
    private static final int OFFSET_ELEVATION = OFFSET_LENGTH + 2;//6
    private static final int OFFSET_OSM_ID = OFFSET_ELEVATION + 2;//8
    private static final int EDGE_SHIFT = 10;
    private static final int B = 4;
    private static final int ID_LENGTH = 30;

    /**
     * returns true is the edge is inverted
     *
     * @param edgeId the id of the edge we are looking at
     * @return true is the edge is inverted
     */
    public boolean isInverted(int edgeId) {
        return edgesBuffer.getInt(edgeId * EDGE_SHIFT + OFFSET_INDEX) < 0;
    }

    /**
     * returns the target node
     *
     * @param edgeId the id of the edge we are looking at
     * @return the target node
     */
    public int targetNodeId(int edgeId) {
        int bits = edgesBuffer.getInt(edgeId * EDGE_SHIFT + OFFSET_INDEX);
        return isInverted(edgeId) ? ~bits : bits;

    }

    /**
     * calculates the length of an edge
     *
     * @param edgeId the id of the edge we are looking at
     * @return the length of an edge
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.
                getShort(edgeId * EDGE_SHIFT + OFFSET_LENGTH)));
    }

    /**
     * calculates the height difference
     *
     * @param edgeId the id of the edge we are looking at
     * @return the height difference
     */
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.
                getShort(edgeId * EDGE_SHIFT + OFFSET_ELEVATION)));
    }

    /**
     * determines if an edge have an id
     *
     * @param edgeId the id of the edge we are looking at
     * @return true if the edge have an id
     */
    public boolean hasProfile(int edgeId) {
        return Bits.extractUnsigned(profileIds.get(edgeId), 30, 2) != 0;
    }

    /**
     * Returns the array of profile samples
     * of the given edge ID.
     *
     * @param edgeId the id of the edge
     * @return a table that contains the samples
     */
    public float[] profileSamples(int edgeId) {
        if (!hasProfile(edgeId)) return new float[0];

        int length = 1 + Math2.ceilDiv(
                edgesBuffer.getShort(edgeId * EDGE_SHIFT + OFFSET_LENGTH), Q28_4.ofInt(2));
        int tableIndex = 0;
        int id = Bits.extractUnsigned(profileIds.get(edgeId), 0, ID_LENGTH);
        int numberOfBits = (Short.SIZE / (1 <<( Bits.extractUnsigned(profileIds.get(edgeId), 30, 2) - 1)));
        boolean isInverted = isInverted(edgeId);

        float[] tab = new float[length];
        /*The simplest case, if we just have
         * a 16 bits vector, then we can
         * extract them directly, we don't have
         * to do any further manipulation
         */
        if (numberOfBits == Short.SIZE) {
            for (int i = id; i < elevations.capacity(); i++) {
                tab[tableIndex] = Q28_4.asFloat(
                        Short.toUnsignedInt(elevations.get(i)));
                tableIndex++;
                if (tableIndex == length) return tableInverter(tab, isInverted);
            }
            return tableInverter(tab, isInverted);
        }

        return compressedCases(numberOfBits, isInverted, id, tab);
    }

    /**
     * finds the edge's attributes
     *
     * @param edgeId the edge we are looking at's ID
     * @return the attributes that are attached to the edges
     */
    public int attributesIndex(int edgeId) {
        return Short.toUnsignedInt(edgesBuffer.getShort(edgeId * EDGE_SHIFT + OFFSET_OSM_ID));
    }

    /**
     * Makes the table, if the id of the edge is not 0, or 1
     *
     * @param numberOfBits the number of bits that contain
     *                     information on the difference of
     *                     elevation
     * @param isInverted   true if the edge is inverted
     * @param id           the first edge's ID
     * @param tab          the table where we have to
     *                     store the data
     * @return the table with samples of the elevation
     */
    private float[] compressedCases(int numberOfBits, boolean isInverted, int id, float[] tab) {
        float elevationDifference;
        int index = id + 1;
        int tableIndex = 1;
        tab[0] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(id)));
        /*
         * Calculates the samples by adding the data we have
         * in the table to the previous elevation. We have to
         * differentiate two cases, first one being when the
         * number of bits is 8, and the other when it is 4,
         * because one interpretation is Q0.4 and the other
         * one is Q4.4.
         *
         */
        while (index < elevations.capacity()) {
            for (int i = Short.SIZE - numberOfBits; i >= 0; i -= numberOfBits) {
                if (numberOfBits == B) {
                    elevationDifference = (float) Bits.extractSigned
                            (elevations.get(index), i, B) / (float) Short.SIZE;
                } else {
                    elevationDifference = (float) Bits.extractSigned
                            (elevations.get(index), i + B, B) +
                            ((float) Bits.extractUnsigned(elevations.get(index),
                                    i, B) / (float) Short.SIZE);
                }

                tab[tableIndex] = tab[tableIndex - 1] + elevationDifference;
                tableIndex++;
                if (tableIndex == tab.length) return tableInverter(tab, isInverted);
            }
            index++;
        }
        return tableInverter(tab, isInverted);

    }

    /**
     * Inverts a table
     *
     * @param tab      the table
     * @param inverted if it has to be reversed
     * @return the table in the right direction
     */
    private float[] tableInverter(float[] tab, boolean inverted) {
        if (inverted) {
            for (int j = 0; j < tab.length / 2; j++) {
                float temp1 = tab[j];
                tab[j] = tab[tab.length - j - 1];
                tab[tab.length - j - 1] = temp1;
            }
        }
        return tab;
    }

}