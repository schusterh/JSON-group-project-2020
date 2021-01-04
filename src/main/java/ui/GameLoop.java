package ui;

import javafx.scene.canvas.Canvas;

public interface GameLoop {

    /**
     * This method is used to initialize everything the Game Loop needs to run.
     */
    void initializeGame(Renderer renderer, Canvas canvas);

    /**
     * Sets the loop in motion and runs the game.
     */
    void startGame();

    /**
     * Implements updating all the tasks that need to run on each frame.
     */
    void tick();

    /**
     * Handles all possible user input that can happen in this loop
     */
    void handleUserInput();

    /**
     * Stops the loop and gracefully ends it.
     */
    void stopGame();
}
