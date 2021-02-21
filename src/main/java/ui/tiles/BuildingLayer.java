package ui.tiles;

import Controller.GameController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import modell.Building;
import modell.Game;
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

        this.controller.plainGround(3, 3, 4, 4);
        //this.buildings.add(new BuildingGraphic(3, 3, 4, 4, "glasfabrik"));
        this.buildings.add(new BuildingGraphic(3, 3, 4, 4, "glasfabrik"));
    }

    public void addBuilding (Building model, int startX, int startY) {
        this.controller.plainGround(startX, startY, model.getWidth(), model.getDepth());
        this.buildings.add(new BuildingGraphic(3, 3, 4, 4, "glasfabrik"));
    }

    @Override
    public void draw(GraphicsContext gc, int offsetX, int offsetY, double zoomFactor) {
        this.buildings = this.buildings.stream()
                .sorted(Comparator.comparingInt(BuildingGraphic::getStartY))
                .sorted(Comparator.comparingInt(BuildingGraphic::getStartX))
                .collect(Collectors.toList());

        for (BuildingGraphic building : buildings) {
            double posX = (( building.startX + building.startY) * (double) (tileDimension / 2) + offsetX) * zoomFactor;
            double heightOffset = (-tileDimension) + (double) (tileDimension/4) + building.graphic.getHeight() - (double) (building.width * tileDimension/4);
            double posY = ((building.startX - building.startY) * (double) (tileDimension / 4) - heightOffset - this.tileHeightOffset + offsetY) * zoomFactor;

            gc.drawImage(building.graphic, posX, posY, building.graphic.getWidth() * zoomFactor, building.graphic.getHeight() * zoomFactor);
        }
    }

}
