package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;

/**
 * Draws the route and the corresponding
 * highlighted position
 *
 * @author Imade Bouhamria (339659)
 */
public final class RouteManager {

    private final static int CIRCLE_RADIUS = 5;
    private final static int MIN_WPTS = 2;
    private final Pane pane;
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;
    private final Circle circle;
    private final Polyline line;
    private final List<Waypoint> newWaypoints;

    /**
     *
     * @param routeBean contains all the properties
     *                  related to the route, such as
     *                  the waypoints for instance
     * @param mapViewParameters is used to convert
     *                          geographical coordinates
     *                          into points on the screen
     * @param stringConsumer displays error messages
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParameters, Consumer<String> stringConsumer) {
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParameters;

        newWaypoints = new ArrayList<>();

        line = new Polyline();
        line.setId("route");

        circle = new Circle(CIRCLE_RADIUS);
        circle.setId("highlight");

        pane = new Pane();


        this.routeBean.waypoints().addListener(
                (Observable o)-> {
                    if (routeBean.routeProperty().isNull().get()) {
                        visibility();
                        if (!(routeBean.waypoints().size() < MIN_WPTS))
                            stringConsumer.accept("Aucun itinéraire disponible !");
                    } else {
                        draw();
                        moveCircle();
                        lineVisibility(true);
                    }
                });
        this.mapViewParameters.addListener((p, oldS, newS) -> {
            if (routeBean.routeProperty().isNull().get() ||
                    routeBean.waypoints().size() < MIN_WPTS) {
                visibility();
                return;
            }
            lineVisibility(true);
            draw();
        });

        this.routeBean.highlightedPositionProperty().addListener((p, o, n) -> {
            moveCircle();
            circleVisibility(!Double.isNaN(routeBean.highlightedPositionProperty().get()));
        });
    }

    /**
     * Makes the itinerary
     * and the highlighted
     * position (in)visible
     */
    private void visibility() {
        lineVisibility(false);
        circleVisibility(false);
    }

    /**
     * Makes the highlighted
     * position (in)visible
     *
     * @param visible true if we
     *                want the circle
     *                to be visible
     */
    private void circleVisibility(boolean visible) {
        circle.setMouseTransparent(!visible);
        circle.opacityProperty().set(visible ? 100 : 0);
    }

    /**
     * Makes the itinerary
     * (in)visible
     *
     * @param visible true if we
     *                want the circle
     *                to be visible
     */
    private void lineVisibility(boolean visible) {
        line.setMouseTransparent(!visible);
        line.opacityProperty().set(visible ? 100 : 0);
    }

    /**
     * Draws the itinerary
     */
    private void line() {
        line.getPoints().setAll(routeBean.routeProperty().get().points()
                .stream()
                .map(PointWebMercator::ofPointCh)
                .flatMapToDouble(e -> DoubleStream.of(mapViewParameters.get().viewX(e),
                                                      mapViewParameters.get().viewY(e)))
                .boxed()
                .toList());
    }

    /**
     * Draws the highlighted position
     */
    private void circle() {

        circle.setOnMouseClicked(e -> {
            Point2D point = pane.localToParent(circle.getCenterX(), circle.getCenterY());
            PointWebMercator pointWebMercator = mapViewParameters
                    .get()
                    .pointAt(point.getX(), point.getY());
            int segIndex = routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition()) + 1;

            newWaypoints.clear();
            newWaypoints.addAll(routeBean.waypoints());
            newWaypoints.add(segIndex, new Waypoint(pointWebMercator.toPointCh(),
                    routeBean.routeProperty().get().nodeClosestTo(routeBean.highlightedPosition())));

            routeBean.setWaypoints(newWaypoints);

        });

    }

    /**
     * Moves the highlighted position
     */
    private void moveCircle() {
        if (!Double.isNaN(routeBean.highlightedPosition())) {
            PointCh highlightedPoint = routeBean.routeProperty().get().pointAt(routeBean.highlightedPosition());
            assert highlightedPoint != null;

            PointWebMercator pWM = PointWebMercator.ofPointCh(highlightedPoint);

            circle.setCenterX(mapViewParameters.get().viewX(pWM));
            circle.setCenterY(mapViewParameters.get().viewY(pWM));
        }
    }

    /**
     * Draws all the elements
     */
    private void draw() {
        pane.getChildren().clear();
        line();
        circle();
        moveCircle();
        pane.getChildren().add(line);
        pane.getChildren().add(circle);
    }

    /**
     * @return A pane containing both
     * the itinerary and the highlighted
     * position
     */
    public Pane pane() {
        pane.setPickOnBounds(false);
        return pane;
    }
}
