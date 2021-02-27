package Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import modell.Building;
import modell.Game;
import modell.MusicPlayer;
import modell.Road;
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

    public GameController(Game model, int tickLength) {
        this.model = model;
        this.tickLenght = tickLength;
        this.currentTick = 0;
        music = new MusicPlayer(this.model.getBackgroundMusic());

        this.timelineTask = event -> {
            this.currentTick++;
            model.handleUpdate();
            System.out.println("UPDATING EVERYTHING!");
        };

        this.timeline = new Timeline(
                new KeyFrame(Duration.seconds(this.tickLenght),
                        this.timelineTask)
        );
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void setGameLoop(GameLoop loop) {
        this.gameLoop = loop;
    }

    public GameLoop getGameLoop() { return this.gameLoop; }

    public void increaseHeightOfSelectedTiles() {
        ArrayList<Coordinate> selectedTiles = this.view.getLandscapeLayer().getSelectedTiles();
        this.model.getMap().increaseHeightOfSelectedTiles(selectedTiles);
    }

    public void decreaseHeightOfSelectedTiles() {
        ArrayList<Coordinate> selectedTiles = this.view.getLandscapeLayer().getSelectedTiles();
        this.model.getMap().decreaseHeightOfSelectedTiles(selectedTiles);
    }

    public void plainGround(int startX, int startY, int width, int depth, int height, boolean isConcrete) {
        this.model.getMap().plainGround(startX, startY, width, depth, height, isConcrete);
    }

    public void addBuildingToMap(Building model, int startX, int startY, int height) {
        this.model.addBuildingToMap(model, startX, startY, height);
    }

    public void startGame() {
        this.view.displayGameScreen();
        this.startAnimation();
        music.playBackgroundMusic();

    }

    public void setCurrentMouseTileIndex(int[] pos) {
        this.model.setCurrentMouseTileIndex(pos);
    }

    public GameMode getGameMode() { return gameMode; }

    public boolean isBuildingPossible() {
        OnMapBuilding pendingBuilding = this.view.getBuildingLayer().getToBePlacedBuilding();

        if (model.isInWater(pendingBuilding.startX, pendingBuilding.startY, pendingBuilding.width, pendingBuilding.depth)) return false;
        if (!model.isInMap(pendingBuilding.startX, pendingBuilding.startY, pendingBuilding.width, pendingBuilding.depth)) return false;

        if (pendingBuilding.model.getClass() == Road.class) {
            Road pendingRoad = (Road) pendingBuilding.model;
            if (!pendingRoad.getSpecial().isPresent()) {
                ArrayList<OnMapBuilding> adjRoads = this.model.getAdjRoads(pendingBuilding);
                return !adjRoads.isEmpty();
            }
        }

        return true;
    }

    public Optional<OnMapBuilding> getCombinationTile(OnMapBuilding pendingBuilding) {
        Optional<OnMapBuilding> underlyingRoadOptional = this.model.getBuildingAtTile(pendingBuilding.getStartX(), pendingBuilding.getStartY());

        if (pendingBuilding.model.getClass() == Road.class) {
            Road roadModel = (Road) pendingBuilding.model;
            if (underlyingRoadOptional.isPresent()) {
                OnMapBuilding underlyingRoad = underlyingRoadOptional.get();

                if (underlyingRoad.model.getClass() == Road.class) {
                    if (roadModel.getCombines().isPresent()) {
                        HashMap<String, String> combines = roadModel.getCombines().get();
                        System.out.println("Trying to combine " + roadModel.getName() + " and " + underlyingRoad.model.getName());
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

    public void placePendingBuilding() {
        if (isBuildingPossible()) {
            OnMapBuilding newBuilding = this.view.getBuildingLayer().getToBePlacedBuilding();
            this.model.addBuildingToMap(new OnMapBuilding(newBuilding.model, newBuilding.startX, newBuilding.startY, newBuilding.height));
            //this.setGameMode(GameMode.NORMAL);
        }
    }

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
            case BUILDING:
                this.view.getLandscapeLayer().setInteractive(false);
                this.view.getBuildingLayer().setInteractive(false);
                break;
        }
    }

    public void startAnimation() {
        this.timeline.play();
    }
    public void stopAnimation() {
        this.timeline.stop();
    }
    public void setView(GameView view) {
        this.view = view;
    }
    public void startSimulation() {
       // this.view.startview();
        this.startAnimation();
    }

    public void setTickLenght(double new_Lenght) {
        this.stopAnimation();
        this.timeline = new Timeline(
                new KeyFrame(Duration.seconds(new_Lenght),
                        this.timelineTask));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.startAnimation();
        System.out.println("GAMESPEEED CHANGEEED!!!!");

    }
}
