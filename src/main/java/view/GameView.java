package view;

import controller.GameStateController;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.GameModel;

import java.awt.*;
import java.sql.Time;

public class GameView {

    private GameModel model;
    private Stage stage;
    private GameStateController controller;

    private Canvas gameCanvas;
    private MapView mapView;

    private Image earth;
    private Image sun;
    private Image space;

    int mouseX;
    int mouseY;
    int startX;
    int startY;
    int newX;
    int newY;

    double panDisplacementX = 0;
    double panDisplacementY = 0;

    public GameView(GameModel model, GameStateController controller, Stage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;
        Pane root = new Pane();
        this.stage.setTitle("Hello World");
        this.stage.setScene(new Scene(root, 800, 600));
        this.stage.show();
    }

    public void startGame() {
        this.mapView = new MapView(this.model.getMapHeights());

        Group root = new Group();
        Scene gameScene = new Scene( root );

        this.gameCanvas = new Canvas(800, 600);
        root.getChildren().add(this.gameCanvas);


        this.gameCanvas.widthProperty().bind(gameScene.widthProperty());
        this.gameCanvas.heightProperty().bind(gameScene.heightProperty());



        this.earth = new Image(getClass().getResourceAsStream("/earth.png"));
        this.sun   = new Image( getClass().getResourceAsStream("/sun.png") );
        this.space = new Image( getClass().getResourceAsStream("/space.png") );

        final long startNanoTime = System.nanoTime();

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                update();
            }
        }.start();

        this.stage.setScene( gameScene );
        this.enablePanning();
        this.stage.show();
    }

    private void update() {
        GraphicsContext gc = this.gameCanvas.getGraphicsContext2D();
        this.evaluateMousePosition();
        //double t = (currentNanoTime - startNanoTime) / 1000000000.0;

        // double x = 232 + 128 * Math.cos(t);
        // double y = 232 + 128 * Math.sin(t);

        // background image clears canvas
        this.prepareCanvas(gc);
        this.mapView.draw(gc, this.panDisplacementX, this.panDisplacementY);
    }

    private void evaluateMousePosition() {
        if (this.mouseX < 50) panDisplacementX += 10;
        else if (this.mouseX > this.gameCanvas.getWidth()-50) panDisplacementX -= 10;

        if (this.mouseY < 50 ) panDisplacementY += 10;
        else if (this.mouseY > this.gameCanvas.getHeight()-50) panDisplacementY -= 10;
    }

    private void prepareCanvas(GraphicsContext gc) {
        gc.setFill(new Color(1.0, 1.0, 1.0, 1.0));
        gc.fillRect(0, 0, this.gameCanvas.getWidth(), this.gameCanvas.getHeight());
    }

    private void enablePanning(){
        //aktuelle Mausposition wir in starX und startY gespeichert
        this.gameCanvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseX = ((int)event.getX());
                mouseY = ((int)event.getY());
            }
        });


        this.gameCanvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent eventPanning) {
                /*
               newX Gibt die X-Koordinate der Maus wieder wärend man sie bewegt
                newY Gibt die Y-Koordinate der Maus wieder wärend man sie bewegt
                 */
                newX = ((int)eventPanning.getX());
                newY = ((int)eventPanning.getY());

                //verschiebt die Pane an die neue Position
                // gameCanvas.setTranslateX(gameCanvas.getTranslateX() + (newX - startX));
                // gameCanvas.setTranslateY(gameCanvas.getTranslateY() + (newY - startY));

                panDisplacementX = (newX - startX);
                panDisplacementY = (newY - startY);

                eventPanning.consume();
            }

        });

        this.gameCanvas.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
                panDisplacementX = event.getX();
                panDisplacementY = event.getY();
            }
        });
    }

}
