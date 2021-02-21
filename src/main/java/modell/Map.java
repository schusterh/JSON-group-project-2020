package modell;

import map.MapGenerator;
import types.Tile;

import java.util.Random;

public class Map {

    private String mapgen;
    private String gamemode;
    private int width;
    private int depth;
    private Tile[][] landscapeMap;

    public Map(String mapgen, String gamemode, int width, int depth) {
        this.mapgen = mapgen;
        this.gamemode = gamemode;
        this.width = width;
        this.depth = depth;
    }

    private void generateMap() {
        Random r = new Random(System.currentTimeMillis());
        MapGenerator mapGen = new MapGenerator(this.width, depth, r.nextLong());
        int[][] heightMap = mapGen.generateHeightmap();

        this.landscapeMap = mapGen.convertHeightMapToTileMap(heightMap);
    }

    public int getDepth() {
        return depth;
    }

    public int getWidth() {
        return width;
    }

    public String getGamemode() {
        return gamemode;
    }

    public String getMapgen() {
        return mapgen;
    }
}
