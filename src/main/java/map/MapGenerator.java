package map;

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
                int height;

                if (value <= -0.2) {
                    height = -1;
                }
                else if (value < 0.2) {
                    height = 1;
                }
                else if (value < 0.6){
                    height = 2;
                }
                else {
                    height = 3;
                }

                mapHeights[x][y] = height;
            }
        }
        return mapHeights;
    }

    public int[][] convertHeightMapToTileMap(int[][] heightMap) {
        Random random = new Random(seed);
        int[][] tileMap = new int[heightMap.length][heightMap[0].length];

        for (int x = 0; x < heightMap.length; x++) {
            for (int y = 0; y < heightMap[0].length; y++) {
                boolean isLand = heightMap[x][y] > 0;

                tileMap[x][y] = isLand ? 1 : 0;

                if (isLand) tileMap[x][y] = random.nextDouble() < 0.1 ? 2 : 1;
            }
        }

        return tileMap;
    };
}
