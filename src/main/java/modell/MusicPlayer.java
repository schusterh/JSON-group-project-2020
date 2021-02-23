package modell;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;

public  class MusicPlayer {
    private String bm_file_path;
    private String mm_file_path;
    MediaPlayer mediaPlayer;

    public MusicPlayer(String bm_file_path, String mm_file_path) {
        this.bm_file_path = bm_file_path;
        this.mm_file_path = mm_file_path;
    }

    public void playBackgroundMusic() {


        Media h = new Media(Paths.get(this.bm_file_path).toUri().toString());
        mediaPlayer = new MediaPlayer(h);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

}
