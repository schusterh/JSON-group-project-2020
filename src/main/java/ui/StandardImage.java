package ui;

import javafx.scene.image.Image;

public class StandardImage {

    private final String standard_image_path;

    public StandardImage(String standard_image_path) {
        this.standard_image_path = standard_image_path;
    }

    public Image setImage(String path) {

        try {
            return new Image(getClass().getResourceAsStream(path));
        }
        catch(Exception e) {
            return new Image(getClass().getResourceAsStream(standard_image_path));
        }
    }
}
