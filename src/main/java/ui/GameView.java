package ui;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import map.MapGenerator;
import ui.tiles.DefaultTileSet;
import ui.tiles.TileRenderLayer;
import ui.tiles.TileRenderer;

public class GameView {

    Stage stage;

    GameLoop gameLoop;
    TileRenderer renderer;

    Canvas canvas;

    public GameView(Stage stage) {
       this.stage = stage;

       this.gameLoop = new WSGameLoop();
       this.renderer = new TileRenderer();
    }

    public void displayGameScreen(int[][] tileMap) {
        DefaultTileSet tileSet = new DefaultTileSet("tilesets/defaultTileSet.png", 4, 3, 100, 100);
        this.renderer.addRenderLayer(new TileRenderLayer(tileMap.length, tileMap[0].length, tileMap, tileSet));

        StackPane root = new StackPane();
        this.canvas = new Canvas(800, 600);

        root.getChildren().add(this.canvas);

        this.gameLoop.initializeGame(this.renderer, this.canvas);
        this.gameLoop.startGame();

        this.stage.setScene(new Scene(root));
        this.stage.show();
    }
}
