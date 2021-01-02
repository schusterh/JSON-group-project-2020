package mapgenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class MapGenerator {

    private int mapWidth;
    private int mapHeight;
    private double featureSize;

    public MapGenerator(int width, int height) {
        this.mapWidth = width;
        this.mapHeight = height;
        this.featureSize = (double) width / 20;
    }

    public int[][] generateHeightmap() {

        OpenSimplexNoise noise = new OpenSimplexNoise();
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
}
