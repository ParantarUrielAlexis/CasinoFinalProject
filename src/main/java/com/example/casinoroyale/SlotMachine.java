package com.example.casinoroyale;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

public class SlotMachine implements Initializable {

    private ArrayList<String> Paths1;
    private ArrayList<String> Paths2;
    private ArrayList<String> Paths3;// List to store image paths

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

    @FXML
    Label Balance;

    @FXML
    Label Total;

    public int Randomizer1, Randomizer2, Randomizer3;

    public int c1,c2,c3;

    static int userBalance;

    public int TotalBet;

    private Parent depositRoot;
    private Parent gobackRoot;
    private final AudioClip SMmusic = new AudioClip(Objects.requireNonNull(getClass().getResource("/background_musics/SlotMachineMusic.mp3")).toString());





    public void onSpinBTN(ActionEvent actionEvent) throws InterruptedException {
        if (TotalBet == 0) {
            System.out.println("Please set a bet amount before spinning!");
            return;
        }
        Paths1 = new ArrayList<>();
        Paths2 = new ArrayList<>();
        Paths3 = new ArrayList<>();// Reinitialize the list each time to avoid duplicates

        Paths1.add("1.png");
        Paths1.add("2.png");
        Paths1.add("3.png");
        Paths1.add("4.png");
        Paths1.add("5.png");
        Paths1.add("6.png");
        Paths1.add("7.png");
        Paths1.add("8.png");
        Paths1.add("9.png");
        Paths1.add("10.png");
        Paths2.add("1.png");
        Paths2.add("2.png");
        Paths2.add("3.png");
        Paths2.add("4.png");
        Paths2.add("5.png");
        Paths2.add("6.png");
        Paths2.add("7.png");
        Paths2.add("8.png");
        Paths2.add("9.png");
        Paths2.add("10.png");
        Paths3.add("1.png");
        Paths3.add("2.png");
        Paths3.add("3.png");
        Paths3.add("4.png");
        Paths3.add("5.png");
        Paths3.add("6.png");
        Paths3.add("7.png");
        Paths3.add("8.png");
        Paths3.add("9.png");
        Paths3.add("10.png");

        Shuffle1();
        Shuffle2();
        Shuffle3();

        SpinSound();


        if (checkAllSame()) {
            // Jackpot! Display jackpot message
            String checker1 = "/SlotMachine/" + "Jackpot.gif";
            Image JImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(checker1)));
            State.setImage(JImage);
            JackpotSound();

