package types;

import javafx.scene.image.Image;
import modell.Building;

public class OnMapBuilding {

    public Building model;
    public int startX;
    public int startY;
    public int width;
    public int depth;
    public int height;
    public Image graphic;

    public OnMapBuilding(Building building, int startX, int startY, int height) {
        this.model = building;
        this.startX = startX;
        this.startY = startY;
        this.width = building.getWidth();
        this.depth = building.getDepth();
        this.height = height;
        System.out.println("Building name: " + building.getName());
        this.graphic = new Image(getClass().getResourceAsStream("/buildings/" + building.getName() + ".png"));
    }

    public int getStartX() { return startX; }

    public int getStartY() { return startY; }
}
