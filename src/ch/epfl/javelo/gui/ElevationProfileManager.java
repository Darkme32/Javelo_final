package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Manages the display and interaction with
 * the longitudinal profile of a route
 *
 * @author Menzo Bouaissi (340377)
 * @author Imade Bouhamria (339659)
 */
public final class ElevationProfileManager {

    private final static int[] POS_STEPS =
            {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
    private final static int[] ELE_STEPS =
            {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};
    private final static int VERTICAL_SPACE = 25;
    private final static int HORIZONTAL_SPACE = 50;
    private final static int ONE_THOUSAND_METER = 1000;
    private final static int ORIGIN_ZERO = 0;

    private final ElevationProfile elevationProfile;
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileP;
    private final DoubleProperty factorHeightP, factorWidthP, mousePosition, highlightedPosition;
    private final ObjectProperty<Rectangle2D> rectangleProperties;
    private final ObjectProperty<Transform> worldToScreenTransformationP;
    private final ObjectProperty<Transform> screenToWorldTransformationP;
    private final ObjectProperty<Text> xLabel, yLabel;

    private final Pane centerPane = new Pane();
    private final Group labels = new Group();
    private final BorderPane borderPane = new BorderPane();
    private final Path grid = new Path();
    private final VBox vbox = new VBox();
    private final Polygon profile = new Polygon();
    private final Line position = new Line();
    private final Text statsText = new Text();
    private final List<Double> coordinates = new ArrayList<>();


    private final static Insets limit = new Insets(10, 10, 20, 40);
    private final static Font font = Font.font("Avenir", 10);


    /**
     * The Profile Manager, we set up
     * the css, and listen some property.
     *
     * @param elevationProfile    a property that contains the profile
     * @param highlightedPosition a property that contains the highlighted position
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile,
                                   ReadOnlyDoubleProperty highlightedPosition) {
        this.elevationProfileP = elevationProfile;
        this.elevationProfile = elevationProfile.get();
        this.factorHeightP = new SimpleDoubleProperty();
        this.factorWidthP = new SimpleDoubleProperty();
        this.mousePosition = new SimpleDoubleProperty(Double.NaN);
        this.highlightedPosition = new SimpleDoubleProperty(Double.NaN);
        this.rectangleProperties = new SimpleObjectProperty<>();
        this.worldToScreenTransformationP = new SimpleObjectProperty<>();
        this.screenToWorldTransformationP = new SimpleObjectProperty<>();

        xLabel = new SimpleObjectProperty<>();
        yLabel = new SimpleObjectProperty<>();

        this.highlightedPosition.bind(highlightedPosition);
        vbox.alignmentProperty().setValue(Pos.CENTER);

        centerPane.getStylesheets().add("elevation_profile.css");
        grid.setId("grid");
        profile.setId("profile");

        statsText.getStyleClass().add("profile");

        centerPaneListener();
        borderPaneListener();
        propertyListener();

        borderPane.setCenter(centerPane);
        borderPane.setBottom(vbox);

        BorderPane.setMargin(centerPane, limit);
    }

    /**
     * Modifies the mouse position if it moves on the
     * pane or leaves it.
     */
    private void borderPaneListener() {
        borderPane.setOnMouseMoved(e -> {
            if (!rectangleProperties.get().contains(e.getX(), e.getY())) {
                mousePosition.setValue(Double.NaN);
                return;
            }
            mousePosition.setValue(screenToWorldX(e.getX() - rectangleProperties.get().getMinX()));
        });
        borderPane.setOnMouseExited(e -> mousePosition.setValue(Double.NaN));
    }

    /**
     * Listens to the properties
     */
    private void propertyListener() {
        highlightedPosition.addListener((p, o, n) -> drawLine());
        elevationProfileP.addListener((p, o, n) -> {
            drawGraph();
            drawStats();
        });
    }

    /**
     * Listens to the center pane, and modifies
     * some value
     */
    private void centerPaneListener() {
        centerPane.widthProperty().addListener((p, o, n) -> {
            factorWidthP
                    .set(n.doubleValue() / (this.elevationProfile.length()));
            drawGraph();
            drawStats();
        });

        centerPane.heightProperty().addListener((p, o, n) -> {
            factorHeightP
                    .set((n.doubleValue() /
                            (this.elevationProfile.maxElevation() - this.elevationProfile.minElevation())));
            drawGraph();
            drawStats();
        });
    }

    public Pane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePosition;
    }


