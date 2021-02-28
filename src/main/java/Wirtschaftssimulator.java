import Controller.MenuController;
import ui.MenuView;
import javafx.application.Application;
import javafx.stage.Stage;
import ui.GameView;

public class Wirtschaftssimulator extends Application {

    GameView view;

    MenuView menuView;
    MenuController menuController;

    final int TICKLENGTH = 1;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.menuController = new MenuController();
        this.menuView = new MenuView(primaryStage, this.menuController);
        this.menuController.addView(this.menuView);

        this.menuController.start();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
