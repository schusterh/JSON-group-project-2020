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

    /**
     * Instantiates a new OnMapBuilding for drawing on the map
     * @param building corresponding base model
     * @param startX base X position in tiles
     * @param startY base Y position in tiles
     * @param height height
     */
    public OnMapBuilding(Building building, int startX, int startY, int height) {
        this.model = building;
        this.startX = startX;
        this.startY = startY;
        this.width = building.getWidth();
        this.depth = building.getDepth();
        this.height = height;

        if (building.getClass() == Factory.class) {
            this.graphic = std_factory.setImage("/buildings/" + building.getName() + ".png");
        }
        else {
            this.graphic = std.setImage("/buildings/" + building.getName() + ".png");
        }

    }

    /**
     * Gets base X position
     * @return x position in tiles
     */
    public int getStartX() { return startX; }

    /**
     * Gets base Y position
     * @return y position in tiles
     */
    public int getStartY() { return startY; }

    /**
     * Replaces a model for a building (for tile combinations)
     * @param newModel new model to be set
     */
    public void replaceModel(Building newModel) {
        this.model = newModel;
        this.graphic = new Image(getClass().getResourceAsStream("/buildings/" + newModel.getName() + ".png"));
    }
}
