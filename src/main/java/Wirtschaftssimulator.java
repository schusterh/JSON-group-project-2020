import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import mapgenerator.OpenSimplexNoise;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

public class Wirtschaftssimulator extends Application {

    private static final int WIDTH = 401;
    private static final int HEIGHT = 301;
    private static final double FEATURE_SIZE = 30;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane root = new Pane();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("It works!");

        OpenSimplexNoise noise = new OpenSimplexNoise();
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                double value = noise.eval(x / FEATURE_SIZE, y / FEATURE_SIZE, 0.0);
                int rgb = value > -0.3 ? 0x00FF00 : 0x0000FF;

                if (value <= -0.4) {
                    rgb = 0x0000FF;
                }
                else if (value < -0.2) {
                    rgb = 0x009900;
                }
                else if (value < 0.0) {
                    rgb = 0x00AA00;
                }
                else if (value < 0.2) {
                    rgb = 0x00BB00;
                }
                else if (value < 0.4){
                    rgb = 0x00CC00;
                }
                else if (value < 0.6){
                    rgb = 0x00DD00;
                }
                else if (value < 0.8){
                    rgb = 0x00EE00;
                }
                else {
                    rgb = 0x00FF00;
                }

                image.setRGB(x, y, rgb);
            }
        }
        ImageIO.write(image, "png", new File("noise.png"));

        launch(args);
    }
}
