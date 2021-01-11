package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public interface Renderer {

    /**
     * Adds a new render layer
     * @param layer
     */
    void addRenderLayer(RenderLayer layer);

    /**
     * Removes a render layer
     * @param layer
     */
    void removeRenderLayer(RenderLayer layer);

    /**
     * Draws every layer to a canvas element.
     */
    void drawFrame(GraphicsContext gc, int offsetX, int offsetY);
}