    /**
     * This method draws the profile.
     * To do that, we look at all the
     * elevation points, then convert them
     * in order to have the right unit to
     * the pane. Finally, we put the coordinates
     * in a Polygon, and we draw it. For the
     * points of the profile, we use the ceil
     * method, because, in some cases, the draw
     * finished too early, and we could see the
     * profile go down to early
     */
    private void drawProfile() {

        rectangleProperties.setValue(new Rectangle2D(limit.getLeft(), limit.getTop(), worldToScreenX(elevationProfile.length()),
                worldToScreenY(-elevationProfile.minElevation())));

        /*Bottom left point*/
        coordinates.add(worldToScreenX(ORIGIN_ZERO));
        coordinates.add(worldToScreenY(-elevationProfile.minElevation()));
        /*The points of the profile*/
        coordinates.addAll(IntStream.range(ORIGIN_ZERO, (int) Math.ceil(rectangleProperties.get().getWidth()))
                .mapToDouble(this::screenToWorldX)
                .flatMap(i -> DoubleStream.of(worldToScreenX(i), worldToScreenY(-elevationProfile.elevationAt(i))))
                .boxed()
                .toList());

        /*Bottom right point*/
        coordinates.add(worldToScreenX(elevationProfile.length()));
        coordinates.add(worldToScreenY(-elevationProfile.minElevation()));

        profile.getPoints().setAll(coordinates);
        centerPane.getChildren().add(profile);
        coordinates.clear();
    }


    /**
     * This method uses the methods below to
     * draw a complete graph of the profiles
     */
    private void drawGraph() {

        try {
            setWorldToScreen();
            setScreenToWorld();
        } catch (NonInvertibleTransformException nonInvertibleTransformException) {
            return;
        }

        centerPane.getChildren().clear();
        drawProfile();
        drawGrid();
    }

    /**
     * This method draws the grid. In order to do that
     * we need to find the largest distance such that a
     * line is drawn every 50 JavaFX unit(for the horizontal
     * lines). Once we find this distance, we draw the lines
     * in every multiple of this distance. The same process
     * is used for the vertical lines(with 25 JavaFX unit).
     */
    private void drawGrid() {
        grid.getElements().clear();
        labels.getChildren().clear();

        drawHorizontalLine();
        drawVerticalLine();

        centerPane.getChildren().add(grid);
        centerPane.getChildren().add(labels);
    }


    /**
     * Draws the horizontal lines
     */
    private void drawHorizontalLine() {
        int arrayPosition = 0;
        /*
        Find the smallest distance
         */
        while ((worldToScreenTransformationP.get().deltaTransform(ORIGIN_ZERO, (ELE_STEPS[arrayPosition])).getY() < VERTICAL_SPACE)
                && arrayPosition < ELE_STEPS.length - 1) arrayPosition++;
        /*
        Draw the lines
         */
        for (double i = ELE_STEPS[arrayPosition] * Math.ceil(elevationProfile.minElevation() / ELE_STEPS[arrayPosition]);
             i < elevationProfile.maxElevation(); i = i + ELE_STEPS[arrayPosition]) {
            double y = worldToScreenY(-i);
            grid.getElements().add(new MoveTo(ORIGIN_ZERO, y));
            grid.getElements().add(new LineTo(rectangleProperties.get().getWidth(), y));
            drawYAxis(i, y);
        }
    }

    /**
     * Draws the vertical lines
     */
    private void drawVerticalLine() {
        int arrayPosition = 0;
         /*
        Find the smallest distance
         */
        while ((factorWidthP.get() * POS_STEPS[arrayPosition] < HORIZONTAL_SPACE) && arrayPosition < POS_STEPS.length - 1) {
            arrayPosition++;
        }
         /*
        Draw the lines
         */
        for (int i = 0; i < elevationProfile.length(); i = i + POS_STEPS[arrayPosition]) {
            double x = worldToScreenX(i);
            grid.getElements().add(new MoveTo(x, ORIGIN_ZERO));
            grid.getElements().add(new LineTo(x, rectangleProperties.get().getHeight()));
            drawXAxis(i / ONE_THOUSAND_METER, x);
        }
    }

