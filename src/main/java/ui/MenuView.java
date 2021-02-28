package ui;

import Controller.MenuController;
import javafx.geometry.Pos;;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modell.Game;
import modell.JSONImporter;

import java.io.File;


/**
 * The type Menu view.
 */
public class MenuView {

    /**
     * The Controller.
     */
    MenuController controller;
    /**
     * The Stage.
     */
    Stage stage;

    /**
     * Instantiates a new Menu view.
     *
     * @param stage      the stage
     * @param controller the controller
     */
    public MenuView(Stage stage, MenuController controller) {
        this.stage = stage;
        this.controller = controller;
    }

    /**
     * Displays a  welcome screen. On the welcome screen you can choose your scenario with a file chooser.
     */
    public void displayWelcomeScreen() {

        final String TITLE = "GGG";
        final String BUTTON_LABEL = "Choose a scene";
        this.stage.setTitle(TITLE);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON-Files", "*.json"));

        StackPane root  = new StackPane();



        Button chooseSceneButton = new Button(BUTTON_LABEL);


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


                Label message= new Label(ex.getMessage());


                Button closeButton = new Button("Close");

                closeButton.setOnAction(d -> popupwindow.close());
                VBox layout= new VBox(10);
                layout.getChildren().addAll(message, closeButton);
                layout.setAlignment(Pos.CENTER);
                Scene error_scene= new Scene(layout, 300, 250);
                popupwindow.setScene(error_scene);
                popupwindow.showAndWait();

        }
        });


        Image Hintergundimage = new Image("/GGG-Hintergund.png");

        ImageView imageView = new ImageView();
        imageView.setImage(Hintergundimage);
        root.getChildren().add(imageView);
        imageView.fitWidthProperty().bind(stage.widthProperty());
        imageView.fitHeightProperty().bind(stage.heightProperty());

        chooseSceneButton.setStyle("-fx-padding: 16px 32px;");
        root.getChildren().add(chooseSceneButton);
        root.setAlignment(chooseSceneButton,Pos.CENTER);
        Scene welcomeWindow = new Scene(root,1024,768);




        this.stage.setScene(welcomeWindow);
        this.stage.show();
    }

}
