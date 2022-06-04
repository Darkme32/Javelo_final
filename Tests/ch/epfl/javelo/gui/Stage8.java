package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.gui.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class Stage8 extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of(".");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        CostFunction cf = new CityBikeCF(graph);
        RouteBean routeBean;

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);

        routeBean = new RouteBean(new RouteComputer(graph,cf));
        routeBean.setHighlightedPosition(1000);

        routeBean.setWaypoints( FXCollections.observableArrayList());

        ErrorManager errorManager = new ErrorManager();

        Consumer<String> errorConsumer =errorManager::displayError;

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        routeBean.waypoints(),
                        errorConsumer);
        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);

        RouteManager routeManager = new RouteManager(routeBean, mapViewParametersP, errorConsumer);


        StackPane mainPane =
                new StackPane(baseMapManager.pane(),
                        routeManager.pane(),//;//),
                        waypointsManager.pane(),
                        errorManager.pane());
        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
}










































//package ch.epfl.javelo.gui;
//
//import ch.epfl.javelo.data.Graph;
//import ch.epfl.javelo.gui.*;
//import ch.epfl.javelo.projection.PointCh;
//import javafx.application.Application;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.Scene;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;
//
//import java.nio.file.Path;
//import java.util.Random;
//import java.util.function.Consumer;
//
//public final class Stage8 extends Application {
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
////        Random r = new Random();
//        Graph graph = Graph.loadFrom(Path.of("lausanne"));
//        Path cacheBasePath = Path.of(".");
//        String tileServerHost = "tile.openstreetmap.org";
//        TileManager tileManager =
//                new TileManager(cacheBasePath, tileServerHost);
//
//        MapViewParameters mapViewParameters =
//                new MapViewParameters(12, 543200, 370650);
//        ObjectProperty<MapViewParameters> mapViewParametersP =
//                new SimpleObjectProperty<>(mapViewParameters);
//        ObservableList<Waypoint> waypoints =
//                FXCollections.observableArrayList(
//                        new Waypoint(new PointCh(2532697, 1152350), 159049),
//                        new Waypoint(new PointCh(2538659, 1154350), 117669));
//        Consumer<String> errorConsumer = new ErrorConsumer();
//
//        WaypointsManager waypointsManager =
//                new WaypointsManager(graph,
//                        mapViewParametersP,
//                        waypoints,
//                        errorConsumer);
//
//        BaseMapManager baseMapManager =
//                new BaseMapManager(tileManager,
//                        waypointsManager,
//                        mapViewParametersP);
//
//
//        StackPane mainPane =
//                new StackPane(baseMapManager.pane());
////                        , waypointsManager.pane());
//
//        mainPane.setMaxHeight(baseMapManager.pane().getMaxHeight());
//        mainPane.setMaxWidth(baseMapManager.pane().getMaxWidth());
//
//        mainPane.getStylesheets().add("map.css");
//        primaryStage.setMinWidth(600);
//        primaryStage.setMinHeight(300);
//        primaryStage.setScene(new Scene(mainPane));
//        primaryStage.show();
//    }
//
//    private static final class ErrorConsumer
//            implements Consumer<String> {
//        @Override
//        public void accept(String s) {
//            System.out.println(s);
//        }
//    }
//}