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

    /**
     * Landscape Layer Constructor
     * @param model Game Instance
     * @param controller Controller Instance
     * @param tileDimension Dimension of ground tile graphic
     * @param tileOffsetY Height of tile border for height displacement calculation
     */
    public LandscapeLayer(Game model, GameController controller, int tileDimension, int tileOffsetY) {
        this.model = model;
        this.controller = controller;
        this.mapWidth = model.getMap().getWidth();
        this.mapHeight= model.getMap().getDepth();
        this.tileOffsetY = tileOffsetY;
        this.tileSet = new DefaultTileSet(this.TILE_SET_URI, this.TILE_SET_COLS, this.TILE_SET_ROWS, tileDimension, tileDimension);
    }

    /**
     * Sets game loop and (counterproductively) sets map to non-interactive to allow building and terrain mode separately
     * @param loop GameLoop Instance
     */
    public void makeInteractable(GameLoop loop) {
        this.isInteractable = false;
        this.gameLoop = loop;
    }

    /**
     * Sets interactivity status (meaning if terrain can be modified with mouse clicks or not)
     * @param value interactive state
     */
    public void setInteractive(boolean value) {
        this.isInteractable = value;
    }

    /**
     * Gets wether a mouse cursor is left or right of a hypothetical line
     *
     * Belongs to quick and dirty method of finding out wether the mouse cursor is inside a conves polygon (tile boundaries)
     * @param xp X Position of mouse cursor
     * @param yp Y Position of mouse cursor
     * @param x1 X Position 1 of line
     * @param y1 Y Position 1 of line
     * @param x2 X Position 2 of line
     * @param y2 Y Position 2 of line
     * @return true if point is left of line
     */
    private int isLeftOfVector(double xp, double yp, double x1, double y1, double x2, double y2) {
        return (yp - y1) * (x2 - x1) - (xp -x1) * (y2 - y1) > 0 ? 1 : 0;
    }

    /**
     * Gets all tiles that are within a specified radius in circle shape
     * @param centerX Center X tile position
     * @param centerY Center Y tile position
     * @param radius Radius in tiles
     * @return List of all tiles inside radius
     */
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

    /**
     * Quick and dirty method of finding out wether a point is inside a convex polygon (tile boundaries) or not by
     * finding out wether the point is on the left side of all lines (works with all convex polygons with sides n > 3)
     * @param xp X Position of mouse cursor
     * @param yp Y Position of mouse cursor
     * @param polyPoiNum Number of sides on polygon
     * @param polyX List of X coordinates for lines
     * @param polyY List of Y coordinates for lines
     * @return true if point is inside polygon, false if not
     */
    private boolean isInsidePolygon(double xp, double yp, int polyPoiNum, double[] polyX, double[] polyY) {
        int result = 0;

        for (int i = 0; i < polyPoiNum; i++) {
            result += isLeftOfVector(xp, yp, polyX[i], polyY[i], (i+1 < polyPoiNum) ? polyX[i+1] : polyX[0], (i+1 < polyPoiNum) ? polyY[i+1] : polyY[0]);
        }

        return result == polyPoiNum;
    }

    /**
     * Sets selection radius in number of tiles
     * @param radius radius in number of tiles
     */
    public void setRadius(int radius) {
        this.selectionRadius = radius;
    }

    /**
     * Paints all tiles in selection radius with border and overlay
     * @param gc GraphicsContext to be drawn on
     * @param x X Position of Tile to be painted
     * @param y Y Position of Tile to be painted
     * @param offsetX current Offset X Position of Map
     * @param offsetY current Offset Y Position of Map
     * @param tileResolution Resolution of base tile
     * @param zoomFactor current zoom factor
     */
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

    /**
     * Draws single frame step to canvas
     * @param gc GraphicsContext to be drawn on
     * @param offsetX current Offset X Position of Map
     * @param offsetY current Offset Y Position of Map
     * @param zoomFactor current Zoom Factor
     */
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

    /**
     * Returns selected Tiles
     * @return List of selected tiles
     */
    public ArrayList<Coordinate> getSelectedTiles() {
        return selectedTiles;
    }

    /**
     * Resets List of all tiles and clears it
     */
    public void clearSelectedTiles() {
        this.selectedTiles.clear();
    }
}
