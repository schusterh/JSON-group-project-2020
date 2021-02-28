package modell;

import map.MapGenerator;
import types.Coordinate;
import types.Tile;
import ui.tiles.LandscapeLayer;

import java.util.List;
import java.util.Random;

/**
 * This class represents the map of the game.
 */
public class Map {

    private String mapgen;
    private String gamemode;
    private int width;
    private int depth;
    private Tile[][] landscapeMap;

    /**
     * Instantiates a new Map.
     *
     * @param mapgen   the mapgen
     * @param gamemode the gamemode
     * @param width    the width
     * @param depth    the depth
     */
    public Map(String mapgen, String gamemode, int width, int depth) {
        this.mapgen = mapgen;
        this.gamemode = gamemode;
        this.width = width;
        this.depth = depth;

        generateMap();
    }

    private void generateMap() {
        Random r = new Random(System.currentTimeMillis());
        MapGenerator mapGen = new MapGenerator(this.width, depth, r.nextLong());
        int[][] heightMap = mapGen.generateHeightmap();

        this.landscapeMap = mapGen.convertHeightMapToTileMap(heightMap);

    }

    /**
     * Gets depth.
     *
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Gets width.
     *
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets gamemode.
     *
     * @return the gamemode
     */
    public String getGamemode() {
        return gamemode;
    }

    /**
     * Gets mapgen.
     *
     * @return the mapgen
     */
    public String getMapgen() {
        return mapgen;
    }

    /**
     * Increase height of selected tiles.
     *
     * @param selectedTiles the selected tiles
     */
    public void increaseHeightOfSelectedTiles(List<Coordinate> selectedTiles) {
        for (Coordinate tileCoord : selectedTiles) {
            this.changeRelativeHeightOfTile(tileCoord.x, tileCoord.y, 1);
        }
    }

    /**
     * Decrease height of selected tiles.
     *
     * @param selectedTiles the selected tiles
     */
    public void decreaseHeightOfSelectedTiles(List<Coordinate> selectedTiles) {
        for (Coordinate tileCoord : selectedTiles) {
            this.changeRelativeHeightOfTile(tileCoord.x, tileCoord.y, -1);
        }
    }

    private void changeRelativeHeightOfTile(int x, int y, int change) {
        int oldHeight = this.landscapeMap[x][y].height;
        int newHeight = oldHeight + change;
        if (newHeight >= -1 && newHeight <= 6) {
            if (newHeight == 0 && oldHeight == -1) {
                this.landscapeMap[x][y].tileIndex = 1;
            } else if (newHeight == -1) {
                this.landscapeMap[x][y].tileIndex = 0;
            }
            this.landscapeMap[x][y].height = newHeight;
            int[] neighbors = new int[]{-1, 0, 1};
            for (int neighborX : neighbors) {
                for (int neighborY : neighbors) {
                    if (!(neighborX == 0 && neighborY == 0) && !(Math.abs(neighborX) == 1 && Math.abs(neighborY) == 1)) {
                        if (x + neighborX >= 0 && x + neighborX < this.width && y + neighborY >= 0 && y + neighborY < this.depth) {
                            int neighborHeight = this.landscapeMap[x + neighborX][y + neighborY].height;
                            if (newHeight - neighborHeight > 1 || newHeight - neighborHeight < -1) {
                                changeRelativeHeightOfTile(x + neighborX, y + neighborY, change);

                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Plain ground.
     *
     * @param startX     the start x
     * @param startY     the start y
     * @param width      the width
     * @param depth      the depth
     * @param height     the height
     * @param isConcrete the is concrete
     */
    public void plainGround(int startX, int startY, int width, int depth, int height, boolean isConcrete) {
        for (int x = startX; x < (startX + width); x++) {
            for (int y = startY; y < (startY + depth); y++) {
                //if (isConcrete) this.landscapeMap[x][y].tileIndex = 4;
                this.landscapeMap[x][y].height = height;
            }
        }
    }

    /**
     * Gets tile.
     *
     * @param x the x
     * @param y the y
     * @return the tile
     */
    public Tile getTile(int x, int y) {
        return landscapeMap[x][y];
    }

    /**
     * Get tile map tile [ ] [ ].
     *
     * @return the tile [ ] [ ]
     */
    public Tile[][] getTileMap() { return landscapeMap; }
}
