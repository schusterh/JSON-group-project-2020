package Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import map.MapGenerator;
import modell.Game;
import types.Tile;
import ui.GameView;

import javax.swing.text.View;
import java.util.Random;


public class GameController {

    Game prerequisites;
    GameView view;
    Timeline timeline;
    EventHandler<ActionEvent> timelineTask;

    public GameController(Game model, int tickLength) {
        this.prerequisites = model;

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

    public void startGame() {
        Random r = new Random(System.currentTimeMillis());
        MapGenerator mapGen = new MapGenerator(this.prerequisites.getMap().getWidth(), prerequisites.getMap().getDepth(), r.nextLong());
        int[][] heightMap = mapGen.generateHeightmap();

        Tile[][] tileMap = mapGen.convertHeightMapToTileMap(heightMap);
        this.view.displayGameScreen(tileMap);

        this.startAnimation();
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
