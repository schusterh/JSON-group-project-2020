package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Arrays;

public class MapView {

    int[][] mapHeights;
    Tile[][] tileMap;

    Image grassH1;
    Image grassH1_Up_NW;
    Image grassH1_Up_SE;
    Image grassH1_Up_SW;
    Image grassH1_Up_NE;

    Image grassH1_Up_N;
    Image grassH1_Up_S;
    Image grassH1_Up_E;
    Image grassH1_Up_W;

    Image grassH1_Up_NN;
    Image grassH1_Up_SS;
    Image grassH1_Up_EE;
    Image grassH1_Up_WW;


    Image grassH2;
    Image grassH3;
    Image water;

    public MapView(int[][] mapHeights) {
        this.mapHeights = mapHeights;
        this.tileMap = new Tile[this.mapHeights.length][this.mapHeights[0].length];

        this.grassH1 = new Image(getClass().getResourceAsStream("/tiles/Grass_H1.png"));
        this.grassH1_Up_NW = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_NE.png"));
        this.grassH1_Up_SE = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_NW.png"));
        this.grassH1_Up_SW = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_SW.png"));
        this.grassH1_Up_NE = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_SE.png"));

        this.grassH1_Up_N = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_N.png"));
        this.grassH1_Up_S = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_S.png"));
        this.grassH1_Up_E = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_E.png"));
        this.grassH1_Up_W = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_W.png"));

        this.grassH1_Up_NN = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_NN.png"));
        this.grassH1_Up_SS = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_SS.png"));
        this.grassH1_Up_EE = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_EE.png"));
        this.grassH1_Up_WW = new Image(getClass().getResourceAsStream("/tiles/Grass_H1_Up_WW.png"));


        this.grassH2 = new Image(getClass().getResourceAsStream("/tiles/Grass_H2.png"));
        this.grassH3 = new Image(getClass().getResourceAsStream("/tiles/Grass_H3.png"));
        this.water = new Image(getClass().getResourceAsStream("/tiles/water.png"));
        this.convertHeightmapToTiles();
    }

    public int[] evaluateHeightDifferenceBetweenTiles(int x, int y) {
        int[] result = {0, 0, 0, 0, 0, 0, 0, 0};

        if (x > 0 && y > 0) {
            result[0] = this.mapHeights[x-1][y] - this.mapHeights[x][y];
            result[1] = this.mapHeights[x][y-1] - this.mapHeights[x][y];
        }

        if (x < this.mapHeights.length-1 && y < this.mapHeights[0].length-1) {
            result[2] = this.mapHeights[x+1][y] - this.mapHeights[x][y];
            result[3] = this.mapHeights[x][y+1] - this.mapHeights[x][y];
        }

        if (x > 0 && y > 0 && x < this.mapHeights.length-1 && y < this.mapHeights[0].length-1) {
            result[4] = this.mapHeights[x-1][y-1] - this.mapHeights[x][y];
            result[5] = this.mapHeights[x+1][y-1] - this.mapHeights[x][y];
            result[6] = this.mapHeights[x+1][y+1] - this.mapHeights[x][y];
            result[7] = this.mapHeights[x-1][y+1] - this.mapHeights[x][y];
        }



        return result;
    }

    public void convertHeightmapToTiles() {
        for (int x = 0; x < this.mapHeights.length; x++) {
            for (int y = 0; y < this.mapHeights[0].length; y++) {
                int[] differences = this.evaluateHeightDifferenceBetweenTiles(x, y);
                Tile tile = new Tile(x, y, this.water);
                switch (this.mapHeights[x][y]) {
                    case -1:
                        tile = new Tile(x, y, this.water);
                        break;
                    case 1:
                        if (differences[0] == 0 && differences[1] == 1 && differences[2] == 0 && differences[3] == 0) tile = new Tile(x, y, this.grassH1_Up_NW);
                        else if (differences[0] == 1 && differences[1] == 0 && differences[2] == 0 && differences[3] == 0) tile = new Tile(x, y, this.grassH1_Up_SE);
                        else if (differences[0] == 0 && differences[1] == 0 && differences[2] == 1 && differences[3] == 0) tile = new Tile(x, y, this.grassH1_Up_SW);
                        else if (differences[0] == 0 && differences[1] == 0 && differences[2] == 0 && differences[3] == 1) tile = new Tile(x, y, this.grassH1_Up_NE);

                        else if (differences[0] == 1 && differences[1] == 1 && differences[2] == 0 && differences[3] == 0) tile = new Tile(x, y, this.grassH1_Up_N);
                        else if (differences[0] == 0 && differences[1] == 1 && differences[2] == 1 && differences[3] == 0) tile = new Tile(x, y, this.grassH1_Up_W);
                        else if (differences[0] == 0 && differences[1] == 0 && differences[2] == 1 && differences[3] == 1) tile = new Tile(x, y, this.grassH1_Up_S);
                        else if (differences[0] == 1 && differences[1] == 0 && differences[2] == 0 && differences[3] == 1) tile = new Tile(x, y, this.grassH1_Up_E);

                        else if (differences[4] == 1 && differences[5] == 0 && differences[6] == 0 && differences[7] == 0) tile = new Tile(x, y, this.grassH1_Up_NN);
                        else if (differences[4] == 0 && differences[5] == 0 && differences[6] == 1 && differences[7] == 0) tile = new Tile(x, y, this.grassH1_Up_SS);
                        else if (differences[4] == 0 && differences[5] == 1 && differences[6] == 0 && differences[7] == 0) tile = new Tile(x, y, this.grassH1_Up_WW);
                        else if (differences[4] == 0 && differences[5] == 0 && differences[6] == 0 && differences[7] == 1) tile = new Tile(x, y, this.grassH1_Up_EE);



                        else tile = new Tile(x, y, this.grassH1);
                        break;
                    case 2:
                        tile = new Tile(x, y, this.grassH2);
                        break;
                    case 3:
                        tile = new Tile(x, y, this.grassH3);
                        break;
                }
                tileMap[x][y] = tile;
            }
        }
    }

    public void draw(GraphicsContext gc, double panX, double panY) {

        for (int x = 0; x < this.mapHeights.length; x++) {
            for (int y = 0; y < this.mapHeights[0].length; y++) {
                double posX = (x - y) * (double) (100 / 2) + panX;
                double posY = (x + y) * (double) (50 / 2) + panY;
                //System.out.println(tileMap[x][y].graphicImage);
                tileMap[x][y].draw(gc, posX, posY);
            }
        }
        System.out.println("Drawed frame!");
    }
}
