package ui.tiles;

import Controller.GameController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import modell.Building;
import modell.Game;
import types.OnMapBuilding;
import ui.RenderLayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BuildingLayer implements RenderLayer {

    private int tileDimension;
    private int tileHeightOffset;

    Game model;
    GameController controller;

    List<BuildingGraphic> buildings = new ArrayList<>();

    private class BuildingGraphic {
        public int startX;
        public int startY;
        int width;
        int depth;
        Image graphic;

        public BuildingGraphic(int startX, int startY, int width, int depth, String name) {
            this.startX = startX;
            this.startY = startY;
            this.width = width;
            this.depth = depth;
            this.graphic = new Image(getClass().getResourceAsStream("/buildings/" + name + ".png"));
        }

        public int getStartX() {
            return startX;
        }

        public int getStartY() {
            return startY;
        }
    }

    public BuildingLayer (Game model, GameController controller, int tileDimension, int tileHeightOffset) {
        this.tileDimension = tileDimension;
        this.model = model;
        this.controller = controller;
        this.tileHeightOffset = tileHeightOffset;
        this.controller.addBuildingToMap(this.model.getFactories().get(6), 3, 3, 2);
    }

    @Override
    public void draw(GraphicsContext gc, int offsetX, int offsetY, double zoomFactor) {
        for (OnMapBuilding building : this.model.getBuildingsOnMap()) {
            double posX = (( building.startX + building.startY) * (double) (tileDimension / 2) + offsetX) * zoomFactor;
            double heightOffset = (-tileDimension) + (double) (tileDimension/4) + building.graphic.getHeight() - (double) (building.width * tileDimension/4);
            double posY = ((building.startX - building.startY) * (double) (tileDimension / 4) - heightOffset - (building.height * this.tileHeightOffset) + offsetY) * zoomFactor;

            gc.drawImage(building.graphic, posX, posY, building.graphic.getWidth() * zoomFactor, building.graphic.getHeight() * zoomFactor);
        }
    }

}
