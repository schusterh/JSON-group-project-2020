package ui.tiles;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.List;

public class DefaultTileSet implements TileSet {

    Image tileSetImage;
    int rowCount;
    int colCount;

    List<Image> tileList;

    public final int TILE_WIDTH;
    public final int TILE_HEIGHT;

    public DefaultTileSet(String tileSetPath, int colCount, int rowCount, int tileWidth, int tileHeight) {
        this.colCount = colCount;
        this.rowCount = rowCount;

        this.TILE_WIDTH = tileWidth;
        this.TILE_HEIGHT = tileHeight;

        this.tileSetImage = new Image(getClass().getResourceAsStream("/" + tileSetPath));
        this.tileList = new ArrayList<>();

        this.generateTiles();
    }

    private void generateTiles() {
        for (int y = 0; y < this.rowCount; y++) {
            for (int x = 0; x < this.colCount; x++) {
                this.tileList.add(new WritableImage(this.tileSetImage.getPixelReader(), x * this.TILE_WIDTH, y * this.TILE_HEIGHT, this.TILE_WIDTH, this.TILE_HEIGHT));
            }
        }
    }

    @Override
    public Image getTile(int index) {
        return this.tileList.get(index);
    }

    @Override
    public int[] getTileResolution() {
        return new int[]{this.TILE_WIDTH, this.TILE_HEIGHT};
    }


}
