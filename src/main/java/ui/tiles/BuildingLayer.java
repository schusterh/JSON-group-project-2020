package ui.tiles;

import Controller.GameController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import modell.Building;
import modell.Game;
import types.GameMode;
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

    OnMapBuilding toBePlacedBuilding;

    boolean isInteractive;

    public double[] calculateDrawingPosition(OnMapBuilding building, int offsetX, int offsetY, double zoomFactor) {
        double posX = (( building.startX + building.startY) * (double) (tileDimension / 2) + offsetX) * zoomFactor;
        double heightOffset = (-tileDimension) + (double) (tileDimension/4) + building.graphic.getHeight() - (double) (building.width * tileDimension/4);
        double posY = ((building.startX - building.startY) * (double) (tileDimension / 4) - heightOffset - (building.height * this.tileHeightOffset) + offsetY) * zoomFactor;

        return new double[]{posX, posY};
    }

    public BuildingLayer (Game model, GameController controller, int tileDimension, int tileHeightOffset) {
        this.tileDimension = tileDimension;
        this.model = model;
        this.controller = controller;
        this.tileHeightOffset = tileHeightOffset;
        this.controller.addBuildingToMap(this.model.getFactories().get(6), 3, 3, 2);
    }

    public void placeBuilding(Building model) {
        this.controller.setGameMode(GameMode.BUILDING);
        this.toBePlacedBuilding = new OnMapBuilding(model, 0, 0, 2);
    }

    public OnMapBuilding removeToBePlacedBuilding() {
        OnMapBuilding returnValue = this.toBePlacedBuilding;
        this.toBePlacedBuilding = null;
        return returnValue;
    }

    public void setInteractive(boolean value) {
        this.isInteractive = value;
    }

    @Override
    public void draw(GraphicsContext gc, int offsetX, int offsetY, double zoomFactor) {
        if (this.toBePlacedBuilding != null) {
            int[] mousePos = this.model.getCurrentMouseTileIndex();

            toBePlacedBuilding.startX = mousePos[0];
            toBePlacedBuilding.startY = mousePos[1];
            toBePlacedBuilding.height = this.model.getMap().getTile(mousePos[0], mousePos[1]).height;

            double[] startPos = calculateDrawingPosition(toBePlacedBuilding, offsetX, offsetY, zoomFactor);

            gc.setGlobalAlpha(0.5);
            gc.drawImage(toBePlacedBuilding.graphic, startPos[0], startPos[1], toBePlacedBuilding.graphic.getWidth() * zoomFactor, toBePlacedBuilding.graphic.getHeight() * zoomFactor);
            gc.setGlobalAlpha(1);
        }

        for (OnMapBuilding building : this.model.getBuildingsOnMap()) {

            double[] startPos = calculateDrawingPosition(building, offsetX, offsetY, zoomFactor);
            gc.drawImage(building.graphic, startPos[0], startPos[1], building.graphic.getWidth() * zoomFactor, building.graphic.getHeight() * zoomFactor);
        }
    }

}
