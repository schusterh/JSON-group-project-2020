package ui.tiles;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import types.Tile;
import ui.GameLoop;
import ui.RenderLayer;

import java.util.ArrayList;

public class TileRenderLayer implements RenderLayer {

    private class Coordinate {
        int x;
        int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    Tile[][] renderMap;
    DefaultTileSet tileSet;

    private final int mapWidth;
    private final int mapHeight;

    private int tileOffsetY = 1000;

    boolean isInteractable = false;

    int selectionRadius = 0;

    ArrayList<Coordinate> selectedTiles = new ArrayList<>();

    GameLoop gameLoop;

    public TileRenderLayer(int mapWidth, int mapHeight, Tile[][] tileMap, DefaultTileSet tileSet) {
        this.mapWidth = mapWidth;
        this.mapHeight= mapHeight;
        this.renderMap = tileMap;
        this.tileSet = tileSet;
    }

    public void setOffsetFromCenterY(int offsetY) {
        this.tileOffsetY = offsetY;
    }

    public void makeInteractable(GameLoop loop) {
        this.isInteractable = true;
        this.gameLoop = loop;
    }

    private int isLeftOfVector(double xp, double yp, double x1, double y1, double x2, double y2) {
        return (yp - y1) * (x2 - x1) - (xp -x1) * (y2 - y1) > 0 ? 1 : 0;
    }

    private void changeRelativeHeightOfTile(int x, int y, int change) {
        int oldHeight = this.renderMap[x][y].height;
        int newHeight = oldHeight + change;
        if (newHeight >= -1 && newHeight <= 6) {
            if (newHeight == 0 && oldHeight == -1) {
                this.renderMap[x][y].tileIndex = 1;
            } else if (newHeight == -1) {
                this.renderMap[x][y].tileIndex = 0;
            }
            this.renderMap[x][y].height = newHeight;
            int[] neighbors = new int[]{-1, 0, 1};
            for (int neighborX : neighbors) {
                for (int neighborY : neighbors) {
                    if (!(neighborX == 0 && neighborY == 0) && !(Math.abs(neighborX) == 1 && Math.abs(neighborY) == 1)) {
                        int neighborHeight = this.renderMap[x + neighborX][y + neighborY].height;
                        if (newHeight - neighborHeight > 1 || newHeight - neighborHeight < -1) {
                            changeRelativeHeightOfTile(x + neighborX, y + neighborY, change);
                        }
                    }
                }
            }
        }
    }

    private ArrayList<Coordinate> getTilesInCircle(int centerX, int centerY, int radius) {
        ArrayList<Coordinate> result = new ArrayList<>();

        for (int y = -radius; y <= radius; y++)
            for (int x = -radius; x <= radius; x++)
                if ((x * x) + (y * y) <= (radius * radius))
                    result.add(new Coordinate(centerX + x, centerY + y));

        return result;
    }

    public void increaseHeightOfSelectedTiles() {
        for (Coordinate tileCoord : this.selectedTiles) {
            this.changeRelativeHeightOfTile(tileCoord.x, tileCoord.y, 1);
        }
    }

    public void decreaseHeightOfSelectedTiles() {
        for (Coordinate tileCoord : this.selectedTiles) {
            this.changeRelativeHeightOfTile(tileCoord.x, tileCoord.y, -1);
        }
    }

    private boolean isInsidePolygon(double xp, double yp, int polyPoiNum, double[] polyX, double[] polyY) {
        int result = 0;

        for (int i = 0; i < polyPoiNum; i++) {
            result += isLeftOfVector(xp, yp, polyX[i], polyY[i], (i+1 < polyPoiNum) ? polyX[i+1] : polyX[0], (i+1 < polyPoiNum) ? polyY[i+1] : polyY[0]);
        }

        return result == polyPoiNum;
    }

    public void setRadius(int radius) {
        this.selectionRadius = radius;
    }

