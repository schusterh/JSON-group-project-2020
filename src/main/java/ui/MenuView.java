package ui;

import Controller.MenuController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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

        VBox root  = new VBox();
        root.setAlignment(Pos.CENTER);
        Button chooseSceneButton = new Button(BUTTON_LABEL);

        chooseSceneButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(this.stage);
            JSONImporter importer = new JSONImporter(selectedFile);

            try {
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

        root.getChildren().add(chooseSceneButton);
        Scene welcomeWindow = new Scene(root,1024,768);




        this.stage.setScene(welcomeWindow);
        this.stage.show();
    }

}
