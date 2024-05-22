package com.example.casinoroyale;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class SlotMachine implements Initializable {

    private ArrayList<String> imagePaths1;
    private ArrayList<String> imagePaths2;
    private ArrayList<String> imagePaths3;// List to store image paths

    @FXML
    ImageView Card1;
    @FXML
    ImageView Card2;
    @FXML
    ImageView Card3;

    @FXML
    Button spinBTN;

    @FXML
    ImageView State;
    private MediaPlayer mediaPlayer;


    public void onSpinBTN(ActionEvent actionEvent) throws InterruptedException {
        imagePaths1 = new ArrayList<>();
        imagePaths2 = new ArrayList<>();
        imagePaths3 = new ArrayList<>();// Reinitialize the list each time to avoid duplicates


        imagePaths1.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\1.png");
        imagePaths1.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\2.png");
        imagePaths1.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\3.png");
        imagePaths1.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\4.png");
        imagePaths1.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\5.png");
        imagePaths1.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\6.png");
        imagePaths1.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\7.png");
        imagePaths1.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\8.png");
        imagePaths1.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\9.png");
        imagePaths1.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\10.png");



        imagePaths2.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\1.png");
        imagePaths2.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\2.png");
        imagePaths2.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\3.png");
        imagePaths2.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\4.png");
        imagePaths2.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\5.png");
        imagePaths2.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\6.png");
        imagePaths2.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\7.png");
        imagePaths2.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\8.png");
        imagePaths2.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\9.png");
        imagePaths2.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\10.png");

        imagePaths3.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\1.png");
        imagePaths3.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\2.png");
        imagePaths3.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\3.png");
        imagePaths3.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\4.png");
        imagePaths3.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\5.png");
        imagePaths3.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\6.png");
        imagePaths3.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\7.png");
        imagePaths3.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\8.png");
        imagePaths3.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\9.png");
        imagePaths3.add("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\10.png");


        Shuffle1();
        Shuffle2();
        Shuffle3();



        SpinSound();
        disableButtonForTwoSeconds();



        if (checkAllSame()) {
            // Jackpot! Display jackpot message
            State.setImage(new Image("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\Jackpot.gif"));
            JackpotSound();
            System.out.println("Jackpot!");  // Replace with your UI update for Jackpot
        } else if (checkTwoSame()) {
            // Two of the same! Display win 100 message
            State.setImage(new Image("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\YouWin.gif"));
            winSound();
            System.out.println("Win 100!");  // Replace with your UI update for win 100
        } else {
            // No win
            State.setImage(new Image("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\YouLost.gif"));
            loseSound();
            System.out.println("Try again!");  // Replace with your UI update for no win
        }

    }

    private boolean checkAllSame() {
        String firstImage = imagePaths1.get(0);
        return firstImage.equals(imagePaths2.get(0)) && firstImage.equals(imagePaths3.get(0));
    }

    private boolean checkTwoSame() {
        String image1 = imagePaths1.get(0);
        String image2 = imagePaths2.get(0);
        String image3 = imagePaths3.get(0);

        return (image1.equals(image2) && !image1.equals(image3)) ||
                (image2.equals(image3) && !image1.equals(image2)) ||
                (image1.equals(image3) && !image1.equals(image2));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SMmusic();
    }

    public void winSound(){
        String s = "src/main/resources/background_musics/win_sound.mp3";
        mediaPlayer = new MediaPlayer(new Media(Paths.get(s).toUri().toString())); // Create a new MediaPlayer
        mediaPlayer.play();
    }

    public void loseSound(){
        String s = "src/main/resources/background_musics/lose_sound.mp3";
        mediaPlayer = new MediaPlayer(new Media(Paths.get(s).toUri().toString())); // Create a new MediaPlayer
        mediaPlayer.play();
    }

    public void JackpotSound(){
        String s = "src/main/resources/background_musics/JackpotSound.mp3";
        mediaPlayer = new MediaPlayer(new Media(Paths.get(s).toUri().toString())); // Create a new MediaPlayer

        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
        pauseTransition.setOnFinished(event -> {
            mediaPlayer.play();
        });
        pauseTransition.play();
    }

    public void SpinSound(){
        String s = "src/main/resources/background_musics/SpinMusic.mp3";
        mediaPlayer = new MediaPlayer(new Media(Paths.get(s).toUri().toString())); // Create a new MediaPlayer
        mediaPlayer.play();
    }

    public void disableButtonForTwoSeconds() {
        // Disable the buttons
        spinBTN.setDisable(true);

        // Pause for 2 seconds before enabling the buttons
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
        pauseTransition.setOnFinished(event -> {
            spinBTN.setDisable(false);
        });
        pauseTransition.play();
    }

    public void Shuffle1(){
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        Collections.shuffle(imagePaths1);
        pauseTransition.setOnFinished(event -> {
            Card1.setImage(new Image(imagePaths1.get(0)));
        });
        pauseTransition.play();
    }

    public void Shuffle2(){
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        Collections.shuffle(imagePaths2);
        pauseTransition.setOnFinished(event -> {
            Card2.setImage(new Image(imagePaths2.get(0)));
        });
        pauseTransition.play();
    }

    public void Shuffle3(){
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        Collections.shuffle(imagePaths3);
        pauseTransition.setOnFinished(event -> {
            Card3.setImage(new Image(imagePaths3.get(0)));
        });
        pauseTransition.play();
    }

    public void onBackClick(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
            double dpiScale = Main.DPIUtil.getDPIScale();

            Scale scale = new Scale(dpiScale, dpiScale);
            root.getTransforms().add(scale);

            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();



        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
        }
    }

    public void SMmusic() {
        String sm = "src/main/resources/background_musics/SlotMachineMusic.mp3";
        Media smh = new Media(Paths.get(sm).toUri().toString());
        mediaPlayer = new MediaPlayer(smh);

        // Add event handler for end of media
        mediaPlayer.setOnEndOfMedia(() -> {
            // Rewind the media to the beginning
            mediaPlayer.seek(Duration.ZERO);
            // Play the media again
            mediaPlayer.play();
        });

        mediaPlayer.play();
    }
}
