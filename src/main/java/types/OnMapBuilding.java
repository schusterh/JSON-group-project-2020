package types;

import javafx.scene.image.Image;
import modell.Building;
import modell.Factory;
import ui.StandardImage;

public class OnMapBuilding {

    public Building model;
    public int startX;
    public int startY;
    public int width;
    public int depth;
    public int height;
    public Image graphic;
    public StandardImage std = new StandardImage("/buildings/error_tile.png");
    public StandardImage std_factory = new StandardImage("/buildings/error_factory.png");

    public OnMapBuilding(Building building, int startX, int startY, int height) {
        this.model = building;
        this.startX = startX;
        this.startY = startY;
        this.width = building.getWidth();
        this.depth = building.getDepth();
        this.height = height;
        System.out.println("Building name: " + building.getName());

        if (building.getClass() == Factory.class) {
            this.graphic = std_factory.setImage("/buildings/" + building.getName() + ".png");
        }
        else {
            this.graphic = std.setImage("/buildings/" + building.getName() + ".png");
        }

    }

    public int getStartX() { return startX; }

    public int getStartY() { return startY; }

    public void replaceModel(Building newModel) {
        this.model = newModel;
        this.graphic = new Image(getClass().getResourceAsStream("/buildings/" + newModel.getName() + ".png"));
    }
}
