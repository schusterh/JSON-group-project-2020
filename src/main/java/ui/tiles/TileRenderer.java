package ui.tiles;

import javafx.scene.canvas.GraphicsContext;
import ui.RenderLayer;

import java.util.ArrayList;

public class TileRenderer {

    LandscapeLayer landscapeLayer;
    BuildingLayer buildingLayer;

    double zoomFactor = 1;

    /**
     * Increases zoom factor for all layers
     */
    public void increaseZoomFactor() {
        this.zoomFactor += (this.zoomFactor + 0.25 < 3.5) ? 0.25 : 0.0;
    }

    /**
     * Decreases zoom factor for all layers
     */
    public void decreaseZoomFactor() {
        this.zoomFactor -= (this.zoomFactor - 0.25 > 0) ? 0.25 : 0.0;
    }

    /**
     * Returns landscape layer
     * @return LandscapeLayer instance
     */
    public LandscapeLayer getLandscapeLayer() {
        return this.landscapeLayer;
    }

    /**
     * Sets landscape layer for game instance
     * @param layer Instance of LandscapeLayer
     */
    public void addLandscapeLayer(LandscapeLayer layer) {
        this.landscapeLayer = layer;
    }

    /**
     * Sets building layer for game instance
     * @param layer Instance of BuildingLayer
     */
    public void addBuildingLayer(BuildingLayer layer) {
        this.buildingLayer = layer;
    }

    /**
     * Handles single frame update step
     * @param gc GraphicsContext that should be drawn on
     * @param offsetX current X Offset from map
     * @param offsetY current Y Offset from map
     */
    public void drawFrame(GraphicsContext gc, int offsetX, int offsetY) {

        if (landscapeLayer != null) {
            landscapeLayer.draw(gc, offsetX, offsetY, zoomFactor);
        }
        if (buildingLayer != null) {
            buildingLayer.draw(gc, offsetX, offsetY, zoomFactor);
        }
    }
}