    private void paintTileSelected(GraphicsContext gc, int x, int y, int offsetX, int offsetY, int[] tileResolution, double zoomFactor) {

        double heightOffset = this.renderMap[x][y].height * this.tileOffsetY;
        double posX = ((x - y) * (double) (tileResolution[0] / 2) + offsetX) * zoomFactor;
        double posY = ((x + y) * (double) (tileResolution[1] / 4) + offsetY - heightOffset) * zoomFactor + 10;

        double[] polyPoiX = new double[]{
                posX,
                (posX + (double) tileResolution[0] / 2 * zoomFactor),
                (posX + (double) tileResolution[0] * zoomFactor),
                (posX + (double) tileResolution[0] / 2 * zoomFactor)};
        double[] polyPoiY = new double[]{
                posY + ((double) tileResolution[1] * (1f / 4f) + this.tileOffsetY) * zoomFactor,
                posY + ((double) tileResolution[1] * (0f / 4f) + this.tileOffsetY) * zoomFactor,
                posY + ((double) tileResolution[1] * (1f / 4f) + this.tileOffsetY) * zoomFactor,
                posY + ((double) tileResolution[1] * (1f / 2f) + this.tileOffsetY) * zoomFactor};

        gc.setFill(new Color(0.41, 0.41, 0.41, 0.3));
        gc.fillPolygon(polyPoiX, polyPoiY, 4);
        gc.strokePolygon(polyPoiX, polyPoiY, 4);
    }

    @Override
    public void draw(GraphicsContext gc, int offsetX, int offsetY, double zoomFactor) {
        int[] tileResolution = this.tileSet.getTileResolution();
        int[] mousePosition = this.gameLoop.getMousePosition();

        for (int x = 0; x < this.mapWidth; x++) {
            for (int y = 0; y < this.mapHeight; y++) {
                double heightOffset = this.renderMap[x][y].height * this.tileOffsetY;
                double posX = ( (x - y) * (double) (tileResolution[0] / 2) + offsetX ) * zoomFactor;
                double posY = ( (x + y) * (double) (tileResolution[1] / 4) + offsetY - heightOffset) * zoomFactor;

                if (posX > -this.tileSet.TILE_WIDTH*2 && posX < gc.getCanvas().getWidth() && posY > -this.tileSet.TILE_HEIGHT*2 && posY < gc.getCanvas().getHeight()) {
                    gc.drawImage(tileSet.getTile(this.renderMap[x][y].tileIndex), posX, posY, tileResolution[0] * zoomFactor, tileResolution[1] * zoomFactor);

                    if (isInteractable) {
                        double[] polyPoiX = new double[]{
                                posX,
                                ( posX + (double) tileResolution[0] / 2 * zoomFactor) ,
                                ( posX + (double) tileResolution[0] * zoomFactor) ,
                                ( posX + (double) tileResolution[0] / 2 * zoomFactor) };
                        double[] polyPoiY = new double[]{
                                posY + ( (double) tileResolution[1] * (1f/4f) + this.tileOffsetY) * zoomFactor,
                                posY + ( (double) tileResolution[1] * (0f/4f) + this.tileOffsetY) * zoomFactor,
                                posY + ( (double) tileResolution[1] * (1f/4f) + this.tileOffsetY) * zoomFactor,
                                posY + ( (double) tileResolution[1] * (1f/2f) + this.tileOffsetY) * zoomFactor};
                        if (this.isInsidePolygon(mousePosition[0], mousePosition[1], 4, polyPoiX, polyPoiY)) {
                            this.selectedTiles = this.getTilesInCircle(x, y, this.selectionRadius);
                        }
                    }
                }
            }
        }

        for (Coordinate tileCoord : this.selectedTiles) {
            this.paintTileSelected(gc, tileCoord.x, tileCoord.y, offsetX, offsetY, tileResolution, zoomFactor);
        }
    }
}
