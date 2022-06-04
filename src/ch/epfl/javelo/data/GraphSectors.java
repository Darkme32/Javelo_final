package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.projection.SwissBounds.*;
/**
 * Represents  the 16384 sectors of JaVelo
 *
 * @author Imade Bouhamria (339659)
 */
public record GraphSectors(ByteBuffer buffer) {


    private static final int OFFSET_4 = 4;
    private static final int OFFSET_6 = OFFSET_4 + 2;
    private static final int SECTORS_NUMBER_X = 128;
    private static final int MAX_SECTOR_NUMBER = SECTORS_NUMBER_X - 1;
    private static final int END_OF_BUFFER_SECTOR = 1;

    /**
     * Returns all the sectors contained in a certain square area
     *
     * @param center   center of the desired area
     * @param distance distance from the center covered in each direction
     * @return the list of sectors intersecting the area
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        List<Sector> sectors = new ArrayList<>();

        double sectorWidth = WIDTH / (double) SECTORS_NUMBER_X;
        double sectorHeight = HEIGHT / (double) SECTORS_NUMBER_X;

        int eMin = (int) (((center.e() - distance) - MIN_E) / sectorWidth);
        int eMax = (int) (((center.e() + distance) - MIN_E) / sectorWidth);
        int nMin = (int) (((center.n() - distance) - MIN_N) / sectorHeight);
        int nMax = (int) (((center.n() + distance) - MIN_N) / sectorHeight);
        int sectorNumber;
        int sectorPosition;
        int startNodeId;
        int nodeNumber;
        int endNodeId;

        eMin = Math2.clamp(0, eMin, MAX_SECTOR_NUMBER);
        eMax = Math2.clamp(0, eMax, MAX_SECTOR_NUMBER);
        nMin = Math2.clamp(0, nMin, MAX_SECTOR_NUMBER);
        nMax = Math2.clamp(0, nMax, MAX_SECTOR_NUMBER);


        for (int i = eMin; i <= eMax; i++) {
            for (int j = nMin; j <= nMax; j++) {

                sectorNumber = SECTORS_NUMBER_X * j + i;
                sectorPosition = sectorNumber * OFFSET_6;
                startNodeId = buffer.getInt(sectorPosition);
                nodeNumber = Short.toUnsignedInt(buffer.getShort(sectorPosition + OFFSET_4));

                if ((sectorPosition + nodeNumber) >= buffer.capacity()) {
                    endNodeId = buffer.getInt(buffer.capacity() - OFFSET_6) + END_OF_BUFFER_SECTOR;
                } else {
                    endNodeId = buffer.getInt(sectorPosition) + nodeNumber;
                }
                sectors.add(new Sector(startNodeId, endNodeId));
            }
        }

        return sectors;
    }

    public record Sector(int startNodeId, int endNodeId) {

    }
}