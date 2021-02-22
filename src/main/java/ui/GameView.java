package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import Controller.GameController;
import modell.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;

import javafx.scene.layout.*;

import javafx.stage.Stage;
import ui.tiles.BuildingLayer;
import ui.tiles.LandscapeLayer;
import ui.tiles.TileRenderer;

import java.util.ArrayList;

public class
GameView {

    Stage stage;

    Game model;
    GameController controller;

    GameLoop gameLoop;
    TileRenderer renderer;

    //HBox topBar;
    VBox topBar;
    LandscapeLayer landscapeLayer;
    BuildingLayer buildingLayer;

    Canvas canvas;

    final int TILE_DIMENSION = 138;
    final int TILE_HEIGHT_OFFSET = 26;

    public GameView(Game model, GameController controller, Stage stage) {
       this.model = model;
       this.controller = controller;
       this.stage = stage;

       this.gameLoop = new GameLoop(controller);
       this.renderer = new TileRenderer();
    }

    public void displayGameScreen() {

        //Creat MenuBar
        MenuBar menuBar = new MenuBar();

        //Creat Menus
        Menu homeMenu = new Menu("Home");
        Menu bauenMenu = new Menu("Building");
        Menu lebenMenu = new Menu("Live");
        Menu exitMenu = new Menu("Exit");

        //Creat MenuItems
        MenuItem straßenItem = new MenuItem("Roads and Rails");
        //MenuItem gleiseItem = new MenuItem("Gleise");
        MenuItem bauwerkItem = new MenuItem("Buildings");
        MenuItem bäumeItem = new MenuItem("Natur");
        MenuItem speichernItem = new MenuItem("Save");
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
        homeMenu.getItems().addAll(speichernItem, exitItem);

        // Add Menus to the MenuBar
        menuBar.getMenus().addAll(homeMenu, bauenMenu, lebenMenu, exitMenu);
        menuLeiste.setTop(menuBar);


        Button closeButton = new Button("Close");
        //Label messageLabel = new Label("Pflanze neue Bäume:");

        HBox hbox = new HBox();


        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #FFFFFF;");


        borderPane.setBottom(closeButton);
        borderPane.setCenter(hbox);
        //borderPane.setTop(messageLabel);
        //borderPane.setPrefSize(1024,150);

        this.landscapeLayer = new LandscapeLayer(this.model, this.controller, this.TILE_DIMENSION, this.TILE_HEIGHT_OFFSET);
        System.out.println("BLUBS?");
        this.buildingLayer = new BuildingLayer(this.model, this.controller, this.TILE_DIMENSION, this.TILE_HEIGHT_OFFSET);
        landscapeLayer.makeInteractable(this.gameLoop);
        this.renderer.addLandscapeLayer(landscapeLayer);
        this.renderer.addBuildingLayer(buildingLayer);
        System.out.println("Layer added");

        StackPane root = new StackPane();

        //VBox root = new VBox();
        //this.topBar = new HBox();
        this.topBar = new VBox();
        this.topBar.setPickOnBounds(false);
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
        for (NatureObject nature : this.model.getNatureObjects()) {
            System.out.println("adjkladsjkl");
            System.out.println(nature.getBuildmenu());
            if (nature.getBuildmenu().equals("nature")) {
                System.out.println("NAME: " + nature.getName());
                System.out.println("PATH: " + "/buildings/" + nature.getName() + ".png");
                Button b = new Button();
                b.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/buildings/" + nature.getName() + ".png"))));

                b.setOnAction(event -> {
                    controller.addBuildingToMap(nature, 1, 2, 1);
                    controller.addBuildingToMap(nature, 1, 3, 1);
                    controller.addBuildingToMap(nature, 1, 4, 1);
                });
                buttonBaum.add(b);
                hbox.getChildren().add(b);
            }
        }
        topBar.getChildren().add(borderPane);

        ArrayList<Button> buttonRoad = new ArrayList<>();
        buttonRoad.add(new Button ("/tilesets/strase_01.png"));
        buttonRoad.add(new Button ("/tilesets/strase_02.png"));

        // When user click on the bäume item.
        /*bäumeItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                for (Button b : buttonBaum){
                    Button bBaum = new Button();
                    Image imageBaum = new Image(getClass().getResourceAsStream(b.getText()));
                    bBaum.setGraphic(new ImageView(imageBaum));
                    hbox.getChildren().add(bBaum);
                    System.out.println("BAUM PFLANZEN YO");
                }
                topBar.getChildren().add(borderPane);
            }
        });*/

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
        //this.gameLoop.setInitialOffset((int) (this.canvas.getWidth()) / 2, (this.model.getMap().getWidth() * TILE_DIMENSION) / 4);
        this.gameLoop.setPanStep(26);
        this.gameLoop.startGame();

        this.stage.setScene(new Scene(root, 1024, 768));
        this.stage.show();

        this.gameLoop.addInputHandler(this.stage.getScene());
    }

    public LandscapeLayer getLandscapeLayer() {
        return landscapeLayer;
    }

    public BuildingLayer getBuildingLayer() {
        return buildingLayer;
    }
}