            TotalBet *= 5;
            userBalance += TotalBet;
            updateUserBalanceInDatabase();
            Balance.setText(userBalance + "");
            TotalBet = 0;
            Total.setText(TotalBet + "");
            disableButtonForTwoSeconds();
        } else if (checkTwoSame()) {
            // Two of the same! Display win 100 message
            String checker2 = "/SlotMachine/" + "YouWin.gif";
            Image WImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(checker2)));
            State.setImage(WImage);
            winSound();

            TotalBet *= 2;
            userBalance += TotalBet;
            updateUserBalanceInDatabase();
            Balance.setText(userBalance + "");
            TotalBet = 0;
            Total.setText(TotalBet + "");
            disableButtonForTwoSeconds();
        } else {
            // No win
            String checker3 = "/SlotMachine/" + "YouLost.gif";
            Image LImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(checker3)));
            State.setImage(LImage);
            loseSound();

            TotalBet *= -1;
            userBalance += TotalBet;
            updateUserBalanceInDatabase();
            Balance.setText(userBalance + "");
            TotalBet = 0;
            Total.setText(TotalBet + "");
            if(retrieveUserBalance() > 0){
                disableButtonForTwoSeconds();
            }
            else if(retrieveUserBalance() == 0){
                spinBTN.setDisable(true);
            }

        }



    }

    private boolean checkAllSame() {
        return (c1 == c2 && c1 == c3) || (c2 == c1 && c2 == c3) || (c3 == c1 && c3 == c2);
    }

    private boolean checkTwoSame() {
        // Check if c1 is the same as either c2 or c3, but not all three
        if ((c1 == c2 && c1 != c3) || (c1 == c3 && c1 != c2)) {
            return true;
        }
        // Check if c2 is the same as either c1 or c3, but not all three
        else if ((c2 == c1 && c2 != c3) || (c2 == c3 && c2 != c1)) {
            return true;
        }
        // Check if c3 is the same as either c1 or c2, but not all three
        else if ((c3 == c1 && c3 != c2) || (c3 == c2 && c3 != c1)) {
            return true;
        }
        // If none of the above conditions are met, then no two characters are same
        else {
            return false;
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SMmusic.setCycleCount(AudioClip.INDEFINITE);
        SMmusic.play();
        userBalance = (int) retrieveUserBalance();
        Balance.setText(userBalance+"");
        Total.setText(TotalBet+"");
        updateSpinButtonState();
        try {
            depositRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("deposit.fxml")));
            gobackRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
        }
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
        Randomizer1= new Random().nextInt(10);
        c1 = Randomizer1;
        String imagePath1 = "/SlotMachine/" + Paths2.get(Randomizer1);
        Image image1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath1)));

        try{
            Card1.setImage(image1);
        }catch (Exception e){
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void Shuffle2(){
        Randomizer2 = new Random().nextInt(10);
        c2 = Randomizer2;
        String imagePath2 = "/SlotMachine/" + Paths2.get(Randomizer2);
        Image image2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath2)));

        try{
            Card2.setImage(image2);
        }catch (Exception e){
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void Shuffle3(){
        Randomizer3 = new Random().nextInt(10);
        c3 = Randomizer3;
        String imagePath3 = "/SlotMachine/" + Paths3.get(Randomizer3);
        Image image3 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath3)));

        try{
            Card3.setImage(image3);
        }catch (Exception e){
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onBackClick(ActionEvent event) {
        SMmusic.stop();
        try {
            // Load the FXML file

            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

            double dpiScale = ScreenHelper.getDPIScale();


            Scale scale = new Scale(dpiScale, dpiScale);
            root.getTransforms().add(scale);

            // Create a new scene with the loaded FXML file
            Scene scene = new Scene(root);

            // Get the stage from the event source
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene on the primary stage
            primaryStage.setTitle("Sign In");
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.show();
        } catch (IOException e) {
            // Handle any IOException that occurs during loading
            e.printStackTrace();
            // You might want to show an error message to the user here
        }
    }

    static void getDPI(ActionEvent event, Parent root) {
        double dpiScale = ScreenHelper.getDPIScale();

        Scale scale = new Scale(dpiScale, dpiScale);
        root.getTransforms().add(scale);

        Scene scene = new Scene(root);

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }





    private double retrieveUserBalance() {
        return SQLHelper.getBalance(SignInController.getUserId());
    }
    private void updateUserBalanceInDatabase() {
        SQLHelper.updateBalance(SignInController.getUserId(), userBalance);
    }

    public void PlusBet(ActionEvent actionEvent) {

        // Check if new bet exceeds user balance
        if (TotalBet <= retrieveUserBalance() && (TotalBet + 50) <= retrieveUserBalance()) {
            TotalBet += 50;
            Total.setText(String.valueOf(TotalBet));
        } else if ((TotalBet + 50) > retrieveUserBalance()) {
            TotalBet = (int) retrieveUserBalance();
            Total.setText(String.valueOf(TotalBet));
        }else {
            // Handle case where bet exceeds user balance (e.g., show error message)
            System.out.println("Insufficient balance. Max bet: " + retrieveUserBalance());
        }

    }

    public void MinusBet(ActionEvent actionEvent) {
        // Check if new bet goes below zero
        if (TotalBet >= 50) {
            TotalBet -= 50;
            Total.setText(String.valueOf(TotalBet));
        }  else if ((TotalBet - 50) < 0) {
            TotalBet = 0;
            Total.setText(String.valueOf(TotalBet));
        }else {
            // Handle case where bet goes below zero (e.g., set minimum bet to 0)
            TotalBet = 0;
        }
    }

    public void MaxBet(ActionEvent actionEvent) {
        TotalBet = (int) retrieveUserBalance();
        Total.setText(String.valueOf(TotalBet));
    }

    private void updateSpinButtonState() {
        spinBTN.setDisable(userBalance == 0); // Disable button if balance is zero
        if (userBalance == 0) {
            System.out.println("Insufficient balance. Please add funds to play.");
            // You can also display an informative message to the user (e.g., using an alert dialog)
        }
    }

    public void TopUpBTN(ActionEvent actionEvent) {
        SMmusic.stop();
        if (depositRoot != null) { // Check if scene is loaded
            getDPI(actionEvent, depositRoot);
        } else {
            // Handle case where scene is not loaded (e.g., display error message)
            System.err.println("Deposit scene not loaded. Please try again later.");
        }
    }
}
