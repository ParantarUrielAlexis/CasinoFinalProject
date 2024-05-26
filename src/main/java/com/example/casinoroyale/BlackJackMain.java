package com.example.casinoroyale;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;


import java.io.IOException;


public class BlackJackMain extends Application {

    @FXML
    Button btnStart, btnExit;

    public static MediaPlayer mediaPlayer;

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("blackjack_main.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setFullScreen(true);
        stage.setTitle("Black Jack");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }

    @FXML
    public void btnStartOnAction() throws IOException {
        // Load the game FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("blackjack_game.fxml"));
        Parent root = fxmlLoader.load();

        // Get the controller instance of the loaded FXML (BlackjackGame)
        BlackjackGame controller = fxmlLoader.getController();

        // Pass player's name and balance to the BlackjackGame controller
        controller.setPlayerInfo(getUsername(), retrieveUserBalance());

        // Create and set the scene
        Scene scene = new Scene(root);
        Stage stage = (Stage) btnStart.getScene().getWindow();
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }


    @FXML
    public void btnExitOnAction(ActionEvent event) throws IOException {
        // Stop the music if the mediaPlayer is playing
        if (mediaPlayer != null && mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mediaPlayer.stop();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
    private double retrieveUserBalance() {
        return SQLHelper.getBalance(SignInController.getUserId());
    }

    private String getUsername() {
        return SQLHelper.getUsername(SignInController.getUserId());
    }
}
