package ui.tiles;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import ui.RenderLayer;

public class TileRenderLayer implements RenderLayer {

    int[][] renderMap;
    TileSet tileSet;

    private final int mapWidth;
    private final int mapHeight;

    public TileRenderLayer(int mapWidth, int mapHeight, int[][] tileMap, TileSet tileSet) {
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
                double posX = (x - y) * (double) (100 / 2) + offsetX;
                double posY = (x + y) * (double) (50 / 2) + offsetY;
                gc.drawImage(tileSet.getTile(this.renderMap[x][y]), posX, posY, tileResolution[0], tileResolution[1]);
            }
        }
    }
}
