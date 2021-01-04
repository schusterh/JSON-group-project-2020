package ui.tiles;

import javafx.scene.canvas.GraphicsContext;
import ui.RenderLayer;
import ui.Renderer;

import java.util.ArrayList;

public class TileRenderer implements Renderer {

    ArrayList<RenderLayer> renderLayers = new ArrayList<>();

    @Override
    public void addRenderLayer(RenderLayer layer) {
        this.renderLayers.add(layer);
    }

    @Override
    public void removeRenderLayer(RenderLayer layer) {
        this.renderLayers.remove(layer);
    }

    @Override
    public void drawFrame(GraphicsContext gc, int offsetX, int offsetY) {

        for (RenderLayer layer : this.renderLayers) {
            layer.draw(gc, offsetX, offsetY);
        }
    }
}
