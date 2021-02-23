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
import ui.GameView;
import java.util.ArrayList;


public class GameController {

    Game model;
    GameView view;
    Timeline timeline;
    EventHandler<ActionEvent> timelineTask;
    MusicPlayer music;

    public GameController(Game model, int tickLength) {
        this.model = model;
        music = new MusicPlayer(this.model.getBackgroundMusic(), this.model.getMenuMusic());

        this.timelineTask = event -> {
            //model.handleUpdate();
            //view.handleModelUpdate();
            System.out.println("UPDATING EVERYTHING!");
        };

        this.timeline = new Timeline(
                new KeyFrame(Duration.seconds(tickLength),
                        this.timelineTask)
        );
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void increaseHeightOfSelectedTiles() {
        ArrayList<Coordinate> selectedTiles = this.view.getLandscapeLayer().getSelectedTiles();
        this.model.getMap().increaseHeightOfSelectedTiles(selectedTiles);
    }

    public void decreaseHeightOfSelectedTiles() {
        ArrayList<Coordinate> selectedTiles = this.view.getLandscapeLayer().getSelectedTiles();
        this.model.getMap().decreaseHeightOfSelectedTiles(selectedTiles);
    }

    public void plainGround(int startX, int startY, int width, int depth, int height) {
        this.model.getMap().plainGround(startX, startY, width, depth, height);
    }

    public void addBuildingToMap(Building model, int startX, int startY, int height) {
        this.model.addBuildingToMap(model, startX, startY, height);
    }

    public void startGame() {
        this.view.displayGameScreen();
        System.out.println("WELL2?");
        this.startAnimation();

        music.playBackgroundMusic();

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



}
