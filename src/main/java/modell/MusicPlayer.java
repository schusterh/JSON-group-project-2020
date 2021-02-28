package modell;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


/**
 * The music player represents an optional feature of  the game. You can specify which background music you want
 * to have in your game in the JSON file. If no music is specified, then a standard background music is being played.
 */
public  class MusicPlayer{
    private String bm_file_path;
    /**
     * The Media player.
     */
    MediaPlayer mediaPlayer;

    /**
     * Instantiates a new Music player.
     *
     * @param bm_file_path the file path to the music file of the background music.
     */
    public MusicPlayer(String bm_file_path) {

        this.bm_file_path = getClass().getResource("/music/" + bm_file_path).toString();

    }

    /**
     * Play background music.
     */
    public void playBackgroundMusic(){

            Media h = new Media(this.bm_file_path);
            mediaPlayer = new MediaPlayer(h);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
    }

    /**
     * Sets volume.
     *
     * @param volume the volume
     */
    public void setVolume(double volume) {
        mediaPlayer.setVolume(volume);
    }

}
