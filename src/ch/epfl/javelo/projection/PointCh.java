package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

import static ch.epfl.javelo.Preconditions.checkArgument;
/**
 * Represents a point in the
 * Swiss coordinate system
 *
 * @author Imade Bouhamria (339659)
 */
public record PointCh(double e, double n) {


    /**
     * Allows to create a PointCh object
     * which corresponds to a geographical
     * location within Switzerland
     *
     * @param e East coordinate
     * @param n North coordinate
     */
    public PointCh {
   checkArgument(SwissBounds.containsEN(e, n));
    }


    /**
     * Calculates the distance between two points squared
     *
     * @param that is the destination point
     * @return the distance between the point calling the method and the destination squared
     */
    public double squaredDistanceTo(PointCh that) {
        return  Math2.squaredNorm(this.e - that.e, this.n - that.n);
    }

    /**
     * Calculates the distance between two points
     *
     * @param that is the destination point
     * @return the distance between the point calling the method and the destination
     */
    public double distanceTo(PointCh that) {
        return Math2.norm(this.e - that.e, this.n - that.n);
    }

    /**
     * Calculates the longitude of the point
     * in the WGS 84 system
     *
     * @return the longitude in radians
     */
    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * Calculates the latitude of the point
     * in the WGS 84 system
     *
     * @return the latitude in radians
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }
}
