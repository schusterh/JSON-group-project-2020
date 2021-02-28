package map;

import types.Tile;

import java.util.Random;

public class MapGenerator {

    private final int mapWidth;
    private final int mapHeight;
    private final double featureSize;

    public long seed;

    /**
     * Instantiates a new map generator instance
     * @param width width of map
     * @param height height of map
     * @param seed random seed for generation
     */
    public MapGenerator(int width, int height, long seed) {
        this.mapWidth = width;
        this.mapHeight = height;
        this.featureSize = 20f;
        this.seed = seed;
    }

    /**
     * Generates a new heightmap for further use
     * @return int[][] heightmap [-1;3]
     */
    public int[][] generateHeightmap() {

        // Uses OpenSimplexNoise-Implementation (NOT OURS! Credit in java file for this class)
        OpenSimplexNoise noise = new OpenSimplexNoise(seed);

        int[][] mapHeights = new int[this.mapWidth][this.mapHeight];

        /*
         * Evaluates generated image and parts grey values into discrete steps for rough heightmap generation
         */
        for (int y = 0; y < this.mapHeight; y++)
        {
            for (int x = 0; x < this.mapWidth; x++)
            {
                double value = noise.eval(x / this.featureSize, y / this.featureSize, 0.0);
                int height;

                if (value <= -0.4) {
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

    /**
     * Converts the generated heightmap into a tilemap for rendering
     * @param heightMap int[][] heightmap from generateHeightmap
     * @return Tile[][] Map of tiles
     */
    public Tile[][] convertHeightMapToTileMap(int[][] heightMap) {
        Random random = new Random(seed);
        Tile[][] tileMap = new Tile[heightMap.length][heightMap[0].length];

        for (int x = 0; x < heightMap.length; x++) {
            for (int y = 0; y < heightMap[0].length; y++) {
                boolean isLand = heightMap[x][y] >= 0;

                Tile tile = new Tile(heightMap[x][y], isLand ? 1 : 0);

                /*
                 * Adds random grass
                 */
                if (isLand) {
                    double nature = random.nextDouble();
                    if (nature < 0.1) {
                        if (nature < 0.020) {
                            tile.tileIndex = 3;
                        }
                        else tile.tileIndex = 3;
                    }

                    else if (nature < 0.2) tile.tileIndex = 2;
                    else tile.tileIndex = 1;
                }
                tileMap[x][y] = tile;
            }
        }

        return tileMap;
    }
}
