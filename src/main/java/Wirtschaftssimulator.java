import javafx.application.Application;
import javafx.stage.Stage;
import map.MapGenerator;
import types.Tile;
import ui.GameView;

import java.util.Random;

public class Wirtschaftssimulator extends Application {

    GameView view;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.view = new GameView(primaryStage);


        Random r = new Random(System.currentTimeMillis());

        MapGenerator mapGen = new MapGenerator(500, 500, r.nextLong());
        int[][] heightMap = mapGen.generateHeightmap();

        Tile[][] tileMap = mapGen.convertHeightMapToTileMap(heightMap);
        this.view.displayWelcomeScreen();
        //this.view.displayGameScreen(tileMap);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
