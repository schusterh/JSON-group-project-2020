package ui;

import javafx.scene.image.Image;

/**
 * This class represents a standard imagge which is being placed if some graphic of a menu is missing.
 */
public class StandardImage {

    private final String standard_image_path;

    /**
     * Instantiates a new Standard image.
     *
     * @param standard_image_path the standard image path
     */
    public StandardImage(String standard_image_path) {
        this.standard_image_path = standard_image_path;
    }

    /**
     * Sets image.
     *
     * @param path the path
     * @return the image
     */
    public Image setImage(String path) {

        try {
            return new Image(getClass().getResourceAsStream(path));
        }
        catch(Exception e) {
            return new Image(getClass().getResourceAsStream(standard_image_path));
        }
    }
}
