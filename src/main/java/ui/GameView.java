package ui;

import modell.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;

import javafx.scene.control.Button;
import javafx.scene.layout.*;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import types.Tile;
import ui.tiles.DefaultTileSet;
import ui.tiles.TileRenderLayer;
import ui.tiles.TileRenderer;

import java.io.File;
public class
GameView {

    Stage stage;

    GameLoop gameLoop;
    TileRenderer renderer;

    HBox topBar;
    Canvas canvas;

    final String TILE_SET_URI = "tilesets/finalTiles.png";

    final int TILE_SET_COLS = 4;
    final int TILE_SET_ROWS = 1;

    final int TILE_WIDTH = 138;
    final int TILE_HEIGHT = 138;

    public GameView(Stage stage) {
       this.stage = stage;

       this.gameLoop = new GameLoop();
       this.renderer = new TileRenderer();
    }


    public void displayGameScreen(Tile[][] tileMap) {
        DefaultTileSet tileSet = new DefaultTileSet(this.TILE_SET_URI, this.TILE_SET_COLS, this.TILE_SET_ROWS, this.TILE_WIDTH, this.TILE_HEIGHT);
        TileRenderLayer landscapeLayer = new TileRenderLayer(tileMap.length, tileMap[0].length, tileMap, tileSet);
        landscapeLayer.setOffsetFromCenterY(26);
        landscapeLayer.makeInteractable(this.gameLoop);
        this.renderer.addRenderLayer(landscapeLayer);


        VBox root = new VBox();
        this.topBar = new HBox();
        this.canvas = new Canvas(1024, 768);
        this.canvas.widthProperty().bind(this.stage.widthProperty());
        this.canvas.heightProperty().bind(this.stage.heightProperty());

        root.getChildren().add(this.canvas);

        this.gameLoop.initializeGame(this.renderer, this.canvas);
        this.gameLoop.setInitialOffset((int) (this.canvas.getWidth()) / 2, (tileMap[0].length * this.TILE_HEIGHT) / 4);
        this.gameLoop.setPanStep(26);
        this.gameLoop.startGame();

        this.stage.setScene(new Scene(root, 1024, 768));
        this.stage.show();

        this.gameLoop.addInputHandler(this.stage.getScene());
    }
}
