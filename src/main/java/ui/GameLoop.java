package ui;

import Controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import modell.Game;
import types.GameMode;
import ui.tiles.TileRenderer;

import java.time.ZonedDateTime;
import java.util.ArrayList;
public class GameLoop {

    TileRenderer renderer;
    Canvas canvas;
    GraphicsContext gc;

    GameController controller;

    ArrayList<String> input = new ArrayList<>();

    AnimationTimer timer;

    int mouseX;
    int mouseY;

    int panX;
    int panY;

    int panStep = 10;

    int selectionRadius = 0;

    public GameLoop(GameController controller) {
        this.controller = controller;
    }

    private void prepareCanvas() {
        this.gc.setFill(Color.web("#555568"));
        this.gc.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
    }

    public void setInitialOffset(int offsetX, int offsetY) {
        this.panX = offsetX;
        this.panY = -offsetY;
    }

    public void setPanStep(int panStep) {
        this.panStep = panStep;
    }

    public int[] getMousePosition() {
        return new int[]{this.mouseX, this.mouseY};
    }

    public void addInputHandler(Scene scene) {
        scene.setOnKeyPressed(
                event -> {
                    String code = event.getCode().toString();
                    if (!input.contains(code)) {
                        input.add(code);
                    }
                }
        );

        scene.setOnKeyReleased(
                event -> {
                    String code = event.getCode().toString();
                    input.remove(code);
                }
        );

        scene.setOnMousePressed(
                event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (!input.contains("CLICK_PRIMARY")) input.add("CLICK_PRIMARY");
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        if (!input.contains("CLICK_SECONDARY")) input.add("CLICK_SECONDARY");
                    }
                }
        );
    }

    public void initializeGame(Renderer renderer, Canvas canvas) {

    }

    public void initializeGame(TileRenderer renderer, Canvas canvas) {
        this.renderer = renderer;
        this.canvas = canvas;
        this.gc = this.canvas.getGraphicsContext2D();
        this.gc.setImageSmoothing(false);

        this.timer = new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                tick();
            }
        };

        this.canvas.setOnMouseMoved(event -> {
            this.mouseX = ((int)event.getX());
            this.mouseY = ((int)event.getY());
        });



    }

    public void startGame() {
        this.timer.start();
    }

    public void tick() {
        this.handleUserInput();
        this.prepareCanvas();
        this.renderer.drawFrame(this.gc, this.panX, this.panY);
    }

    public void handleUserInput() {
        if (this.input.contains("W"))
            this.panY += this.panStep;
        if (this.input.contains("A"))
            this.panX += this.panStep;
        if (this.input.contains("S"))
            this.panY -= this.panStep;
        if (this.input.contains("D"))
            this.panX -= this.panStep;
        if (this.input.contains("ADD")) {
            this.renderer.increaseZoomFactor();
            this.input.remove("ADD");
        }
        if (this.input.contains("SUBTRACT")) {
            this.renderer.decreaseZoomFactor();
            this.input.remove("SUBTRACT");
        }
        if (this.input.contains("ESCAPE")) {
            this.controller.setGameMode(GameMode.NORMAL);

        }
        if (this.input.contains("PERIOD")) {
            this.selectionRadius = this.selectionRadius < 6 ? this.selectionRadius + 1 : 6;
            this.renderer.getLandscapeLayer().setRadius(this.selectionRadius);
            this.input.remove("PERIOD");
        }
        if (this.input.contains("COMMA")) {
            this.selectionRadius = this.selectionRadius > 0 ? this.selectionRadius - 1 : 0;
            this.renderer.getLandscapeLayer().setRadius(this.selectionRadius);
            this.input.remove("COMMA");
        }
        if (this.input.contains("CLICK_PRIMARY")) {
            if (this.controller.getGameMode() == GameMode.TERRAIN) {
                this.controller.increaseHeightOfSelectedTiles();
            } else if (this.controller.getGameMode() == GameMode.BUILDING) {
                this.controller.placePendingBuilding();
            }
            this.input.remove("CLICK_PRIMARY");
        }
        if (this.input.contains("CLICK_SECONDARY")) {
            this.controller.decreaseHeightOfSelectedTiles();
            this.input.remove("CLICK_SECONDARY");
        }


        /*
        if (this.mouseX > 0 && this.mouseX < 50) this.panX += 10;
        else if (this.mouseX > (this.canvas.getWidth() - 50) && this.mouseX < this.canvas.getWidth()) this.panX -= 10;
        else if (this.mouseY > 0 && this.mouseY < 50) this.panY += 10;
        else if (this.mouseY > (this.canvas.getHeight() - 50) && this.mouseY < this.canvas.getHeight()) this.panY -= 10;
         */
    }

    public void stopGame() {
        this.timer.stop();
    }
}
