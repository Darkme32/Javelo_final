package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;

/**
 * Creates a bean that contains the property
 * related to the route.
 *
 * @author Menzo Bouaissi (340377)
 */
public final class RouteBean {

    private final static int MAX_NBR_OF_ROUTE = 50;
    private final static int MAX_STEP_LENGTH = 5;

    private final Map<Pair<Integer, Integer>, Route> allRoute;
    private final ArrayList<Route> routeList;

    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;

    /**
     * Manages the property, and calculates
     * the best route using the
     * route computer
     *
     * @param routeComputer a routeComputer object that
     *                      calculates the best
     *                      route between two points
     */
    public RouteBean(RouteComputer routeComputer) {
        routeList = new ArrayList<>();
        allRoute = new LinkedHashMap<>();
        waypoints = FXCollections.observableArrayList();

        highlightedPosition = new SimpleDoubleProperty(Double.NaN);
        elevationProfile = new SimpleObjectProperty<>();
        route = new SimpleObjectProperty<>();

        waypoints.addListener((Observable o) ->
                waypointsCalculator(routeComputer));
    }

    /**
     * Return the property of the highlighted position
     * @return the property value of the highlighted position
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * Return the double value of the highlighted position
     * @return the double value of the highlighted position
     */
    public double highlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * Set the highlighted at a certain value
     * @param value the value we want to attribute
     */
    public void setHighlightedPosition(double value) {
        highlightedPosition.set(value);
    }

    /**
     * Return the route in a read only property
     * @return the route in a read only property
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    /**
     * Set the list of waypoints at a certain value
     * @param value of the list of waypoints
     */
    public void setWaypoints(List<Waypoint> value) {
        waypoints.setAll(value);
    }

    /**
     * Return the waypoints in an observable list
     * @return the waypoints in an observable list
     */
    public ObservableList<Waypoint> waypoints() {
        return waypoints;
    }

    /**
     * Return the elevation profile in a read only property
     * @return the elevation profile in a read only property
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }

    /**
     * Calculates the best route, and sets the property
     * containing it. We initialize both the route and the
     * elevation profile to null, whether the list of
     * waypoint is inferior to two, or the route doesn't
     * exist
     *
     * @param routeComputer the computer that calculates
     *                      the best route
     */
    private void waypointsCalculator(RouteComputer routeComputer) {
        Route bestRoute;
        routeList.clear();
        if (waypoints.size() < 2) {
            nullSetter();
            return;
        }
        for (int i = 0; i < waypoints.size() - 1; i++) {
            routeAdder(routeComputer, i);
            bestRoute = allRoute.get(
                    new Pair<>(waypoints.get(i).id(), waypoints.get(i + 1).id()));
            if (bestRoute == null) {
                nullSetter();
                return;
            }
            routeList.add(bestRoute);
        }
        Route entireRoute = new MultiRoute(routeList);

        route.set(entireRoute);
        elevationProfile.set(ElevationProfileComputer.elevationProfile(entireRoute, MAX_STEP_LENGTH));

    }

    /**
     * Sets the route and the elevation
     * profile to null
     */
    private void nullSetter() {
        route.set(null);
        elevationProfile.set(null);
    }

    /**
     * Adds a route to the list, only if
     * it is not the same point,
     * and checks whether the size of the map
     * is under 50
     *
     * @param routeComputer the route computer
     * @param i             the index of the waypoint
     */
    private void routeAdder(RouteComputer routeComputer, int i) {
        if (waypoints.get(i).id() != waypoints.get(i + 1).id())
            if (!allRoute.containsKey(new Pair<>(waypoints.get(i).id(), waypoints.get(i + 1).id())))
                allRoute.put(
                        new Pair<>(waypoints.get(i).id(), waypoints.get(i + 1).id()),
                        routeComputer.bestRouteBetween(waypoints.get(i).id(), waypoints.get(i + 1).id()));
        if (allRoute.size() > MAX_NBR_OF_ROUTE)
            allRoute.remove(allRoute.keySet().iterator().next());
    }

    /**
     *
     * @param position the position relative
     *                 to the route
     * @return the index of the segment containing
     *          the indicated position
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = routeProperty().get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).id();
            int n2 = waypoints.get(i + 1).id();
            if (n1 == n2) index += 1;
        }
        return index;
    }

}

