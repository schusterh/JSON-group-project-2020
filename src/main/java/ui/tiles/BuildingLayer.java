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
import java.util.Optional;
import java.util.stream.Collectors;

public class BuildingLayer implements RenderLayer {

    private int tileDimension;
    private int tileHeightOffset;

    Game model;
    GameController controller;

    OnMapBuilding toBePlacedBuilding;
    OnMapBuilding combinationOverlay;

    ColorAdjust buildingNotPossibleEffect;

    boolean isInteractive;

    /**
     * Calculates the start coordinates on the canvas for drawing a building
     * @param building The OnMapBuilding to be drawn
     * @param offsetX current offset X positio of Map
     * @param offsetY current offset Y position of Map
     * @param zoomFactor current Zoom factor
     * @return start position with X at [0] and Y at [1]
     */
    public double[] calculateDrawingPosition(OnMapBuilding building, int offsetX, int offsetY, double zoomFactor) {
        double posX = (( building.startX + building.startY) * (double) (tileDimension / 2) + offsetX) * zoomFactor;
        double heightOffset = (-tileDimension) + (double) (tileDimension/4) + building.graphic.getHeight() - (double) (building.width * tileDimension/4);
        double posY = ((building.startX - building.startY) * (double) (tileDimension / 4) - heightOffset - (building.height * this.tileHeightOffset) + offsetY) * zoomFactor;

        return new double[]{posX, posY};
    }

    /**
     * Instantiates a new BuildingLayer
     * @param model Model of the game
     * @param controller Controller of the game
     * @param tileDimension width/height dimension of base tile
     * @param tileHeightOffset height of tile border for height displacement
     */
    public BuildingLayer (Game model, GameController controller, int tileDimension, int tileHeightOffset) {
        this.tileDimension = tileDimension;
        this.model = model;
        this.controller = controller;
        this.tileHeightOffset = tileHeightOffset;

        this.buildingNotPossibleEffect = new ColorAdjust();
        this.buildingNotPossibleEffect.setSaturation(1);
        this.buildingNotPossibleEffect.setHue(Color.RED.getHue());
    }

    /**
     * Starts building placement mode and begins mouse tracking for pending building
     * @param model Model of to be placed building
     */
    public void placeBuilding(Building model) {
        this.controller.setGameMode(GameMode.BUILDING);
        this.toBePlacedBuilding = new OnMapBuilding(model, 0, 0, 2);
    }

    /**
     * Returns the pending building if it's not a combination, else returns the combination
     * @return OnMapBuilding instance to be placed on the map
     */
    public OnMapBuilding getToBePlacedBuilding() {
        return this.combinationOverlay != null ? this.combinationOverlay : this.toBePlacedBuilding;
    }

    /**
     * Resets the current pending building to exit build mode
     */
    public void removeToBePlacedBuilding() {
        this.toBePlacedBuilding = null;
    }

    /**
     * Sets interactivity status of building layer
     * @param value true if interactive
     */
    public void setInteractive(boolean value) {
        this.isInteractive = value;
    }


    /**
     * Draws a single frame step on the building layer
     * @param gc GraphcisContext to be drawn on
     * @param offsetX current offset X position of Map
     * @param offsetY current offset Y position of Map
     * @param zoomFactor current zoom factor
     */
    @Override
    public void draw(GraphicsContext gc, int offsetX, int offsetY, double zoomFactor) {

        /*
         * Renders all buildings already on the map
         */
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

        /*
         * Renders the currently pending building and manages display of possible road combination
         */
        if (this.toBePlacedBuilding != null) {
            int[] mousePos = this.model.getCurrentMouseTileIndex();

            toBePlacedBuilding.startX = mousePos[0];
            toBePlacedBuilding.startY = mousePos[1];
            toBePlacedBuilding.height = this.model.getMap().getTile(mousePos[0], mousePos[1]).height;

            double[] startPos = calculateDrawingPosition(toBePlacedBuilding, offsetX, offsetY, zoomFactor);

            Optional<OnMapBuilding> combinationOverlayOptional = this.controller.getCombinationTile(toBePlacedBuilding);

            gc.setGlobalAlpha(0.5);
            if (!controller.isBuildingPossible()) {
                gc.setEffect(this.buildingNotPossibleEffect);
            }
            if (combinationOverlayOptional.isPresent()) {
                this.combinationOverlay = combinationOverlayOptional.get();
                gc.drawImage(combinationOverlay.graphic, startPos[0], startPos[1], combinationOverlay.graphic.getWidth() * zoomFactor, combinationOverlay.graphic.getHeight() * zoomFactor);
            } else {
                this.combinationOverlay = null;
                gc.drawImage(toBePlacedBuilding.graphic, startPos[0], startPos[1], toBePlacedBuilding.graphic.getWidth() * zoomFactor, toBePlacedBuilding.graphic.getHeight() * zoomFactor);

            }
            //gc.setGlobalAlpha(1.0);
            gc.setEffect(null);
        }
    }

}
