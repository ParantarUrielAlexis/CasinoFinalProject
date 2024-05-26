package com.example.casinoroyale;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class HiloMusicController {
    private static MediaPlayer mediaPlayer;

    public static void playMusic() {
        if (mediaPlayer == null) {
            String musicFile = "src/main/resources/background_musics/jazz.mp3"; // replace with your music file path
            Media media = new Media(new File(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
        }
        mediaPlayer.play();
    }

    public static void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
