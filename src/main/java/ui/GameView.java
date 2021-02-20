package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
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

public class
GameView {

    Stage stage;

    GameLoop gameLoop;
    TileRenderer renderer;

    //HBox topBar;
    VBox topBar;
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
        MenuItem straßenItem = new MenuItem("Straßen und Gleise");
        //MenuItem gleiseItem = new MenuItem("Gleise");
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
        bauenMenu.getItems().addAll(straßenItem, bauwerkItem, bäumeItem);
        homeMenu.getItems().addAll(speichernItem,exitItem);

        // Add Menus to the MenuBar
        menuBar.getMenus().addAll(homeMenu, bauenMenu, lebenMenu, exitMenu);
        menuLeiste.setTop(menuBar);


        Button closeButton = new Button("schließen");
        Label messageLabel = new Label("Pflanze neue Bäume:");

        HBox hbox = new HBox();


        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #FFFFFF;");


        borderPane.setBottom(closeButton);
        borderPane.setCenter(hbox);
        borderPane.setTop(messageLabel);
        //borderPane.setPrefSize(1024,150);


        DefaultTileSet tileSet = new DefaultTileSet(this.TILE_SET_URI, this.TILE_SET_COLS, this.TILE_SET_ROWS, this.TILE_WIDTH, this.TILE_HEIGHT);
        TileRenderLayer landscapeLayer = new TileRenderLayer(tileMap.length, tileMap[0].length, tileMap, tileSet);
        landscapeLayer.setOffsetFromCenterY(26);
        landscapeLayer.makeInteractable(this.gameLoop);
        this.renderer.addRenderLayer(landscapeLayer);

        StackPane root = new StackPane();
        //VBox root = new VBox();
        //this.topBar = new HBox();
        this.topBar = new VBox();
        this.canvas = new Canvas(1024, 768);
        this.canvas.widthProperty().bind(this.stage.widthProperty());
        this.canvas.heightProperty().bind(this.stage.heightProperty());



        // Wenn im bäumeBaumenü auf schließen gecklickt wird.
        closeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                topBar.getChildren().remove(borderPane);
                borderPane.getChildren().removeAll();
                hbox.getChildren().removeAll();
            }
        });



        root.getChildren().add(this.canvas);
        this.topBar.getChildren().add(menuLeiste);
        root.getChildren().add(this.topBar);

        ArrayList<Button> buttonBaum = new ArrayList<>();
        buttonBaum.add(new Button ("/tilesets/baum_01.png"));
        buttonBaum.add(new Button ("/tilesets/baum_02.png"));
        buttonBaum.add(new Button ("/tilesets/baum_03.png"));

        ArrayList<Button> buttonRoad = new ArrayList<>();
        buttonRoad.add(new Button ("/tilesets/strase_01.png"));
        buttonRoad.add(new Button ("/tilesets/strase_02.png"));

        // When user click on the bäume item.
        bäumeItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                for (Button b : buttonBaum){
                    Button bBaum = new Button();
                    Image imageBaum = new Image(getClass().getResourceAsStream(b.getText()));
                    bBaum.setGraphic(new ImageView(imageBaum));
                    hbox.getChildren().add(bBaum);
                }
                topBar.getChildren().add(borderPane);
            }
        });

        // When user click on the road item.
        straßenItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                for (Button b : buttonRoad){
                    Button bRoad = new Button();
                    Image imageRoad= new Image(getClass().getResourceAsStream(b.getText()));
                    bRoad.setGraphic(new ImageView(imageRoad));
                    hbox.getChildren().add(bRoad);
                }
                topBar.getChildren().add(borderPane);
            }
        });




        this.gameLoop.initializeGame(this.renderer, this.canvas);
        this.gameLoop.setInitialOffset((int) (this.canvas.getWidth()) / 2, (tileMap[0].length * this.TILE_HEIGHT) / 4);
        this.gameLoop.setPanStep(26);
        this.gameLoop.startGame();

        this.stage.setScene(new Scene(root, 1024, 768));
        this.stage.show();

        this.gameLoop.addInputHandler(this.stage.getScene());
    }
}
