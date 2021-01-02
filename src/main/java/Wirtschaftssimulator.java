import controller.GameStateController;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import mapgenerator.OpenSimplexNoise;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import model.GameModel;
import view.GameView;

public class Wirtschaftssimulator extends Application {

    GameView view;
    GameStateController controller;
    GameModel model;

    private final int MAP_WIDTH = 200;
    private final int MAP_HEIGHT = 100;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.model = new GameModel(MAP_WIDTH, MAP_HEIGHT);
        this.controller = new GameStateController(model);
        this.view = new GameView(model, controller, primaryStage);
        this.controller.addView(this.view);

        this.controller.startGame();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("It works!");

        launch(args);
    }
}
