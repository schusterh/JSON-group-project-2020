package ui.tiles;

import javafx.scene.canvas.GraphicsContext;
import types.Tile;
import ui.RenderLayer;

public class TileRenderLayer implements RenderLayer {

    Tile[][] renderMap;
    TileSet tileSet;

    private final int mapWidth;
    private final int mapHeight;

    public TileRenderLayer(int mapWidth, int mapHeight, Tile[][] tileMap, TileSet tileSet) {
        this.mapWidth = mapWidth;
        this.mapHeight= mapHeight;
        this.renderMap = tileMap;
        this.tileSet = tileSet;
    }

    @Override
    public void draw(GraphicsContext gc, int offsetX, int offsetY) {
        int[] tileResolution = this.tileSet.getTileResolution();

        for (int x = 0; x < this.mapWidth; x++) {
            for (int y = 0; y < this.mapHeight; y++) {
                double heightOffset = this.renderMap[x][y].height * 12;
                double posX = (x - y) * (double) (100 / 2) + offsetX;
                double posY = (x + y) * (double) (50 / 2) + offsetY - heightOffset;
                gc.drawImage(tileSet.getTile(this.renderMap[x][y].tileIndex), posX, posY, tileResolution[0], tileResolution[1]);
            }
        }
    }
}
