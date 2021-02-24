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
import types.GameMode;
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
       this.controller.setGameLoop(this.gameLoop);
       this.renderer = new TileRenderer();
    }

    public void displayGameScreen() {

        //Creat MenuBar
        MenuBar menuBar = new MenuBar();

        //Creat Menus
        Menu homeMenu = new Menu("Home");
        Menu bauenMenu = new Menu("Building");
        Menu lebenMenu = new Menu("Live");
        Menu landscapeMenu = new Menu("Terrain");
        Menu exitMenu = new Menu("Exit");

        //Creat MenuItems
        MenuItem straßenItem = new MenuItem("Roads");
        MenuItem gleiseItem = new MenuItem("Rails");
        MenuItem airportItem = new MenuItem("Airport");
        MenuItem bäumeItem = new MenuItem("Natur");
        MenuItem speichernItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");

        MenuItem landscapeItem = new MenuItem("Shape");

        // Set Accelerator for Exit MenuItem.
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));

        landscapeItem.setOnAction(event -> controller.setGameMode(GameMode.TERRAIN));

        // When user click on the Exit item.
        exitItem.setOnAction(event -> System.exit(0));

        BorderPane menuLeiste = new BorderPane();
        // Add menuItems to the Menus
        bauenMenu.getItems().addAll(straßenItem, gleiseItem, airportItem, bäumeItem);
        homeMenu.getItems().addAll(speichernItem, exitItem);
        landscapeMenu.getItems().add(landscapeItem);

        // Add Menus to the MenuBar
        menuBar.getMenus().addAll(homeMenu, bauenMenu, lebenMenu, landscapeMenu, exitMenu);
        menuLeiste.setTop(menuBar);


        Button closeButton = new Button("Close");
        //Label messageLabel = new Label("Pflanze neue Bäume:");

        //HBox hbox = new HBox();
        HBox hboxNatur = new HBox();
        HBox hboxRoad = new HBox();
        //HBox hboxBuildings = new HBox();
        HBox hboxRailway = new HBox();
        HBox hboxAirport = new HBox();

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #FFFFFF;");


        borderPane.setBottom(closeButton);
        //borderPane.setCenter(hbox);
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

        //this.topBar = new HBox();
        this.topBar = new VBox();
        this.topBar.setPickOnBounds(false);
        this.canvas = new Canvas(1024, 768);
        this.canvas.widthProperty().bind(this.stage.widthProperty());
        this.canvas.heightProperty().bind(this.stage.heightProperty());

        // Wenn im bäumeBaumenü auf schließen gecklickt wird.
        closeButton.setOnAction(event -> {
            topBar.getChildren().remove(borderPane);
            borderPane.getChildren().removeAll();
            hboxNatur.getChildren().removeAll();
        });



        root.getChildren().add(this.canvas);
        this.topBar.getChildren().add(menuLeiste);
        root.getChildren().add(this.topBar);



        //ArrayList<Button> buttonBaum = new ArrayList<>();
        for (NatureObject nature : this.model.getNatureObjects()) {

            if (nature.getBuildmenu().isPresent() && nature.getBuildmenu().get().equals("nature")) {
                Button bNatur = new Button();
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/buildings/" + nature.getName() + ".png")));
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(100);

                bNatur.setGraphic(imageView);

                bNatur.setOnAction(event -> {
                    buildingLayer.placeBuilding(nature);
                });
                hboxNatur.getChildren().add(bNatur);
            }
        }

        bäumeItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                topBar.getChildren().remove(borderPane);
                borderPane.setCenter(hboxNatur);
                topBar.getChildren().add(borderPane);
            }
        });


        for (Road road : this.model.getRoads()) {

            if (road.getBuildmenu().isPresent() && road.getBuildmenu().get().equals("road")) {
                Button bRoad = new Button();
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/buildings/" + road.getName() + ".png")));
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(100);
                bRoad.setGraphic(imageView);

                bRoad.setOnAction(event -> {
                    buildingLayer.placeBuilding(road);
                });
                hboxRoad.getChildren().add(bRoad);
            }
        }


        straßenItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                topBar.getChildren().remove(borderPane);
                borderPane.setCenter(hboxRoad);
                topBar.getChildren().add(borderPane);
            }
        });


        for (Railway railway : this.model.getRailways()) {

            if (railway.getBuildmenu().isPresent() && railway.getBuildmenu().get().equals("rail")) {
                Button bRailway = new Button();
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/buildings/" + railway.getName() + ".png")));
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(100);
                bRailway.setGraphic(imageView);

                bRailway.setOnAction(event -> {
                    buildingLayer.placeBuilding(railway);
                });
                hboxRailway.getChildren().add(bRailway);
            }
        }


        gleiseItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                topBar.getChildren().remove(borderPane);
                borderPane.setCenter(hboxRailway);
                topBar.getChildren().add(borderPane);
            }
        });


        for (AirportObject airport : this.model.getAirportObjects()) {

            if (airport.getBuildmenu().isPresent() && airport.getBuildmenu().get().equals("airport")) {
                Button bAirport = new Button();
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/buildings/" + airport.getName() + ".png")));
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(100);

                bAirport.setGraphic(imageView);

                bAirport.setOnAction(event -> {
                    buildingLayer.placeBuilding(airport);
                });
                hboxAirport.getChildren().add(bAirport);
            }
        }

        airportItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                topBar.getChildren().remove(borderPane);
                borderPane.setCenter(hboxAirport);
                topBar.getChildren().add(borderPane);
            }
        });

/*
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
       /* straßenItem.setOnAction(event -> {
                for (Button b : buttonRoad) {
                    Button bRoad = new Button();
                    Image imageRoad = new Image(getClass().getResourceAsStream(b.getText()));
                    bRoad.setGraphic(new ImageView(imageRoad));
                    hbox.getChildren().add(bRoad);
                }
                topBar.getChildren().add(borderPane);
        });*/




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
