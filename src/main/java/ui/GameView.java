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


/**
 * The type Game view.
 */
public class
GameView {

    /**
     * The Stage.
     */
    Stage stage;

    /**
     * The Model.
     */
    Game model;
    /**
     * The Controller.
     */
    GameController controller;

    /**
     * The Game loop.
     */
    GameLoop gameLoop;
    /**
     * The Renderer.
     */
    TileRenderer renderer;

    /**
     * The Top bar. Enthält MenuLeiste und Baumenüs
     */
    VBox topBar;
    /**
     * The Landscape layer.
     */
    LandscapeLayer landscapeLayer;
    /**
     * The Building layer.
     */
    BuildingLayer buildingLayer;

    /**
     * The Canvas.
     */
    Canvas canvas;
    /**
     * The Music.
     */
    MusicPlayer music;


    /**
     * The Tile dimension.
     */
    final int TILE_DIMENSION = 138;
    /**
     * The Tile height offset.
     */
    final int TILE_HEIGHT_OFFSET = 26;

    /**
     * Instantiates a new Game view.
     *
     * @param model      the model
     * @param controller the controller
     * @param stage      the stage
     */
    public GameView(Game model, GameController controller, Stage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        this.gameLoop = new GameLoop(controller);
        this.controller.setGameLoop(this.gameLoop);
        this.renderer = new TileRenderer();
    }

    /**
     * Display game screen. Anzeige des Spieles, Menüleiste und Baumenüs
     */
    public void displayGameScreen() {

        //Creat MenuBar
        MenuBar menuBar = new MenuBar();

        //Creat Menus for MenuBar
        Menu homeMenu = new Menu("Home");
        Menu bauenMenu = new Menu("Building");
        Menu landscapeMenu = new Menu("Edit");
        Menu speedMenu = new Menu("Speed");
        Menu volumeMenu = new Menu("Music");
        Menu languageMenu = new Menu("Language");


        //Creat MenuItems (Unterpunkte der Menüpunkte)
        MenuItem straßenItem = new MenuItem("Roads");
        MenuItem gleiseItem = new MenuItem("Rails");
        MenuItem airportItem = new MenuItem("Airport");
        MenuItem bäumeItem = new MenuItem("Natur");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem changeLang = new MenuItem("Change language");

        MenuItem fullVolumeItem = new MenuItem("100%");
        MenuItem halfVolumeItem = new MenuItem("50%");
        MenuItem muteVolumeItem = new MenuItem("Off");

        MenuItem landscapeItem = new MenuItem("Terrain");
        MenuItem demolitionItem = new MenuItem("Demolition");

        MenuItem speedItem0 = new MenuItem("0");
        speedItem0.setOnAction(event -> controller.stopAnimation());
        MenuItem speedItem1 = new MenuItem("1x");
        speedItem1.setOnAction(event -> controller.setTickLenght(1));
        MenuItem speedItem2 = new MenuItem("2x");
        speedItem2.setOnAction(event -> controller.setTickLenght(0.5));


        //Musik Lautstärke
        fullVolumeItem.setOnAction(event -> {
            controller.setVolume(1.0);
        });

        halfVolumeItem.setOnAction(event -> {
            controller.setVolume(0.3);
        });

        muteVolumeItem.setOnAction(event -> {
            controller.setVolume(0.0);
        });

        // Add menuItems to the Menus
        bauenMenu.getItems().addAll(straßenItem, gleiseItem, airportItem, bäumeItem);
        homeMenu.getItems().addAll(exitItem);
        landscapeMenu.getItems().add(landscapeItem);
        speedMenu.getItems().addAll(speedItem0, speedItem1, speedItem2);
        volumeMenu.getItems().addAll(fullVolumeItem, halfVolumeItem, muteVolumeItem);
        languageMenu.getItems().addAll(changeLang);

        // Add Menus to the MenuBar
        menuBar.getMenus().addAll(homeMenu, bauenMenu, lebenMenu, landscapeMenu,speedMenu,volumeMenu,languageMenu);

        BorderPane menuLeiste = new BorderPane();
        menuLeiste.setTop(menuBar);

        //Verschiedene HBoxen für die Unterschiedlichen Baumenüs (Natur, Road, Rail und Airport)
        HBox hboxNatur = new HBox();
        HBox hboxRoad = new HBox();
        HBox hboxRailway = new HBox();
        HBox hboxAirport = new HBox();

        //Pane vom Baumenü in der Später Buttons mit den verschiedenen Grafiken zum Bauen erscheinen werden
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #FFFFFF;");

        //Button closeButton = new Button("Close");

        //Schließen der Baumenüs
        Button closeButton = new Button("Close");

        closeButton.setOnAction(event -> {
            topBar.getChildren().remove(borderPane);
            borderPane.getChildren().removeAll();
            hboxNatur.getChildren().removeAll();
        });

        borderPane.setBottom(closeButton);

        //Ändern der Sprache (Deutsch und Englisch)
        changeLang.setOnAction(event -> {
           //Deutsch
            if(homeMenu.getText().equals("Home")) {
            homeMenu.setText("Start");
            bauenMenu.setText("Bauen");
            landscapeMenu.setText("Landschaft");
            speedMenu.setText("Geschwindigkeit");
            volumeMenu.setText("Musik");
            languageMenu.setText("Sprache");
            straßenItem.setText("Wege");
            gleiseItem.setText("Gleise");
            airportItem.setText("Flughafen");
            bäumeItem.setText("Natur");
            exitItem.setText("Verlassen");
            changeLang.setText("Sprache wechseln");
            closeButton.setText("Zuklappen");
            muteVolumeItem.setText("Aus");
            landscapeItem.setText("Form");
            }
            //Englisch
            else {
                homeMenu.setText("Home");
                bauenMenu.setText("Building");
                landscapeMenu.setText("Terrain");
                speedMenu.setText("Speed");
                volumeMenu.setText("Music");
                languageMenu.setText("Language");
                straßenItem.setText("Roads");
                gleiseItem.setText("Rails");
                airportItem.setText("Airport");
                bäumeItem.setText("Nature");
                exitItem.setText("Exit");
                changeLang.setText("Change language");
                closeButton.setText("Close");
                muteVolumeItem.setText("Off");
                landscapeItem.setText("Shape");
            }
        });


        // Set Accelerator for Exit MenuItem.
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));

        landscapeItem.setOnAction(event -> controller.setGameMode(GameMode.TERRAIN));
        demolitionItem.setOnAction(event -> controller.setGameMode(GameMode.DEMOLITION));

        // Close Game with Exit
        exitItem.setOnAction(event -> System.exit(0));

        BorderPane menuLeiste = new BorderPane();
        // Add menuItems to the Menus
        bauenMenu.getItems().addAll(straßenItem, gleiseItem, airportItem, bäumeItem);
        homeMenu.getItems().addAll(exitItem);
        landscapeMenu.getItems().addAll(landscapeItem, demolitionItem);
        speedMenu.getItems().addAll(speedItem0, speedItem1, speedItem2);
        volumeMenu.getItems().addAll(fullVolumeItem, halfVolumeItem, muteVolumeItem);
        languageMenu.getItems().addAll(changeLang);

        // Add Menus to the MenuBar
        menuBar.getMenus().addAll(homeMenu, bauenMenu, landscapeMenu,speedMenu,volumeMenu);
        menuLeiste.setTop(menuBar);



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
        this.buildingLayer = new BuildingLayer(this.model, this.controller, this.TILE_DIMENSION, this.TILE_HEIGHT_OFFSET);
        landscapeLayer.makeInteractable(this.gameLoop);
        this.renderer.addLandscapeLayer(landscapeLayer);
        this.renderer.addBuildingLayer(buildingLayer);
        System.out.println("Layer added");

        StackPane root = new StackPane();

        this.topBar = new VBox();
        this.topBar.setPickOnBounds(false);
        this.canvas = new Canvas(1024, 768);
        this.canvas.widthProperty().bind(this.stage.widthProperty());
        this.canvas.heightProperty().bind(this.stage.heightProperty());


        root.getChildren().add(this.canvas);
        this.topBar.getChildren().add(menuLeiste);
        root.getChildren().add(this.topBar);

        //Baumenü Natur
        //Schleife durch alle NaturObjects aus dem JSON
        for (NatureObject nature : this.model.getNatureObjects()) {

            //Überprüfung ob NaturObjekt ein Baumenu besitzt und dieses zu natur gehört
            if (nature.getBuildmenu().isPresent() && nature.getBuildmenu().get().equals("nature")) {
                Button bNatur = new Button();
                //Wenn keine Grafik für das NaturObjekt vorhanden ist soll eine einheitliche Baum-Grafik verwendet werden (error-Grafik)
                StandardImage std_nature = new StandardImage("/buildings/tree.png");
                //Grafik wird anhand des NaturObjekt name gesucht
                ImageView imageView = new ImageView(std_nature.setImage("/buildings/" + nature.getName() + ".png"));
                imageView.setPreserveRatio(true);
                //Da die Grafiken zu groß für das Baumenü sind werden sie kleiner Skaliert
                imageView.setFitHeight(100);
                //NaturObjekt Grafik wird dem Butten hinzugefügt
                bNatur.setGraphic(imageView);
                //Jeder Button bekommt einen ActionHandler, womit man das entsprechende NaturObjekt auf der Map plazieren kann
                bNatur.setOnAction(event -> {
                    buildingLayer.placeBuilding(nature);
                });
                //Button wird der entprechenden HBox hinzugefügt, damit es auch im Baumenü angezeigt wird
                hboxNatur.getChildren().add(bNatur);
            }
        }

        //ActionHandler auf BäumeItem in der Menüleiste, welche das Baumeü Natur anzeigt
        bäumeItem.setOnAction(event -> {
            topBar.getChildren().remove(borderPane);
            borderPane.setCenter(hboxNatur);
            topBar.getChildren().add(borderPane);
        });

        //Baumenü Road
        for (Road road : this.model.getRoads()) {
            if (this.model.getRoads().indexOf(road) == 8) {
                controller.addBuildingToMap(road, 1, 1, this.model.getMap().getTile(1, 1).height);
            }
            if (road.getBuildmenu().isPresent() && road.getBuildmenu().get().equals("road")) {
                Button bRoad = new Button();
                StandardImage std_road = new StandardImage("/buildings/error_tile.png");
                ImageView imageView = new ImageView(std_road.setImage("/buildings/" + road.getName() + ".png"));

                imageView.setPreserveRatio(true);
                imageView.setFitHeight(100);
                bRoad.setGraphic(imageView);

                bRoad.setOnAction(event -> {
                    buildingLayer.placeBuilding(road);
                });
                hboxRoad.getChildren().add(bRoad);
            }
        }


        straßenItem.setOnAction(event -> {
            topBar.getChildren().remove(borderPane);
            borderPane.setCenter(hboxRoad);
            topBar.getChildren().add(borderPane);
        });

        //Baumenü Rail
        for (Railway railway : this.model.getRailways()) {

            if (railway.getBuildmenu().isPresent() && railway.getBuildmenu().get().equals("rail")) {
                Button bRailway = new Button();

                StandardImage std_rail = new StandardImage("/buildings/error_tile.png");
                ImageView imageView = new ImageView(std_rail.setImage("/buildings/" + railway.getName() + ".png"));
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(50);
                bRailway.setGraphic(imageView);

                bRailway.setOnAction(event -> {
                    buildingLayer.placeBuilding(railway);
                });
                hboxRailway.getChildren().add(bRailway);
            }
        }

        gleiseItem.setOnAction(event -> {
            topBar.getChildren().remove(borderPane);
            borderPane.setCenter(hboxRailway);
            topBar.getChildren().add(borderPane);
        });


        for (AirportObject airport : this.model.getAirportObjects()) {

            if (airport.getBuildmenu().isPresent() && airport.getBuildmenu().get().equals("airport")) {
                Button bAirport = new Button();

                StandardImage std_airport = new StandardImage("/buildings/error_tile.png");
                ImageView imageView = new ImageView(std_airport.setImage("/buildings/" + airport.getName() + ".png"));
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(100);

                bAirport.setGraphic(imageView);

                bAirport.setOnAction(event -> {
                    buildingLayer.placeBuilding(airport);
                });
                hboxAirport.getChildren().add(bAirport);
            }
        }

        //Flughafen Tower
        for (Tower t : this.model.getTowers()) {

            if (t.getBuildmenu().equals("airport") && t.getSpecial().equals("tower")) {
                Button bTower = new Button();

                StandardImage std_airport = new StandardImage("/buildings/error_tile.png");
                ImageView imageView = new ImageView(std_airport.setImage("/buildings/" + t.getName() + ".png"));
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(100);

                bTower.setGraphic(imageView);

                bTower.setOnAction(event -> {
                    buildingLayer.placeBuilding(t);
                });
                hboxAirport.getChildren().add(bTower);
            }
        }

        airportItem.setOnAction(event -> {
            topBar.getChildren().remove(borderPane);
            borderPane.setCenter(hboxAirport);
            topBar.getChildren().add(borderPane);
        });


        this.gameLoop.initializeGame(this.renderer, this.canvas);
        //this.gameLoop.setInitialOffset((int) (this.canvas.getWidth()) / 2, (this.model.getMap().getWidth() * TILE_DIMENSION) / 4);
        this.gameLoop.setPanStep(26);
        this.gameLoop.startGame();

        this.stage.setScene(new Scene(root, 1024, 768));
        this.stage.show();

        this.gameLoop.addInputHandler(this.stage.getScene());
    }


    /**
     * Gets landscape layer.
     *
     * @return the landscape layer
     */
    public LandscapeLayer getLandscapeLayer() {
        return landscapeLayer;
    }

    /**
     * Gets building layer.
     *
     * @return the building layer
     */
    public BuildingLayer getBuildingLayer() {
        return buildingLayer;
    }
}
