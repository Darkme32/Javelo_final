package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TestTileManager extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TileManager tm = new TileManager(
                Path.of("."), "tile.openstreetmap.org");

        Image tileImage = tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185422));
        tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185422));tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185422));tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185422));tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185422));tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185422));tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185422));tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185422));tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185422));tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185422));tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185422));
        Image tileImage2 = tm.imageForTileAt(
                new TileManager.TileId(19, 271625, 185423));
        for (int i = 0; i < 120; i++) {
            tm.imageForTileAt(
                    new TileManager.TileId(19, 271625, 185422+i));
        }
        //Creating the image view
        ImageView imageView = new ImageView();
        //Setting image to the image view
        imageView.setImage(tileImage);
        imageView.setPreserveRatio(true);
        //Setting the Scene object
        Group root = new Group(imageView);
        Scene scene = new Scene(root);
        primaryStage.setTitle("Displaying Image");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Test
    void test()throws Exception{
        TileManager tm = new TileManager(
                Path.of("."), "tile.openstreetmap.org");

        assertThrows(IllegalArgumentException.class, () -> {
            tm.imageForTileAt(
                    new TileManager.TileId(1, 435, 43));
        });
    }
    @Test
    void test3()throws Exception{
        TileManager tm = new TileManager(
                Path.of("."), "tile.openstreetmap.org");

        assertThrows(IllegalArgumentException.class, () -> {
            tm.imageForTileAt(
                    new TileManager.TileId(9, 5096, 2048));
        });
    }
    @Test
    void test2()throws Exception{
        TileManager tm = new TileManager(
                Path.of("."), "tile.openstreetmap.org");

        Image tileImage = tm.imageForTileAt(
                new TileManager.TileId(19, 271726, 185422));
    }
}