package ui.tiles;

import Controller.GameController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import modell.Game;
import modell.Map;
import types.Coordinate;
import types.CoordinateConverter;
import types.Tile;
import ui.GameLoop;
import ui.RenderLayer;

import java.util.ArrayList;



public class LandscapeLayer implements RenderLayer {

    final String TILE_SET_URI = "tilesets/finalTiles.png";

    final int TILE_SET_COLS = 5;
    final int TILE_SET_ROWS = 1;


    Game model;
    GameController controller;
    DefaultTileSet tileSet;

    private final int mapWidth;
    private final int mapHeight;

    private int tileOffsetY;

    boolean isInteractable = false;

    int selectionRadius = 0;

    ArrayList<Coordinate> selectedTiles = new ArrayList<>();

    GameLoop gameLoop;

    public LandscapeLayer(Game model, GameController controller, int tileDimension, int tileOffsetY) {
        this.model = model;
        this.controller = controller;
        this.mapWidth = model.getMap().getWidth();
        this.mapHeight= model.getMap().getDepth();
        this.tileOffsetY = tileOffsetY;
        this.tileSet = new DefaultTileSet(this.TILE_SET_URI, this.TILE_SET_COLS, this.TILE_SET_ROWS, tileDimension, tileDimension);
    }

    public void setOffsetFromCenterY(int offsetY) {
        this.tileOffsetY = offsetY;
    }

    public void makeInteractable(GameLoop loop) {
        this.isInteractable = false;
        this.gameLoop = loop;
    }

    public void setInteractive(boolean value) {
        this.isInteractable = value;
    }

    private int isLeftOfVector(double xp, double yp, double x1, double y1, double x2, double y2) {
        return (yp - y1) * (x2 - x1) - (xp -x1) * (y2 - y1) > 0 ? 1 : 0;
    }

    private ArrayList<Coordinate> getTilesInCircle(int centerX, int centerY, int radius) {
        ArrayList<Coordinate> result = new ArrayList<>();

        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                if ((x * x) + (y * y) <= (radius * radius)) {
                    if (centerX + x >= 0 && centerX + x < this.mapWidth && centerY + y >= 0 && centerY + y < this.mapHeight)
                        result.add(new Coordinate(centerX + x, centerY + y));
                }
            }
        }
        return result;
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

        double heightOffset = this.model.getMap().getTile(x, y).height * this.tileOffsetY;
        double posX = ((x + y) * (double) (tileResolution[0] / 2) + offsetX) * zoomFactor;
        double posY = ((x - y) * (double) (tileResolution[1] / 4) + offsetY - heightOffset) * zoomFactor + 10;

        double[][] polygons = CoordinateConverter.createPolygonFromPoint(posX, posY, tileResolution[0], tileOffsetY, zoomFactor);

        gc.setFill(new Color(0.41, 0.41, 0.41, 0.1));
        gc.fillPolygon(polygons[0], polygons[1], 4);
        gc.strokePolygon(polygons[0], polygons[1], 4);
        gc.setFill(new Color(1, 1, 1, 1));
        gc.fillText("[x: " + x + ", y: " + y + "]", posX + (double)tileResolution[0]/4, posY + (double) tileResolution[1]/2);
    }

    @Override
    public void draw(GraphicsContext gc, int offsetX, int offsetY, double zoomFactor) {
        int[] tileResolution = this.tileSet.getTileResolution();
        int[] mousePosition = this.gameLoop.getMousePosition();

        for (int y = this.mapHeight - 1; y > 0; y--) {
            for (int x = 0; x < this.mapWidth; x++) {
                double heightOffset = this.model.getMap().getTile(x, y).height * this.tileOffsetY;
                double posX = ((x + y) * (double) (tileResolution[0] / 2) + offsetX) * zoomFactor;
                double posY = ((x - y) * (double) (tileResolution[1] / 4) + offsetY - heightOffset) * zoomFactor;

                if (posX > -this.tileSet.TILE_WIDTH * 2 && posX < gc.getCanvas().getWidth() && posY > -this.tileSet.TILE_HEIGHT * 2 && posY < gc.getCanvas().getHeight()) {
                    gc.drawImage(tileSet.getTile(this.model.getMap().getTile(x, y).tileIndex), posX, posY, tileResolution[0] * zoomFactor, tileResolution[1] * zoomFactor);

                    double[][] polygons = CoordinateConverter.createPolygonFromPoint(posX, posY, tileResolution[0], tileOffsetY, zoomFactor);

                    if (this.isInsidePolygon(mousePosition[0], mousePosition[1], 4, polygons[0], polygons[1])) {
                        this.controller.setCurrentMouseTileIndex(new int[]{x, y});
                        if (isInteractable) {
                            this.selectedTiles = this.getTilesInCircle(x, y, this.selectionRadius);
                        }
                    }
                }
            }
            for (Coordinate tileCoord : this.selectedTiles) {
                this.paintTileSelected(gc, tileCoord.x, tileCoord.y, offsetX, offsetY, tileResolution, zoomFactor);
            }
        }
    }

    public ArrayList<Coordinate> getSelectedTiles() {
        return selectedTiles;
    }

    public void clearSelectedTiles() {
        this.selectedTiles.clear();
    }
}
