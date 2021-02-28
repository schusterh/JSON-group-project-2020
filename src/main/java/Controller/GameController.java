package Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import modell.*;
import types.Coordinate;
import types.GameMode;
import types.OnMapBuilding;
import types.Tile;
import ui.GameLoop;
import ui.GameView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;


public class GameController {

    Game model;
    GameView view;
    Timeline timeline;
    EventHandler<ActionEvent> timelineTask;
    MusicPlayer music;
    GameLoop gameLoop;
    int tickLenght;
    int currentTick;

    GameMode gameMode;

    /**
     * Instantiates a new GameController instance
     * @param model Model of the game
     * @param tickLength Ticklength in seconds
     */
    public GameController(Game model, int tickLength) {
        this.model = model;
        this.tickLenght = tickLength;
        this.currentTick = 0;
        music = new MusicPlayer(this.model.getBackgroundMusic());
        this.gameMode = GameMode.NORMAL;

        this.timelineTask = event -> {
            this.currentTick++;
            model.handleUpdate(currentTick);
        };

        this.timeline = new Timeline(
                new KeyFrame(Duration.seconds(this.tickLenght),
                        this.timelineTask)
        );
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Sets volume of background music
     * @param volume double [0;1]
     */
    public void setVolume(double volume) {
        this.music.setVolume(volume);
    }

    /**
     * Sets game loop instance
     * @param loop game loop instance
     */
    public void setGameLoop(GameLoop loop) {
        this.gameLoop = loop;
    }

    /**
     * Gets game loop instance
     * @return game loop instance
     */
    public GameLoop getGameLoop() { return this.gameLoop; }

    /**
     * Increases height of the selected tile inside the landscape layer
     */
    public void increaseHeightOfSelectedTiles() {
        ArrayList<Coordinate> selectedTiles = this.view.getLandscapeLayer().getSelectedTiles();
        this.model.getMap().increaseHeightOfSelectedTiles(selectedTiles);
    }

    /**
     * Decreases height of the selected tile in side the landscape layer
     */
    public void decreaseHeightOfSelectedTiles() {
        ArrayList<Coordinate> selectedTiles = this.view.getLandscapeLayer().getSelectedTiles();
        this.model.getMap().decreaseHeightOfSelectedTiles(selectedTiles);
    }

    /**
     * Adds a new building to the map
     * @param model corresponding model
     * @param startX start X Position of Building
     * @param startY start Y Position of Building
     * @param height height of Building
     */
    public void addBuildingToMap(Building model, int startX, int startY, int height) {
        this.model.addBuildingToMap(model, startX, startY, height);
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        this.view.displayGameScreen();
        this.startAnimation();

        music.playBackgroundMusic();

    }

    /**
     * Sets the current mouse tile index
     * @param pos mouse X tile at [0], mouse Y tile at [1]
     */
    public void setCurrentMouseTileIndex(int[] pos) {
        this.model.setCurrentMouseTileIndex(pos);
    }

    /**
     * Returns the current game mode
     * @return Game Mode enum value
     */
    public GameMode getGameMode() { return gameMode; }

    /**
     * Checks if a building can be placed at the specified location
     * @return true if building at current mouse pointer is possible
     */
    public boolean isBuildingPossible() {
        OnMapBuilding pendingBuilding = this.view.getBuildingLayer().getToBePlacedBuilding();

        if (model.isInWater(pendingBuilding.startX, pendingBuilding.startY, pendingBuilding.width, pendingBuilding.depth)) {
            return false;
        }
        if (!model.isInMap(pendingBuilding.startX, pendingBuilding.startY, pendingBuilding.width, pendingBuilding.depth)) {
            return false;
        }

        if (pendingBuilding.model.getClass() == Road.class) {
            Road pendingRoad = (Road) pendingBuilding.model;
            if (pendingRoad.getSpecial().isEmpty()) {
                ArrayList<OnMapBuilding> adjRoads = this.model.getAdjRoads(pendingBuilding);
                return !adjRoads.isEmpty();
            }
        }

        return true;
    }

    /**
     * Checks for possible tile combination and returns if possible a combination
     * @param pendingBuilding building to be placed
     * @return Optional of possible tile combination
     */
    public Optional<OnMapBuilding> getCombinationTile(OnMapBuilding pendingBuilding) {
        Optional<OnMapBuilding> underlyingRoadOptional = this.model.getBuildingAtTile(pendingBuilding.getStartX(), pendingBuilding.getStartY());

        if (pendingBuilding.model.getClass() == Road.class) {
            Road roadModel = (Road) pendingBuilding.model;
            if (underlyingRoadOptional.isPresent()) {
                OnMapBuilding underlyingRoad = underlyingRoadOptional.get();

                if (underlyingRoad.model.getClass() == Road.class) {
                    if (roadModel.getCombines().isPresent()) {
                        HashMap<String, String> combines = roadModel.getCombines().get();
                        if (combines.containsKey(underlyingRoad.model.getName())) {
                            Road replacementModel = this.model.getRoads().stream().filter(roadFilter -> roadFilter.getName().equals(combines.get(underlyingRoad.model.getName()))).findFirst().orElse(null);
                            if (replacementModel != null) {
                                return Optional.of(new OnMapBuilding(replacementModel, pendingBuilding.startX, pendingBuilding.startY, pendingBuilding.height));
                            }
                        }
                    }
                }
            }
        }


        return Optional.empty();
    }

    /**
     * Places the pending building from BuildingLayer on the map and in the model
     */
    public void placePendingBuilding() {
        if (isBuildingPossible()) {
            OnMapBuilding newBuilding = this.view.getBuildingLayer().getToBePlacedBuilding();
            boolean isCombination = this.view.getBuildingLayer().isPendingBuildingCombination();
            this.model.addBuildingToMap(new OnMapBuilding(newBuilding.model, newBuilding.startX, newBuilding.startY, newBuilding.height), isCombination);
        }
    }

    /**
     * Switches between Game modes and ensures mouse focus for layers
     * @param gameMode specified new game mode
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;

        switch (this.gameMode) {
            case NORMAL:
                this.view.getLandscapeLayer().setInteractive(false);
                this.view.getLandscapeLayer().clearSelectedTiles();
                this.view.getBuildingLayer().removeToBePlacedBuilding();
                this.view.getBuildingLayer().setInteractive(true);
                break;
            case TERRAIN:
                this.view.getLandscapeLayer().setInteractive(true);
                this.view.getBuildingLayer().setInteractive(false);
                break;
            case BUILDING: case DEMOLITION:
                this.view.getLandscapeLayer().setInteractive(false);
                this.view.getBuildingLayer().setInteractive(false);
                break;
        }
    }

    /**
     * Starts game ticks
     */
    public void startAnimation() {
        this.timeline.play();
    }

    public void removeBuilding() {
        Optional<OnMapBuilding> toBeRemovedBuilding = this.view.getBuildingLayer().getToBeRemovedBuilding();

        toBeRemovedBuilding.ifPresent(onMapBuilding -> {
            if (onMapBuilding.model.getClass() != Factory.class) {
                this.model.getBuildingsOnMap().remove(onMapBuilding);
            }
        });
    }

    /**
     * Stops game ticks
     */
    public void stopAnimation() {
        this.timeline.stop();
    }

    /**
     * Sets view instane
     * @param view view instance
     */
    public void setView(GameView view) {
        this.view = view;
    }

    /**
     * Updates the ticklength to allow for multiple playback speeds
     * @param new_Lenght ticklength in seconds
     */
    public void setTickLenght(double new_Lenght) {
        this.stopAnimation();
        this.timeline = new Timeline(
                new KeyFrame(Duration.seconds(new_Lenght),
                        this.timelineTask));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.startAnimation();
    }
}