    /**
     * This method is used to create
     * labels for the x axis
     *
     * @param colNb the value that will be
     *              shown on the label
     * @param x     the x coordinate of the
     *              label's line
     */
    private void drawXAxis(int colNb, double x) {

        xLabel.setValue(new Text(Integer.toString(colNb)));

        xLabel.get().getStyleClass().add("grid_label");
        xLabel.get().getStyleClass().add("horizontal");

        xLabel.get().setTextOrigin(VPos.TOP);
        xLabel.get().setTextAlignment(TextAlignment.CENTER);

        xLabel.get().setX(x - (xLabel.get().prefWidth(0) / 2));
        xLabel.get().setY(rectangleProperties.get().getHeight());

        xLabel.get().setFont(font);

        labels.getChildren().add(xLabel.get());
    }

    /**
     * This method is used to create
     * labels for the y axis
     *
     * @param altitude the value that will be
     *                 shown on the label
     * @param y        the y coordinate of the
     *                 label's line
     */
    private void drawYAxis(double altitude, double y) {

        yLabel.setValue(new Text(Integer.toString((int) altitude)));

        yLabel.get().getStyleClass().add("grid_label");
        yLabel.get().getStyleClass().add("vertical");

        yLabel.get().setTextOrigin(VPos.CENTER);
        yLabel.get().setTextAlignment(TextAlignment.RIGHT);

        yLabel.get().setX(-(yLabel.get().prefWidth(0) + 2));
        yLabel.get().setY(y);

        yLabel.get().setFont(font);

        labels.getChildren().add(yLabel.get());

    }

    /**
     * This method draws the
     * highlight line over the
     * graph, and binds it to
     * the position of the mouse
     */
    private void drawLine() {

        borderPane.getChildren().remove(position);
        if (worldToScreenTransformationP.isNull().get()) return;
        position.setMouseTransparent(true);
        position.layoutXProperty().bind(
                Bindings.createDoubleBinding(() -> worldToScreenX(highlightedPosition.get()) + rectangleProperties.get().getMinX()));
        position.startYProperty().bind(Bindings.select(rectangleProperties, "minY"));
        position.endYProperty().bind(Bindings.select(rectangleProperties, "maxY"));
        position.visibleProperty().bind(highlightedPosition.greaterThanOrEqualTo(0));

        borderPane.getChildren().add(position);
    }

    /**
     * This method draws the various
     * statistics about the route at
     * the bottom of the graph
     */
    private void drawStats() {

        vbox.getChildren().clear();

        String stats = String.format("Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",
                elevationProfile.length() / ONE_THOUSAND_METER,
                elevationProfile.totalAscent(),
                elevationProfile.totalDescent(),
                elevationProfile.minElevation(),
                elevationProfile.maxElevation());

        statsText.setText(stats);
        statsText.setY(vbox.getHeight()/2D);

        vbox.getChildren().add(statsText);
    }

    /**
     * Makes the transformation from the screen
     * coordinates to the world coordinates
     *
     * @throws NonInvertibleTransformException if the transformation is not possible
     */
    private void setScreenToWorld() throws NonInvertibleTransformException {
        screenToWorldTransformationP.set(worldToScreenTransformationP.get().createInverse());
    }

    /**
     * Makes the transformation from the world
     * coordinates to the screen coordinates
     */
    private void setWorldToScreen() {

        if (elevationProfile == null) return;
        Affine affine = new Affine();
        affine.prepend(Transform.translate(ORIGIN_ZERO, elevationProfile.maxElevation()));
        affine.prepend(Transform.scale(factorWidthP.get(), factorHeightP.get()));
        affine.prepend(Transform.translate(ORIGIN_ZERO, ORIGIN_ZERO));
        worldToScreenTransformationP.set(affine);
    }

    /**
     * the x coordinate from the world
     * to screen transformation
     *
     * @param value the value to be converted
     * @return the converted value
     */
    private double worldToScreenX(double value) {
        return worldToScreenTransformationP.get().transform(value, ORIGIN_ZERO).getX();
    }

    /**
     * the y coordinate from the world
     * to screen transformation
     *
     * @param value the value to be converted
     * @return the converted value
     */
    private double worldToScreenY(double value) {
        return worldToScreenTransformationP.get().transform(ORIGIN_ZERO, value).getY();
    }

    /**
     * the x coordinate from the screen
     * to world transformation
     *
     * @param value the value to be converted
     * @return the converted value
     */
    private double screenToWorldX(double value) {
        return screenToWorldTransformationP.get().transform(value, ORIGIN_ZERO).getX();
    }

}