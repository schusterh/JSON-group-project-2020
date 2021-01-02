package model;

import mapgenerator.MapGenerator;

public class GameModel {

    MapGenerator generator;
    int[][] mapHeights;

    public GameModel(int mapWidth, int mapHeight) {
        this.generator = new MapGenerator(mapWidth, mapHeight);
        this.mapHeights = this.generator.generateHeightmap();
    }

    public int[][] getMapHeights() {
        return mapHeights;
    }
}
