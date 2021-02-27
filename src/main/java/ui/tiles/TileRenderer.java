package ui.tiles;

import javafx.scene.canvas.GraphicsContext;
import ui.RenderLayer;

import java.util.ArrayList;

public class TileRenderer {

    LandscapeLayer landscapeLayer;
    BuildingLayer buildingLayer;

    double zoomFactor = 1;

    public void increaseZoomFactor() {
        this.zoomFactor += (this.zoomFactor + 0.5 < 3.5) ? 0.5 : 0.0;
    }

    public void decreaseZoomFactor() {
        this.zoomFactor -= (this.zoomFactor - 0.5 > 0) ? 0.5 : 0.0;
    }

    public LandscapeLayer getLandscapeLayer() {
        return this.landscapeLayer;
    }

    public void addLandscapeLayer(LandscapeLayer layer) {
        this.landscapeLayer = layer;
    }

    public void addBuildingLayer(BuildingLayer layer) {
        this.buildingLayer = layer;
    }

    public void drawFrame(GraphicsContext gc, int offsetX, int offsetY) {

        if (landscapeLayer != null) {
            landscapeLayer.draw(gc, offsetX, offsetY, zoomFactor);
        }
        if (buildingLayer != null) {
            buildingLayer.draw(gc, offsetX, offsetY, zoomFactor);
        }
    }
}
