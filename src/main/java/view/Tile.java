package view;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Tile extends Node {

    int positionX;
    int positionY;
    Image graphicImage;

    public Tile(int x, int y, Image image) {
        this.positionX = x;
        this.positionY = y;
        this.graphicImage = image;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void draw(GraphicsContext gc, double x, double y) {
        gc.drawImage( this.graphicImage, x, y, 100, 100 );
    }
}
