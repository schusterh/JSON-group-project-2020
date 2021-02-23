package ui;

import Controller.MenuController;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Box;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modell.Game;
import modell.JSONImporter;
import types.Tile;

import java.io.File;

public class MenuView {

    MenuController controller;
    Stage stage;

    public MenuView(Stage stage, MenuController controller) {
        this.stage = stage;
        this.controller = controller;
    }

    public void displayWelcomeScreen() {

        final String TITLE = "Wirtschaftssimulator";
        final String BUTTON_LABEL = "Choose a scene";
        this.stage.setTitle(TITLE);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON-Files", "*.json"));

        Group root  = new Group();
        //VBox VBox = new VBox();
        //VBox.setAlignment(Pos.CENTER);

        Button chooseSceneButton = new Button(BUTTON_LABEL);
        chooseSceneButton.setTranslateX(500);
        chooseSceneButton.setTranslateY(380);

        chooseSceneButton.setOnAction(e -> {
            try {
            File selectedFile = fileChooser.showOpenDialog(this.stage);

            if(selectedFile == null) {
                throw new Exception("Please choose a scenario file!");
            }

            JSONImporter importer = new JSONImporter(selectedFile);


                Game prerequisites = importer.LoadMap();
                this.controller.createGame(prerequisites, this.stage);
            }
            catch (Exception ex) {
                Stage popupwindow=new Stage();

                popupwindow.initModality(Modality.APPLICATION_MODAL);
                popupwindow.setTitle("Error!");


                Label label1= new Label(ex.getMessage());


                Button button1= new Button("Close");

                button1.setOnAction(d -> popupwindow.close());
                VBox layout= new VBox(10);
                layout.getChildren().addAll(label1, button1);
                layout.setAlignment(Pos.CENTER);
                Scene error_scene= new Scene(layout, 300, 250);
                popupwindow.setScene(error_scene);
                popupwindow.showAndWait();

        }
        });

       // Image Hintergundimage = new Image("/Menu_Hintergrund.png");
        Image Hintergundimage = new Image("/Hintergrund.jpg");

        ImageView imageView = new ImageView();
        imageView.setImage(Hintergundimage);
        root.getChildren().add(imageView);

        imageView.setFitWidth(1024);
        imageView.setFitHeight(768);

        root.getChildren().add(chooseSceneButton);
        //root.getChildren().add(VBox);
        Scene welcomeWindow = new Scene(root,1024,768);




        this.stage.setScene(welcomeWindow);
        this.stage.show();
    }

}
