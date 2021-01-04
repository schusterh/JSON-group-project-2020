package map;

import types.Tile;

import java.awt.image.BufferedImage;
import java.util.Random;

public class MapGenerator {

    private int mapWidth;
    private int mapHeight;
    private double featureSize;

    public long seed;

    public MapGenerator(int width, int height, long seed) {
        this.mapWidth = width;
        this.mapHeight = height;
        this.featureSize = (double) width / 20;
        this.seed = seed;
    }

    public int[][] generateHeightmap() {

        OpenSimplexNoise noise = new OpenSimplexNoise(seed);
        BufferedImage image = new BufferedImage(this.mapWidth, this.mapHeight, BufferedImage.TYPE_INT_RGB);

        int[][] mapHeights = new int[this.mapWidth][this.mapHeight];

        for (int y = 0; y < this.mapHeight; y++)
        {
            for (int x = 0; x < this.mapWidth; x++)
            {
                double value = noise.eval(x / this.featureSize, y / this.featureSize, 0.0);
                int height = -1;

                if (value <= -0.2) {
                    height = -1;
                }
                else if (value < 0.2) {
                    height = 0;
                }
                else if (value < 0.6){
                    height = 1;
                }
                else {
                    height = 2;
                }

                mapHeights[x][y] = height;
            }
        }
        return mapHeights;
    }

    public Tile[][] convertHeightMapToTileMap(int[][] heightMap) {
        Random random = new Random(seed);
        Tile[][] tileMap = new Tile[heightMap.length][heightMap[0].length];

        for (int x = 0; x < heightMap.length; x++) {
            System.out.printf("[");
            for (int y = 0; y < heightMap[0].length; y++) {
                System.out.printf(heightMap[x][y] + ", ");
            }
            System.out.printf("]\n");
        }

        for (int x = 0; x < heightMap.length; x++) {
            for (int y = 0; y < heightMap[0].length; y++) {
                boolean isLand = heightMap[x][y] >= 0;

                Tile tile = new Tile(heightMap[x][y], isLand ? 1 : 0);

                if (isLand) tile.tileIndex = random.nextDouble() < 0.1 ? 2 : 1;
                tileMap[x][y] = tile;
            }
        }

        return tileMap;
    };
}
