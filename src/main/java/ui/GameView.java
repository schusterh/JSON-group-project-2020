package ui;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import types.Tile;
import ui.tiles.DefaultTileSet;
import ui.tiles.TileRenderLayer;
import ui.tiles.TileRenderer;

public class GameView {

    Stage stage;

    WSGameLoop gameLoop;
    TileRenderer renderer;

    Canvas canvas;

    final String TILE_SET_URI = "tilesets/pixelArtTileSet.png";

    final int TILE_SET_COLS = 3;
    final int TILE_SET_ROWS = 1;

    final int TILE_WIDTH = 64;
    final int TILE_HEIGHT = 64;

    public GameView(Stage stage) {
       this.stage = stage;

       this.gameLoop = new WSGameLoop();
       this.renderer = new TileRenderer();
    }

    public void displayGameScreen(Tile[][] tileMap) {
        DefaultTileSet tileSet = new DefaultTileSet(this.TILE_SET_URI, this.TILE_SET_COLS, this.TILE_SET_ROWS, this.TILE_WIDTH, this.TILE_HEIGHT);
        TileRenderLayer landscapeLayer = new TileRenderLayer(tileMap.length, tileMap[0].length, tileMap, tileSet);
        landscapeLayer.setOffsetFromCenterY(9);
        landscapeLayer.makeInteractable(this.gameLoop);
        this.renderer.addRenderLayer(landscapeLayer);


        StackPane root = new StackPane();
        this.canvas = new Canvas(1024, 768);
        this.canvas.widthProperty().bind(this.stage.widthProperty());
        this.canvas.heightProperty().bind(this.stage.heightProperty());

        root.getChildren().add(this.canvas);

        this.gameLoop.initializeGame(this.renderer, this.canvas);
        this.gameLoop.setInitialOffset((int) (this.canvas.getWidth()) / 2, (tileMap[0].length * this.TILE_HEIGHT) / 4);
        this.gameLoop.startGame();

        this.stage.setScene(new Scene(root, 1024, 768));
        this.stage.show();
    }
}
