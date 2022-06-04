package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Manages the display and interaction
 * with the background map
 *
 * @author Menzo Bouaissi (340377)
 */
public class BaseMapManager {
    private static final int TILE_SIZE = 256;
    private static final int MIN_ZOOM_LIMIT = 7;
    private static final int MAX_ZOOM_LIMIT = 20;
    private static final int ZOOM_FACTOR = 2;
    private static final int ONE_TILE = 1;

    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final SimpleLongProperty minScrollTime;
    private final BooleanProperty redrawNeeded;

    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final Canvas canvas;
    private final Pane pane;
    private final GraphicsContext graphicsContext;

    /**
     * Manages the map. The listener updates
     * the map at each user interaction
     *
     * @param tileManager      the tile manager
     * @param waypointsManager the waypoint manager
     * @param objectProperty   an ObjectProperty containing
     *                         the map parameters
     */
    public BaseMapManager(TileManager tileManager,
                          WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> objectProperty) {

        this.minScrollTime = new SimpleLongProperty();
        this.redrawNeeded = new SimpleBooleanProperty();

        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParametersP = objectProperty;
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);
        this.graphicsContext = canvas.getGraphicsContext2D();


        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        canvas.widthProperty().addListener((p, oldS, newS) ->
                redrawOnNextPulse());
        canvas.heightProperty().addListener((p, oldS, newS) ->
                redrawOnNextPulse());
        mapViewParametersP.addListener((p, oldS, newS) ->
                redrawOnNextPulse());
        mouseHandler();
    }

    /**
     * Returns the pane
     *
     * @return the pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Redraws the map
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded.get()) return;
        redrawNeeded.set(false);
        drawTiles();
    }

    /**
     * Draws the map when it's called. We first determine the
     * top left corner of the map, with the correct ID. Then we
     * calculate the shift, and we finally ask and draw the images.
     */
    private void drawTiles() {
        double topLeftToCanvasX = mapViewParametersP.get().x() / TILE_SIZE;
        double topLeftToCanvasY = mapViewParametersP.get().y() / TILE_SIZE;

        /*We extract the double part,
         and multiply it by the size of a tile*/
        double shiftX = (topLeftToCanvasX % 1) * TILE_SIZE;
        double shiftY = (topLeftToCanvasY % 1) * TILE_SIZE;

        try {
            for (int i = -ONE_TILE;
                 i < Math2.ceilDiv((int) canvas.getWidth(), TILE_SIZE) + ONE_TILE; i++) {
                for (int j = -ONE_TILE;
                     j < Math2.ceilDiv((int) canvas.getHeight(), TILE_SIZE) + ONE_TILE; j++) {

                    Image image = tileManager.imageForTileAt(
                            new TileManager.TileId(mapViewParametersP.get().zoomLevel(),
                                    (int) (topLeftToCanvasX + i),
                                    (int) (topLeftToCanvasY + j)));

                    graphicsContext.drawImage(image, i * TILE_SIZE - shiftX,
                            j * TILE_SIZE - shiftY);
                }
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Handles the mouse interactions with the canvas
     * We create a simpleObjectProperty, in order to
     * store the value of the dragged point.
     */
    private void mouseHandler() {
        SimpleObjectProperty<Point2D> draggedPoint = new SimpleObjectProperty<>();

      /*
        If the user click, then we add a waypoint
       */
        pane.setOnMouseClicked(e -> {
            if (!e.isStillSincePress()) return;
            waypointsManager.addWaypoint(mapViewParametersP
                    .get()
                    .pointAt(e.getX(), e.getY())
                    .toPointCh());
        });

        /*
          If the mouse is pressed, we store the actual position on
          the pane, in order to know later what is the actual shift
         */
        pane.setOnMousePressed(e -> draggedPoint.set(new Point2D(e.getX(), e.getY())));
        /*
          If the user drag the mouse, then we calculate the shift
          between the initial position and the actual position of
          the cursor
         */
        pane.setOnMouseDragged(e -> {
            draggedPoint.set(draggedPoint.get().subtract(e.getX(), e.getY()));
            mapViewParametersP.set(
                    mapViewParametersP.get().withMinXY(
                            mapViewParametersP.get().x() + draggedPoint.get().getX(),
                            mapViewParametersP.get().y() + draggedPoint.get().getY()));
            draggedPoint.set(new Point2D(e.getX(), e.getY()));
        });

        /*
           When the user scroll the mouse, we first determine
           the direction of the scroll, then we do the operation
           in order to have the correct top left corner
         */
        pane.setOnScroll(e -> {
            if (e.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int level = (int) Math.signum(e.getDeltaY());

            draggedPoint.set(new Point2D(e.getX(), e.getY()));
            if (!((mapViewParametersP.get().zoomLevel() + level > MIN_ZOOM_LIMIT)
                    && (MAX_ZOOM_LIMIT > mapViewParametersP.get().zoomLevel() + level))) return;

            Point2D newPosition = level > 0 ?
                    mapViewParametersP
                            .get()
                            .topLeft()
                            .multiply(Math.pow(ZOOM_FACTOR, level))
                            .add(draggedPoint.get()) :
                    mapViewParametersP
                            .get()
                            .topLeft()
                            .subtract(draggedPoint.get())
                            .multiply(Math.pow(ZOOM_FACTOR, level));

            mapViewParametersP.set(MapViewParameters.of(
                    mapViewParametersP.get().zoomLevel() + level,
                    newPosition.getX(),
                    newPosition.getY()));
        });
    }

    /**
     * Redraws the map, when it's called
     */
    private void redrawOnNextPulse() {
        redrawNeeded.set(true);
        Platform.requestNextPulse();
    }


}