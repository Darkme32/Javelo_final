package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

/**
 * Combines the background with
 * the route and the errors
 *
 * @author Imade Bouhamria (339659)
 */
public final class AnnotatedMapManager {

    private final static int DEFAULT_ZOOM = 12;
    private final static int DEFAULT_X = 543200;
    private final static int DEFAULT_Y = 370650;
    private final static int MAX_DISTANCE = 15;

    private final ObjectProperty<MapViewParameters> mapViewParameters;
    private final DoubleProperty mousePosition;

    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final RouteManager routeManager;

    private final StackPane mainPane;


    /**
     *
     * @param graph the map's graph
     * @param tileManager the tile manager
     * @param routeBean contains all the properties
     *                  related to the route
     * @param errorConsumer displays the errors
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> errorConsumer) {

        mapViewParameters = new SimpleObjectProperty<>(new MapViewParameters(DEFAULT_ZOOM, DEFAULT_X, DEFAULT_Y));

        mousePosition = new SimpleDoubleProperty(Double.NaN);
        waypointsManager = new WaypointsManager(graph, mapViewParameters, routeBean.waypoints(), errorConsumer);
        baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParameters);
        routeManager = new RouteManager(routeBean, mapViewParameters, errorConsumer);

        mainPane = new StackPane(
                baseMapManager.pane(),
                routeManager.pane(),
                waypointsManager.pane()
        );

        mainPane.setOnMouseMoved(e -> mouseMovement(routeBean,e.getX(), e.getY()));
        mainPane.setOnMouseClicked(e -> mouseMovement(routeBean,e.getX(), e.getY()));
        mainPane.setOnMouseReleased(e -> mouseMovement(routeBean,e.getX(), e.getY()));
        mainPane.setOnScroll(e -> mouseMovement(routeBean, e.getX(),e.getY()));
        mainPane.setOnMouseExited(e -> mousePosition.setValue(Double.NaN));

        mainPane.getStylesheets().add("map.css");
    }

    /**
     * Updates the mouse position when
     * appropriate
     *
     * @param routeBean the current route bean
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     */
    private void mouseMovement(RouteBean routeBean, double mouseX, double mouseY) {
        if (routeBean.routeProperty().isNull().get()) return;
        PointCh mousePointCH = mapViewParameters.get().pointAt(mouseX, mouseY).toPointCh();
        if (mousePointCH == null) return;
        RoutePoint routePointRP = routeBean.routeProperty().get().pointClosestTo(mousePointCH);
        PointWebMercator routePoint = PointWebMercator.ofPointCh(routePointRP.point());

        double x = mouseX - mapViewParameters.get().viewX(routePoint);
        double y = mouseY - mapViewParameters.get().viewY(routePoint);

        double distance = Math2.norm(x, y);

        if (distance <= MAX_DISTANCE) {
            mousePosition.setValue(routePointRP.position());
        } else {
            mousePosition.setValue(Double.NaN);
        }
    }

    /**
     * @return the pane combining the map,
     * the waypoints and the route
     */
    public Pane pane() {
        return mainPane;
    }

    /**
     * @return the mouse position
     */
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty() {
        return mousePosition;
    }
}

