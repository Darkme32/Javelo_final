package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * Represents an OSM tile manager
 *
 * @author Menzo Bouaissi (340377)
 */
public final class TileManager {
    private final static int MAX_NBR_OF_TILE = 100;

    private final Path basePath;
    private final String serverName;
    private final LinkedHashMap<String, Image> cache;

    /**
     * Represents an OSM tile manager
     *
     * @param basePath   the path where the tiles
     *                   are stored
     * @param serverName the name of the server
     */
    public TileManager(Path basePath, String serverName) {
        this.basePath = basePath;
        this.serverName = serverName;
        this.cache = new LinkedHashMap<>(100);
    }

    /**
     * Returns the Image corresponding to a certain
     * tileId. First, we check if the tileId is valid,
     * then we  verify whether the image is already in the
     * memory cache, or in the disk cache . If not, we
     * pull it from the server, and save it in the
     * memory and disk cache
     *
     * @param tileId the id of the tile
     * @return the image of the tile, if it exists
     * @throws IOException if the arguments for the tile
     *                     are invalid
     */
    public Image imageForTileAt(TileId tileId) throws IOException {
        Preconditions.checkArgument(
                TileId.isValid(tileId.zoomLevel(), tileId.x, tileId.y));
        if (!wasInMemoryCache(tileId)) {
            if (!Files.exists(basePath.resolve(tileId.toString()))) {
                Path path = basePath;

                path = path.resolve(tileId.zoomLevel +
                        "/" +
                        (int) tileId.x +
                        "/");
                if (!Files.exists(path)) Files.createDirectories(path);
                path = path.resolve((int) tileId.y + ".png");

                URL u = new URL("https://" +
                        serverName +
                        "/" +
                        tileId);
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "JaVelo");
                try (InputStream inputStream
                             = c.getInputStream();
                     FileOutputStream diskCache
                             = new FileOutputStream(path.toFile())) {
                    inputStream.transferTo(diskCache);
                }
            }
            addImage(tileId);
        }
        return getValue(tileId);
    }

    /**
     * Checks whether the tile is in
     * the cache memory
     *
     * @param tileId the id of the tile
     * @return true if the tile is in the cache memory
     */
    private boolean wasInMemoryCache(TileId tileId) {
        return cache.containsKey(tileId.toString());
    }

    /**
     * Returns the Image of a certain tile
     *
     * @param tileId the id of the tile
     * @return the Image of a certain tile
     */
    private Image getValue(TileId tileId) {
        return cache.get(tileId.toString());
    }

    /**
     * Sets a value in the cache memory of
     * a certain tile
     *
     * @param tileId the id of the tile
     * @param image  the Image corresponding to the tile
     */
    private void setValue(TileId tileId, Image image) {
        cache.put(tileId.toString(), image);
        if (cache.size() > MAX_NBR_OF_TILE)
            cache.remove(cache.keySet().iterator().next());
    }

    /**
     * Adds the image into the cache
     *
     * @param tileId the id of the tile
     * @throws IOException if the path is incorrect
     */
    private void addImage(TileId tileId) throws IOException {

        try (InputStream inputStream =
                     new FileInputStream(basePath.resolve(tileId.toString()).toFile())) {
            setValue(tileId, new Image(inputStream));
        }

    }

    /**
     * Represents the id of an OSM tile
     *
     * @param zoomLevel the zoom level of the map
     * @param x         the x coordinate
     * @param y         the y coordinate
     */
    public record TileId(int zoomLevel, double x, double y) {

        /**
         * Checks whether the tile is valid
         *
         * @param zoomLevel the zoom level of the map
         * @param x         the x coordinate
         * @param y         the y coordinate
         * @return true if the tile is in the server
         */
        public static boolean isValid(int zoomLevel, double x, double y) {
            return (zoomLevel == 0) ?
                    x == 0 && y == 0 :
                    0 <= x && x < Math.pow(2, zoomLevel) &&
                    0 <= y && y < Math.pow(2, zoomLevel);
        }


        /**
         * Builds the address of a tile
         *
         * @return the address of a tile
         */
        @Override
        public String toString() {
            return this.zoomLevel + "/" +
                    (int) this.x + "/" +
                    (int) this.y + ".png";
        }
    }
}