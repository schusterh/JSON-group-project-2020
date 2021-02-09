import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import modell.Game;

import javax.swing.text.View;


public class Controller {

    Game model;
    View view;
    Timeline timeline;
    EventHandler<ActionEvent> timelineTask;

    public Controller(Game model, int tickLength) {
        this.model = model;

        this.timelineTask = event -> {
            //model.handleUpdate();
            //view.handleModelUpdate();
        };

        this.timeline = new Timeline(
                new KeyFrame(Duration.seconds(tickLength),
                        this.timelineTask)
        );
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void startAnimation() {
        this.timeline.play();
    }
    public void stopAnimation() {
        this.timeline.stop();
    }
    public void setView(View view) {
        this.view = view;
    }
    public void startSimulation() {
       // this.view.startview();
        this.startAnimation();
    }
}
