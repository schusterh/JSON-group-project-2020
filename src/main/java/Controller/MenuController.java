package Controller;

import javafx.stage.Stage;
import map.MapGenerator;
import types.Tile;
import ui.GameView;
import ui.MenuView;
import modell.Game;

import java.util.Random;


public class MenuController {

    final int TICKLENGTH = 1;

    MenuView view;

    GameController gameController;
    GameView gameView;

    public MenuController() {
    }

    public void start() {
        this.view.displayWelcomeScreen();
    }

    public void addView(MenuView view) {
        this.view = view;
    }

    public void createGame(Game prerequisites, Stage stage) {
        this.gameController = new GameController(prerequisites, TICKLENGTH);
        this.gameView = new GameView(stage);
        this.gameController.setView(this.gameView);

        this.gameController.startGame();
    }
}
