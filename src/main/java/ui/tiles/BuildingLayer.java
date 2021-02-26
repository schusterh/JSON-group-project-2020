package ui.tiles;

import Controller.GameController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import modell.Building;
import modell.Factory;
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

    ColorAdjust buildingNotPossibleEffect;

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

        this.buildingNotPossibleEffect = new ColorAdjust();
        this.buildingNotPossibleEffect.setSaturation(1);
        this.buildingNotPossibleEffect.setHue(Color.RED.getHue());
    }

    public void placeBuilding(Building model) {
        this.controller.setGameMode(GameMode.BUILDING);
        this.toBePlacedBuilding = new OnMapBuilding(model, 0, 0, 2);
    }

    public OnMapBuilding getToBePlacedBuilding() {
        return this.toBePlacedBuilding;
    }

    public void removeToBePlacedBuilding() {
        this.toBePlacedBuilding = null;
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
            if (!controller.isBuildingPossible()) {
                gc.setEffect(this.buildingNotPossibleEffect);
            }
            gc.drawImage(toBePlacedBuilding.graphic, startPos[0], startPos[1], toBePlacedBuilding.graphic.getWidth() * zoomFactor, toBePlacedBuilding.graphic.getHeight() * zoomFactor);
            gc.setGlobalAlpha(1f);
            gc.setEffect(null);
        }

        for (OnMapBuilding building : this.model.getBuildingsOnMap()) {

            double[] startPos = calculateDrawingPosition(building, offsetX, offsetY, zoomFactor);
            gc.drawImage(building.graphic, startPos[0], startPos[1], building.graphic.getWidth() * zoomFactor, building.graphic.getHeight() * zoomFactor);
            if (building.model.getClass() == Factory.class) {
                Factory model = (Factory) building.model;
                Font temp = gc.getFont();
                gc.setFont(new Font("Arial", 24));
                gc.setFill(Color.WHITE);
                gc.fillText(model.getProdMessage(), startPos[0] + (building.graphic.getWidth() / 3), startPos[1] + 100);
                gc.setFont(temp);
            }
        }
    }

}
