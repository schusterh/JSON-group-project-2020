package ui.tiles;

import javafx.scene.canvas.GraphicsContext;
import ui.RenderLayer;
import ui.Renderer;

import java.util.ArrayList;

public class TileRenderer {

    ArrayList<TileRenderLayer> renderLayers = new ArrayList<>();

    double zoomFactor = 2;

    public void increaseZoomFactor() {
        this.zoomFactor += (this.zoomFactor + 0.5 < 3.5) ? 0.5 : 0.0;
    }

    public void decreaseZoomFactor() {
        this.zoomFactor -= (this.zoomFactor - 0.5 > 0) ? 0.5 : 0.0;
    }

    public TileRenderLayer getLandscapeLayer() {
        return this.renderLayers.get(0);
    }

    public void addRenderLayer(TileRenderLayer layer) {
        this.renderLayers.add(layer);
    }

    public void removeRenderLayer(TileRenderLayer layer) {
        this.renderLayers.remove(layer);
    }

    public void drawFrame(GraphicsContext gc, int offsetX, int offsetY) {

        for (RenderLayer layer : this.renderLayers) {
            layer.draw(gc, offsetX, offsetY, this.zoomFactor);
        }
    }
}
