package ui.tiles;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public interface RenderObject {

    /**
     * Draws the object on a canvas
     * @param gc specified canvas
     */
    void draw(GraphicsContext gc);
}
