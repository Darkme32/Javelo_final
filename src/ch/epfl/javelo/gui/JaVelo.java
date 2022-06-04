package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

import static ch.epfl.javelo.routing.GpxGenerator.writeGpx;
/**
 * Main class of JaVelo
 *
 * @author Imade Bouhamria (339659)
 */
public final class JaVelo extends Application {

    private final static String NAME = "JaVelo";
    private final static int MIN_WIDTH = 800;
    private final static int MIN_HEIGHT = 600;
    private final static int THREE_PANE = 3;
    private final static int TWO_PANE = 2;
    private final static int PANE_INDEX_ONE = 1;

    private final DoubleProperty highlightedPosition;

    /**
     * Javelo's main class
     * Manages all the macro-elements
     * and displays them into a window
     */
    public JaVelo() {
        highlightedPosition = new SimpleDoubleProperty(Double.NaN);
    }

    /**
     * Main program
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        /*
         * Loading up the necessary data
         * and objects
         */
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("osm-cache.");
        String tileServerHost = "tile.openstreetmap.org";
        CostFunction cf = new CityBikeCF(graph);
        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);
        RouteBean routeBean = new RouteBean(new RouteComputer(graph, cf));
        routeBean.setWaypoints(FXCollections.observableArrayList());
        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = errorManager::displayError;
        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph,
                tileManager,
                routeBean,
                errorConsumer);
        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);

        routeBean.highlightedPositionProperty().bind(highlightedPosition);

        /*
        Display of the elevation profile and the
        highlighting circle when appropriate
         */
        routeBean.waypoints().addListener((ListChangeListener<? super Waypoint>) e -> {

            if (routeBean.elevationProfileProperty().get() != null) {
                ElevationProfileManager elevationProfileManager =
                        new ElevationProfileManager(routeBean.elevationProfileProperty(), highlightedPosition);

                highlightedPosition.bind(Bindings.when(
                                Bindings.greaterThanOrEqual(annotatedMapManager.mousePositionOnRouteProperty(),0))
                        .then(annotatedMapManager.mousePositionOnRouteProperty())
                        .otherwise(elevationProfileManager.mousePositionOnProfileProperty()));

                splitPane.getItems().add(elevationProfileManager.pane());
                if (splitPane.getItems().size() == THREE_PANE)
                    splitPane.getItems().remove(PANE_INDEX_ONE);

            } else if (splitPane.getItems().size() == TWO_PANE)
                splitPane.getItems().remove(PANE_INDEX_ONE);

        });

        /*
        Set up of the menu bar and
        the "Export to GPX" button
         */
        MenuBar menuBar = new MenuBar();
        Menu file = new Menu("Fichier");
        MenuItem export = new MenuItem("Exporter GPX");
        export.disableProperty().bind(routeBean.routeProperty().isNull());
        export.setOnAction(e -> writeGpx("javelo",
                routeBean.routeProperty().get(),
                routeBean.elevationProfileProperty().get()));
        file.getItems().add(export);
        menuBar.getMenus().add(file);
        menuBar.setUseSystemMenuBar(true);


        /*
        Adding every sub-element to the
        main window
         */
        StackPane stackPane = new StackPane(splitPane, errorManager.pane());

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(stackPane);
        mainPane.setTop(menuBar);
        mainPane.getStylesheets().add("map.css");

        primaryStage.setTitle(NAME);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

}