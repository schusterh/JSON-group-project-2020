import javafx.application.Application;
import javafx.stage.Stage;
import map.MapGenerator;
import types.Tile;
import ui.GameView;

public class Wirtschaftssimulator extends Application {

    GameView view;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.view = new GameView(primaryStage);

        MapGenerator mapGen = new MapGenerator(100, 100, 2045731057);
        int[][] heightMap = mapGen.generateHeightmap();

        Tile[][] tileMap = mapGen.convertHeightMapToTileMap(heightMap);

        this.view.displayGameScreen(tileMap);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
