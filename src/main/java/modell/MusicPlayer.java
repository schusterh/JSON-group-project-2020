package modell;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;



public  class MusicPlayer {
    private String bm_file_path;
    private String mm_file_path;
    MediaPlayer mediaPlayer;

    public MusicPlayer(String bm_file_path, String mm_file_path) {

        this.bm_file_path = getClass().getResource("/music/" + bm_file_path).toString();

        this.mm_file_path = getClass().getResource("/music/" + mm_file_path).toString();

    }

    public void playBackgroundMusic(){

            Media h = new Media(this.bm_file_path);
            mediaPlayer = new MediaPlayer(h);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
    }

    public void playMenuMusic() {
        Media h = new Media(this.mm_file_path);
        mediaPlayer = new MediaPlayer(h);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }
}
