package ui.tiles;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import ui.RenderLayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BuildingLayer implements RenderLayer {

    private int tileDimension;

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

    public BuildingLayer (int tileDimension) {
        this.tileDimension = tileDimension;

        this.buildings.add(new BuildingGraphic(3, 3, 4, 4, "glasfabrik"));
    }

    @Override
    public void draw(GraphicsContext gc, int offsetX, int offsetY, double zoomFactor) {
        this.buildings = this.buildings.stream()
                .sorted(Comparator.comparingInt(BuildingGraphic::getStartY))
                .collect(Collectors.toList());

        for (BuildingGraphic building : buildings) {
            double posX = (( building.startX + building.startY) * (double) (tileDimension / 2) + offsetX) * zoomFactor;
            double heightOffset = building.graphic.getHeight() * zoomFactor - (building.width * tileDimension/4) + tileDimension/2;
            double posY = ((building.startX - building.startY) * (double) (tileDimension / 4) - heightOffset + offsetY) * zoomFactor;

            gc.drawImage(building.graphic, posX, posY, building.graphic.getWidth() * zoomFactor, building.graphic.getHeight() * zoomFactor);
        }
    }

}
