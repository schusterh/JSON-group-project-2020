package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import modell.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;

import javafx.scene.layout.*;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import types.Tile;
import ui.tiles.DefaultTileSet;
import ui.tiles.TileRenderLayer;
import ui.tiles.TileRenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class GameView {

    Stage stage;

    GameLoop gameLoop;
    TileRenderer renderer;

    VBox topBar;
    Canvas canvas;

    final String TILE_SET_URI = "tilesets/1BitPixelArtTileSet.png";

    final int TILE_SET_COLS = 5;
    final int TILE_SET_ROWS = 1;

    final int TILE_WIDTH = 32;
    final int TILE_HEIGHT = 32;

    public GameView(Stage stage) {
       this.stage = stage;

       this.gameLoop = new GameLoop();
       this.renderer = new TileRenderer();
    }

    public void displayWelcomeScreen() {

        final String TITLE = "Wirtschaftssimulator";
        final String BUTTON_LABEL = "Choose a scene";
        this.stage.setTitle(TITLE);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON-Files", "*.json"));

        VBox root  = new VBox();
        root.setAlignment(Pos.CENTER);
        Button chooseSceneButton = new Button(BUTTON_LABEL);
        chooseSceneButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(this.stage);
            JSONImporter importer = new JSONImporter(selectedFile);

            try {
                importer.LoadMap();
            }
            catch (Exception ex) {
                System.out.println("error eccoured");
            }
        });

        root.getChildren().add(chooseSceneButton);
        Scene welcomeWindow = new Scene(root,1024,768);
        this.stage.setScene(welcomeWindow);
        this.stage.show();
    }

    public void displayGameScreen(Tile[][] tileMap) {

        //Creat MenuBar
        MenuBar menuBar = new MenuBar();

        //Creat Menus
        Menu homeMenu = new Menu("Home");
        Menu bauenMenu = new Menu("Bauen");
        Menu lebenMenu = new Menu("Leben");
        Menu exitMenu = new Menu("Exit");

        //Creat MenuItems
        MenuItem straßenItem = new MenuItem("Straßen");
        MenuItem gleiseItem = new MenuItem("Gleise");
        MenuItem bauwerkItem = new MenuItem("Bauwerk");
        MenuItem bäumeItem = new MenuItem("Bäume");
        MenuItem speichernItem = new MenuItem("Speichern");
        MenuItem exitItem = new MenuItem("Exit");

        // Set Accelerator for Exit MenuItem.
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));

        // When user click on the Exit item.
        exitItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        BorderPane menuLeiste = new BorderPane();
        // Add menuItems to the Menus
        bauenMenu.getItems().addAll(straßenItem, gleiseItem, bauwerkItem, bäumeItem);
        homeMenu.getItems().addAll(speichernItem,exitItem);

        // Add Menus to the MenuBar
        menuBar.getMenus().addAll(homeMenu, bauenMenu, lebenMenu, exitMenu);
        menuLeiste.setTop(menuBar);


        //Button restartButton = new Button("Bauen");
        Label messageLabel = new Label("Pflanze neue Bäume:");

        HBox hbox = new HBox();

        BorderPane borderPane = new BorderPane();

        //borderPane.setBottom(restartButton);
        borderPane.setCenter(hbox);
        borderPane.setTop(messageLabel);
        borderPane.setPrefSize(1024,150);



        DefaultTileSet tileSet = new DefaultTileSet(this.TILE_SET_URI, this.TILE_SET_COLS, this.TILE_SET_ROWS, this.TILE_WIDTH, this.TILE_HEIGHT);
        TileRenderLayer landscapeLayer = new TileRenderLayer(tileMap.length, tileMap[0].length, tileMap, tileSet);
        landscapeLayer.setOffsetFromCenterY(5);
        landscapeLayer.makeInteractable(this.gameLoop);
        this.renderer.addRenderLayer(landscapeLayer);

        StackPane root = new StackPane();
        //VBox root = new VBox();
        this.topBar = new VBox();
        this.canvas = new Canvas(1024, 768);
        this.canvas.widthProperty().bind(this.stage.widthProperty());
        this.canvas.heightProperty().bind(this.stage.heightProperty());




        // When user click on the bäume item.
        bäumeItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                topBar.getChildren().add(borderPane);
            }
        });

        root.getChildren().add(this.canvas);
        this.topBar.getChildren().add(menuLeiste);
        //root.getChildren().add(borderPane);
        root.getChildren().add(this.topBar);

        //ArrayList<Button> buttons = new ArrayList<>();
        //hbox.getChildren().add(buttons);

        for (int i=0; i<5; i++){
            Button button = new Button();
            Image imageOk = new Image(getClass().getResourceAsStream("/tilesets/baum_02.png"));
            button.setGraphic(new ImageView(imageOk));
            hbox.getChildren().add(button);

        }




        this.gameLoop.initializeGame(this.renderer, this.canvas);
        this.gameLoop.setInitialOffset((int) (this.canvas.getWidth()) / 2, (tileMap[0].length * this.TILE_HEIGHT) / 4);
        this.gameLoop.setPanStep(10);
        this.gameLoop.startGame();

        this.stage.setScene(new Scene(root, 1024, 768));
        this.stage.show();

        this.gameLoop.addInputHandler(this.stage.getScene());
    }
}
