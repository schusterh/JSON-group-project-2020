package ui;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ui.tiles.TileRenderLayer;
import ui.tiles.TileRenderer;

public class WSGameLoop implements GameLoop {

    Renderer renderer;
    Canvas canvas;
    GraphicsContext gc;

    boolean isPaused = false;

    AnimationTimer timer;

    int mouseX;
    int mouseY;

    int panX;
    int panY;

    private void prepareCanvas() {
        this.gc.setFill(new Color(1.0, 1.0, 1.0, 1.0));
        this.gc.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
    }

    @Override
    public void initializeGame(Renderer renderer, Canvas canvas) {
        this.renderer = renderer;
        this.canvas = canvas;
        this.gc = this.canvas.getGraphicsContext2D();
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

    @Override
    public void startGame() {
        this.timer.start();
    }

    @Override
    public void tick() {
        this.handleUserInput();
        this.prepareCanvas();
        this.renderer.drawFrame(this.gc, this.panX, this.panY);
    }

    @Override
    public void handleUserInput() {
        if (this.mouseX > 0 && this.mouseX < 50) this.panX += 10;
        else if (this.mouseX > (this.canvas.getWidth() - 50) && this.mouseX < this.canvas.getWidth()) this.panX -= 10;
        else if (this.mouseY > 0 && this.mouseY < 50) this.panY += 10;
        else if (this.mouseY > (this.canvas.getHeight() - 50) && this.mouseY < this.canvas.getHeight()) this.panY -= 10;
    }

    @Override
    public void stopGame() {
        this.timer.stop();
    }
}
