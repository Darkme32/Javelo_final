package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Represents a waypoint
 * as a PointCh and a node
 * ID
 *
 * @author Imade Bouhamria (339659)
 */
public record  Waypoint(PointCh pointCh, int id) {}
