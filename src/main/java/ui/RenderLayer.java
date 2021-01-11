package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public interface RenderLayer {

    /**
     * Draws the contents of this layer to a canvas element.
     * @param canvas the canvas that should be drawn on
     */
    void draw(GraphicsContext gc, int offsetX, int offsetY, double zoomFactor);
}
