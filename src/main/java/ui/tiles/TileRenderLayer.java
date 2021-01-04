package ui.tiles;

import javafx.scene.canvas.GraphicsContext;
import types.Tile;
import ui.GameLoop;
import ui.RenderLayer;
import ui.WSGameLoop;

public class TileRenderLayer implements RenderLayer {

    Tile[][] renderMap;
    DefaultTileSet tileSet;

    private final int mapWidth;
    private final int mapHeight;

    private int tileOffsetY = 13;

    boolean isInteractable = false;

    WSGameLoop gameLoop;

    public TileRenderLayer(int mapWidth, int mapHeight, Tile[][] tileMap, DefaultTileSet tileSet) {
        this.mapWidth = mapWidth;
        this.mapHeight= mapHeight;
        this.renderMap = tileMap;
        this.tileSet = tileSet;
    }

    public void setOffsetFromCenterY(int offsetY) {
        this.tileOffsetY = offsetY;
    }

    public void makeInteractable(WSGameLoop loop) {
        this.isInteractable = true;
        this.gameLoop = loop;
    }

    private int isLeftOfVector(double xp, double yp, double x1, double y1, double x2, double y2) {
        return (yp - y1) * (x2 - x1) - (xp -x1) * (y2 - y1) > 0 ? 1 : 0;
    }

    private boolean isInsidePolygon(double xp, double yp, int polyPoiNum, double[] polyX, double[] polyY) {
        int result = 0;

        for (int i = 0; i < polyPoiNum; i++) {
            result += isLeftOfVector(xp, yp, polyX[i], polyY[i], (i+1 < polyPoiNum) ? polyX[i+1] : polyX[0], (i+1 < polyPoiNum) ? polyY[i+1] : polyY[0]);
        }

        return result == polyPoiNum;
    }

    @Override
    public void draw(GraphicsContext gc, int offsetX, int offsetY) {
        int[] tileResolution = this.tileSet.getTileResolution();
        int[] mousePosition = this.gameLoop.getMousePosition();

        for (int x = 0; x < this.mapWidth; x++) {
            for (int y = 0; y < this.mapHeight; y++) {
                double heightOffset = this.renderMap[x][y].height * this.tileOffsetY;
                double posX = (x - y) * (double) (tileResolution[0] / 2) + offsetX;
                double posY = (x + y) * (double) (tileResolution[1] / 4) + offsetY - heightOffset;
                gc.drawImage(tileSet.getTile(this.renderMap[x][y].tileIndex), posX, posY, tileResolution[0], tileResolution[1]);

                if (isInteractable) {
                    double[] polyPoiX = new double[]{
                            posX,
                            posX + (double) tileResolution[0] / 2,
                            posX + (double) tileResolution[0],
                            posX + (double) tileResolution[0] / 2};
                    double[] polyPoiY = new double[]{
                            posY + (double) tileResolution[1] * (1f/4f) + this.tileOffsetY,
                            posY + (double) tileResolution[1] * (0f/4f) + this.tileOffsetY,
                            posY + (double) tileResolution[1] * (1f/4f) + this.tileOffsetY,
                            posY + (double) tileResolution[1] * (1f/2f) + this.tileOffsetY};
                    if (this.isInsidePolygon(mousePosition[0], mousePosition[1], 4, polyPoiX, polyPoiY)) {
                        gc.strokePolygon(polyPoiX, polyPoiY, 4);
                    }
                }
            }
        }
    }
}
