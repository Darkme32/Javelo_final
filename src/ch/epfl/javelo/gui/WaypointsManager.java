package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Adds, moves and removes
 * waypoints
 *
 * @author Imade Bouhamria (339659)
 */
public final class WaypointsManager {

    private final static int SEARCH_DISTANCE = 1000;
    private final static String PIN_LINE          = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final static String PIN_STYLE         = "pin_outside";
    private final static String CIRCLE            = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private final static String CIRCLE_STYLE      = "pin_inside";
    private final static String FIRST_PIN_STYLE   = "first";
    private final static String LAST_PIN_STYLE    = "last";
    private final static String DEFAULT_PIN_STYLE = "middle";
    private final static String PIN               = "pin";

    private final DoubleProperty oldDeltaX, oldDeltaY,
                                   deltaX, deltaY;
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> mapViewParameters;
    private final ObservableList<Waypoint> waypoints;
    private final List<Waypoint> newWaypoints;
    private final Consumer<String> csm;
    private final Pane pane;

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParameters, ObservableList<Waypoint> waypoints, Consumer<String> csm) {
        this.graph = graph;
        this.mapViewParameters = mapViewParameters;
        this.waypoints = waypoints;
        this.csm = csm;
        newWaypoints = new LinkedList<>();
        oldDeltaX = new SimpleDoubleProperty(0);
        oldDeltaY = new SimpleDoubleProperty(0);
        deltaX = new SimpleDoubleProperty(0);
        deltaY = new SimpleDoubleProperty(0);

        pane = new Pane();
        drawWaypoints();
        this.waypoints.addListener(
                (Observable o) -> drawWaypoints());
        this.mapViewParameters.addListener((p, oldS, newS) -> {
            assert oldS == null;
            drawWaypoints();
        });
    }

    /**
     * draws the waypoints
     */
    private void drawWaypoints() {
        int MAX_INDEX = waypoints.size() - 1;
        pane.getChildren().clear();
        for (int i = 0; i <= MAX_INDEX; i++) {
            Waypoint wpt = waypoints.get(i);
            PointCh point = wpt.pointCh();
            Group group = new Group();
            group.setId(Integer.toString(wpt.id()));
            group.getStyleClass().add(PIN);
            SVGPath outerPin = new SVGPath();
            outerPin.setContent(PIN_LINE);
            outerPin.getStyleClass().add(PIN_STYLE);
            SVGPath innerCircle = new SVGPath();
            innerCircle.setContent(CIRCLE);
            innerCircle.getStyleClass().add(CIRCLE_STYLE);

            if (i == 0) {
                group.getStyleClass().add(FIRST_PIN_STYLE);
            } else if (i == MAX_INDEX) {
                group.getStyleClass().add(LAST_PIN_STYLE);
            } else {
                group.getStyleClass().add(DEFAULT_PIN_STYLE);
            }
            group.getChildren().add(outerPin);
            group.getChildren().add(innerCircle);
            PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(point);

            group.setLayoutX(mapViewParameters.get().viewX(pointWebMercator) );
            group.setLayoutY(mapViewParameters.get().viewY(pointWebMercator) );
            int finalI = i;


            group.setOnMouseClicked(e -> {
                if (e.isStillSincePress()) removeWaypoint(finalI);
            });

            group.setOnMousePressed(e -> {
                deltaX.setValue(e.getX());
                deltaY.setValue(e.getY());
            });
            group.setOnMouseDragged(e -> {
                group.setLayoutX(group.getLayoutX() + e.getX() - deltaX.get());
                group.setLayoutY(group.getLayoutY() + e.getY() - deltaY.get());
                moveWaypoint(group);
            });
            group.setOnMouseReleased(e -> {
                if (e.isStillSincePress()) return;
                PointWebMercator newPoint = PointWebMercator.of(mapViewParameters.get().zoomLevel(),
                        mapViewParameters.get().x() + group.getLayoutX(),
                        mapViewParameters.get().y() + group.getLayoutY());
                addWaypoint(newPoint.toPointCh(), finalI);
            });
            pane.getChildren().add(group);
        }
    }

    /**
     *
     * @return the pane with the waypoints on it
     */
    public Pane pane() {
        pane.setPickOnBounds(false);
        return pane;
    }

    /**
     * Manages waypoint movement
     * @param group the selected waypoint's
     *              JavaFX group
     */
    private void moveWaypoint(Group group) {
        group.setLayoutX(group.getLayoutX() + (deltaX.get() - oldDeltaX.get()));
        group.setLayoutY(group.getLayoutY() + (deltaY.get() - oldDeltaY.get()));
    }

    /**
     * Adds a waypoint to a certain
     * index in the list
     * @param pointCh the waypoint's coordinates
     * @param index the index at which the waypoint
     *              is to be added
     */
    public void addWaypoint(PointCh pointCh, int index) {
        int newWpt = graph.nodeClosestTo(pointCh, SEARCH_DISTANCE);
        if (newWpt == -1) {
            csm.accept("Aucune route à proximité !");
        } else {
            newWaypoints.clear();
            newWaypoints.addAll(waypoints);
            if (newWaypoints.size() <= index)
                newWaypoints.add(new Waypoint(pointCh, newWpt));
            else
                newWaypoints.set(index, new Waypoint(pointCh, newWpt));

            waypoints.setAll(newWaypoints);
        }
    }

    /**
     * Adds a waypoint at the end of the list
     * @param pointCh the waypoint's coordinates
     */
    public void addWaypoint(PointCh pointCh) {
        addWaypoint(pointCh, waypoints.size());
    }

    /**
     * Removes a waypoint from an index
     * @param index the index at which we
     *              want to remove the
     *              waypoint
     */
    public void removeWaypoint(int index) {
        newWaypoints.clear();
        newWaypoints.addAll(waypoints);
        newWaypoints.remove(index);
        waypoints.setAll(newWaypoints);
    }
}