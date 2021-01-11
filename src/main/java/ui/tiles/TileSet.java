package ui.tiles;

import javafx.scene.image.Image;

public interface TileSet {

    /**
     * Returns an Image object for the specified index.
     * @param index index of requested tile
     * @return Image instance of the specified index
     */

    Image getTile(int index);

    /**
     * Gets width and height of an individual tile
     * @return [x,y] tile resolution as int
     */
    int[] getTileResolution();
}
