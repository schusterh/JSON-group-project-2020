package controller;

import model.GameModel;
import view.GameView;

public class GameStateController {

    GameModel model;
    GameView view;

    public GameStateController(GameModel model) {
        this.model = model;
    }

    public void addView(GameView view) {
        this.view = view;
    }

    public void startGame() {
        this.view.startGame();
    }
}
