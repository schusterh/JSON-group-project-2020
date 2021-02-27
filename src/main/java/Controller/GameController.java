package Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import modell.Building;
import modell.Game;
import modell.MusicPlayer;
import types.Coordinate;
import types.GameMode;
import types.OnMapBuilding;
import types.Tile;
import ui.GameLoop;
import ui.GameView;

import java.io.FileNotFoundException;
import java.util.ArrayList;


public class GameController {

    Game model;
    GameView view;
    Timeline timeline;
    EventHandler<ActionEvent> timelineTask;
    MusicPlayer music;
    GameLoop gameLoop;
    int tickLenght;

    GameMode gameMode;

    public GameController(Game model, int tickLength) {
        this.model = model;
        this.tickLenght = tickLength;
        music = new MusicPlayer(this.model.getBackgroundMusic());

        this.timelineTask = event -> {
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

    public void placePendingBuilding() {
        OnMapBuilding newBuilding = this.view.getBuildingLayer().getToBePlacedBuilding();
        this.model.addBuildingToMap(new OnMapBuilding(newBuilding.model, newBuilding.startX, newBuilding.startY, newBuilding.height));
        //this.setGameMode(GameMode.NORMAL);
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
