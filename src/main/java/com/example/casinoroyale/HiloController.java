package com.example.casinoroyale;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;



public class HiloController {
    @FXML
    public ImageView cardRandomizer;

    @FXML
    public ImageView cardRandomizer2;

    @FXML
    public ImageView showLose,showWin, showCollected;
    @FXML
    public ImageView foreground;

    public Button btnHigh;
    @FXML
    public Button btnLow;

    @FXML
    public Button betID;

    @FXML
    public Button btnSkip, btnCollect;

    @FXML
    public Text balanceID, jackpotID;

    @FXML
    public Text showNoStake, showINoBalance;
    @FXML
    public TextField inputBalance;
    @FXML
    public ProgressBar progressBarID;
    @FXML
    public Text oddsHigh, oddsLow, balancePayout;

    @FXML
    public Button btnBack, btnTopUp;
    private Parent depositRoot;
    private  Parent gobackRoot;
    MediaPlayer mediaPlayer;
    public int cardNumber1;
    private int skipCounter = 1;
    private int correctGuessCount = 0;

    static int userBalance;

    public void setForeground() {
        btnBack.setVisible(false);
        btnTopUp.setVisible(false);
        userBalance = (int) retrieveUserBalance();
        balanceID.setText(String.valueOf(userBalance));
        foreground.setVisible(true); // Set visibility to true initially
        progressBarID.setVisible(true); // Set visibility to true initially
        progress();
        // Fade out animation for progressBarID
        FadeTransition fadeOutProgressBar = new FadeTransition(Duration.seconds(1), progressBarID);
        fadeOutProgressBar.setFromValue(1);
        fadeOutProgressBar.setToValue(0);

        // Pause for 3 seconds before starting the fade-out animation for progressBarID
        PauseTransition pauseProgressBar = new PauseTransition(Duration.seconds(3));
        pauseProgressBar.setOnFinished(event -> fadeOutProgressBar.play());

        // Set visibility to false after fade-out for progressBarID
        fadeOutProgressBar.setOnFinished(event -> {
            progressBarID.setVisible(false);
            // After progressBarID fade-out, start progress

        });

        // Fade out animation for foreground
        FadeTransition fadeOutForeground = new FadeTransition(Duration.seconds(1), foreground);
        fadeOutForeground.setFromValue(1);
        fadeOutForeground.setToValue(0);

        // Pause for 3 seconds before starting the fade-out animation for foreground
        PauseTransition pauseForeground = new PauseTransition(Duration.seconds(3));
        pauseForeground.setOnFinished(event -> fadeOutForeground.play());

        // Set visibility to false after fade-out for foreground
        fadeOutForeground.setOnFinished(event -> {
            foreground.setVisible(false);
            showExit();
        });

        // Start the pause transition for progressBarID
        pauseProgressBar.play();
        // Start the pause transition for foreground
        pauseForeground.play();
    }

    public void progress() {
        // Set initial progress
        progressBarID.setProgress(0);

        // Define the duration for the progress animation (in milliseconds)
        int durationMillis = 3000; // 3 seconds

        // Create a Timeline animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBarID.progressProperty(), 0)),
                new KeyFrame(Duration.millis(durationMillis), new KeyValue(progressBarID.progressProperty(), 1))
        );

        // Play the timeline animation
        timeline.play();
    }




    public static final String[] cardImages = {
            "ace_of_spades.png", "ace_of_hearts.png", "ace_of_clubs.png", "ace_of_diamonds.png",
            "2_of_clubs.png", "2_of_diamonds.png", "2_of_spades.png", "2_of_hearts.png",
            "3_of_clubs.png", "3_of_diamonds.png", "3_of_spades.png", "3_of_hearts.png",
            "4_of_clubs.png", "4_of_diamonds.png", "4_of_spades.png", "4_of_hearts.png",
            "5_of_clubs.png", "5_of_diamonds.png", "5_of_spades.png", "5_of_hearts.png",
            "6_of_clubs.png", "6_of_diamonds.png", "6_of_spades.png", "6_of_hearts.png",
            "7_of_clubs.png", "7_of_diamonds.png", "7_of_spades.png", "7_of_hearts.png",
            "8_of_clubs.png", "8_of_diamonds.png", "8_of_spades.png", "8_of_hearts.png",
            "9_of_clubs.png", "9_of_diamonds.png", "9_of_spades.png", "9_of_hearts.png",
            "10_of_clubs.png", "10_of_diamonds.png", "10_of_spades.png", "10_of_hearts.png",
            "jack_of_clubs.png", "jack_of_diamonds.png", "jack_of_spades.png", "jack_of_hearts.png",
            "queen_of_clubs.png", "queen_of_diamonds.png", "queen_of_spades.png", "queen_of_hearts.png",
            "king_of_clubs.png", "king_of_diamonds.png", "king_of_spades.png", "king_of_hearts.png",
    };


    // Map to store card values
    public static final Map<String, Integer> cardValuesMap = new HashMap<>();

    static {
        cardValuesMap.put("ace_of_spades.png", 1);
        cardValuesMap.put("ace_of_hearts.png", 1);
        cardValuesMap.put("ace_of_clubs.png", 1);
        cardValuesMap.put("ace_of_diamonds.png", 1);

        cardValuesMap.put("2_of_clubs.png", 2);
        cardValuesMap.put("2_of_diamonds.png", 2);
        cardValuesMap.put("2_of_spades.png", 2);
        cardValuesMap.put("2_of_hearts.png", 2);

        cardValuesMap.put("3_of_clubs.png", 3);
        cardValuesMap.put("3_of_diamonds.png", 3);
        cardValuesMap.put("3_of_spades.png", 3);
        cardValuesMap.put("3_of_hearts.png", 3);

        cardValuesMap.put("4_of_clubs.png", 4);
        cardValuesMap.put("4_of_diamonds.png", 4);
        cardValuesMap.put("4_of_spades.png", 4);
        cardValuesMap.put("4_of_hearts.png", 4);

        cardValuesMap.put("5_of_clubs.png", 5);
        cardValuesMap.put("5_of_diamonds.png", 5);
        cardValuesMap.put("5_of_spades.png", 5);
        cardValuesMap.put("5_of_hearts.png", 5);

        cardValuesMap.put("6_of_clubs.png", 6);
        cardValuesMap.put("6_of_diamonds.png", 6);
        cardValuesMap.put("6_of_spades.png", 6);
        cardValuesMap.put("6_of_hearts.png", 6);

        cardValuesMap.put("7_of_clubs.png", 7);
        cardValuesMap.put("7_of_diamonds.png", 7);
        cardValuesMap.put("7_of_spades.png", 7);
        cardValuesMap.put("7_of_hearts.png", 7);

        cardValuesMap.put("8_of_clubs.png", 8);
        cardValuesMap.put("8_of_diamonds.png", 8);
        cardValuesMap.put("8_of_spades.png", 8);
        cardValuesMap.put("8_of_hearts.png", 8);

        cardValuesMap.put("9_of_clubs.png", 9);
        cardValuesMap.put("9_of_diamonds.png", 9);
        cardValuesMap.put("9_of_spades.png", 9);
        cardValuesMap.put("9_of_hearts.png", 9);

        cardValuesMap.put("10_of_clubs.png", 10);
        cardValuesMap.put("10_of_diamonds.png", 10);
        cardValuesMap.put("10_of_spades.png", 10);
        cardValuesMap.put("10_of_hearts.png", 10);

        cardValuesMap.put("jack_of_clubs.png", 11);
        cardValuesMap.put("jack_of_diamonds.png", 11);
        cardValuesMap.put("jack_of_spades.png", 11);
        cardValuesMap.put("jack_of_hearts.png", 11);

        cardValuesMap.put("queen_of_clubs.png", 12);
        cardValuesMap.put("queen_of_diamonds.png", 12);
        cardValuesMap.put("queen_of_spades.png", 12);
        cardValuesMap.put("queen_of_hearts.png", 12);

        cardValuesMap.put("king_of_clubs.png", 13);
        cardValuesMap.put("king_of_diamonds.png", 13);
        cardValuesMap.put("king_of_spades.png", 13);
        cardValuesMap.put("king_of_hearts.png", 13);
    }



    public void showExit(){
        btnBack.setVisible(true);
        btnTopUp.setVisible(true);
    }
    public void initialized() {
        userBalance = (int) retrieveUserBalance();
        balanceID.setText(String.valueOf(userBalance));
        // Generate random card numbers for each ImageView
        cardNumber1 = new Random().nextInt(52); // 0 to 51
        int cardNumber2 = new Random().nextInt(52);

        // Get the image file name for cardNumber1
        String imagePath1 = "/cards/" + cardImages[cardNumber1];
        // Get the image file name for cardNumber2
        String imagePath2 = "/cards/" + cardImages[cardNumber2];

        showNoStake.setOpacity(0);
        showINoBalance.setOpacity(0);
        // Load images for each ImageView based on the card numbers
        try {
            // Load and set the image for cardRandomizer
            Image image1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath1)));
            cardRandomizer.setImage(image1);
            setOddsOfCard();
        } catch (NullPointerException e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            depositRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("deposit.fxml")));
            gobackRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
        }
    }



    public int playGame() {
        balanceID.setText(String.valueOf(userBalance));

        if (inputBalance.getText().isEmpty()) {
            showNoStake.setOpacity(1);
            return -1;
        }

        int betAmount = Integer.parseInt(inputBalance.getText());
        int newBalance = userBalance - betAmount;

        if (newBalance >= 0) {
            userBalance = newBalance;
            balanceID.setText(String.valueOf(newBalance));
            updateUserBalanceInDatabase();
        } else {
            showINoBalance.setOpacity(1);
            return -1;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                cardSound();
                initialized();
                setOddsOfCard();

                Platform.runLater(() -> {
                    btnBack.setDisable(true);
                    btnTopUp.setDisable(true);
                    btnHigh.setDisable(false);
                    btnLow.setDisable(false);
                    btnSkip.setDisable(false);
                    btnCollect.setDisable(true);
                    betID.setDisable(true);
                    balancePayout.setText(String.valueOf(betAmount));
                });
            }
        });
        thread.start();

        return cardNumber1;
    }



    public void btnHighClicked(MouseEvent actionEvent) {
        // Generate random card number for cardNumber2
        disableCollectButton();
        int betAmount = Integer.parseInt(inputBalance.getText()); // Get the bet amount from the input
        String oddsHighVal = oddsHigh.getText().toString();
        if (!oddsHighVal.isEmpty()){
            double payout = Double.parseDouble(oddsHighVal);
            int initialPayout = (int) (betAmount * payout);
            int netWin = initialPayout - betAmount;
            int currentBalance = Integer.parseInt(balancePayout.getText());
            int finalBalance = currentBalance + netWin;
            balancePayout.setText(String.valueOf(finalBalance));
        }
        int cardNumber2 = new Random().nextInt(52); // 0 to 51

        // Load image for cardRandomizer2 based on the card number
        String imagePath2 = "/cards/" + cardImages[cardNumber2];

        // Load image and set it to cardRandomizer2
        try {

            Image image2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath2)));
            cardRandomizer2.setImage(image2);
            compareCards(cardNumber1, cardNumber2, true);
            setOddsOfCard();
        } catch (NullPointerException e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void btnLowClicked(MouseEvent actionEvent) {
        // Generate random card number for cardNumber2
        disableCollectButton();
        int betAmount = Integer.parseInt(inputBalance.getText()); // Get the bet amount from the input
        String oddsLowVal = oddsLow.getText().toString();
        if (!oddsLowVal.isEmpty()){
            double payout = Double.parseDouble(oddsLowVal);
            int initialPayout = (int) (betAmount * payout);
            int netWin = initialPayout - betAmount;
            int currentBalance = Integer.parseInt(balancePayout.getText());
            int finalBalance = currentBalance + netWin;
            balancePayout.setText(String.valueOf(finalBalance));
        }
        int cardNumber2 = new Random().nextInt(52); // 0 to 51

        // Load image for cardRandomizer2 based on the card number
        String imagePath2 = "/cards/" + cardImages[cardNumber2];

        // Load image and set it to cardRandomizer2
        try {

            Image image2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath2)));
            cardRandomizer2.setImage(image2);
            // Compare the cards and print the result based on user's guess (Low)
            compareCards(cardNumber1, cardNumber2, false); // Pass cardNumber2 and the guess (Low)
            setOddsOfCard();
        } catch (NullPointerException e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void compareCards(int cardNumber1, int cardNumber2, boolean isHigh) {
        // Get the image file name for cardNumber2
        String imageFileName2 = cardImages[cardNumber2];
        // Get the image file name for cardNumber1
        String imageFileName1 = cardImages[cardNumber1];

        // Get the card value from the map for cardNumber2
        Integer cardValue2 = cardValuesMap.get(imageFileName2);
        // Get the card value from the map for cardNumber1
        Integer cardValue1 = cardValuesMap.get(imageFileName1);
        // Check if the card values are null
        if (cardValue1 != null && cardValue2 != null) {
            // Print the result based on user's guess (High or Low)
            if ((isHigh && cardValue2 >= cardValue1) || (!isHigh && cardValue2 <= cardValue1)) {
                btnBack.setDisable(true);
                btnTopUp.setDisable(true);
                winSound();
                showWinForAMoment();
                disableButtonsForTwoSeconds();
                updateCardRandomizer();
                resetCard2AfterDelay();
                setJackpot();
            } else {
                btnBack.setDisable(false);
                btnTopUp.setDisable(false);
                loseSound();
                updateUserBalanceInDatabase();
                showLoseForAMoment();
                showCardForAMoment();
                balancePayout.setText("");

            }
        } else {
            System.err.println("Error: Card values not found for one or both cards.");
        }
    }
    private void resetCard2AfterDelay() {
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
        pauseTransition.setOnFinished(event -> {
            resetCard2(); // Reset card 2 after 2 seconds
        });
        pauseTransition.play();
    }


    private void showCardForAMoment() {
        // Pause for 1 second before resetting the game
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
        pauseTransition.setOnFinished(event -> {
            resetCard2();
            resetGame();
        });
        pauseTransition.play();
        btnHigh.setDisable(true);
        btnLow.setDisable(true);
        btnSkip.setDisable(true);
        btnCollect.setDisable(true);
        betID.setDisable(false);
        disableBetButtonForTwoSeconds();
    }
    public void updateCardRandomizer() {
        // Generate a new random card number for cardRandomizer (cardNumber1)
        cardNumber1 = new Random().nextInt(52);
        String imagePath1 = "/cards/" + cardImages[cardNumber1];

        // Load and set the image for cardRandomizer
        try {
            Image newImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath1)));
            cardRandomizer.setImage(newImage);
        } catch (NullPointerException e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void showWinForAMoment() {
        showWin.setOpacity(1);

        // Pause for 2 seconds before hiding the element
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
        pauseTransition.setOnFinished(event -> {
            showWin.setOpacity(0);
        });
        pauseTransition.play();
    }

    public void showLoseForAMoment() {
        showLose.setOpacity(1);

        // Pause for 2 seconds before hiding the element
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
        pauseTransition.setOnFinished(event -> {
            showLose.setOpacity(0);
        });
        pauseTransition.play();
    }

    public void disableButtonsForTwoSeconds() {
        // Disable the buttons
        btnHigh.setDisable(true);
        btnLow.setDisable(true);
        btnSkip.setDisable(true);
        btnCollect.setDisable(true);

        // Pause for 2 seconds before enabling the buttons
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
        pauseTransition.setOnFinished(event -> {
            btnHigh.setDisable(false);
            btnLow.setDisable(false);
            if(skipCounter >= 7){
                skipCounter++;
                disableSkipButton();
            } else {
                btnSkip.setDisable(false);
            }
            btnCollect.setDisable(false);
        });
        pauseTransition.play();
    }


    public void resetCard2() {
        try {
            // Load the back image for cardRandomizer2
            InputStream inputStream = getClass().getResourceAsStream("/cards/cardbg.jpg");
            if (inputStream != null) {
                Image backImage = new Image(inputStream);
                cardRandomizer2.setImage(backImage);
            } else {
                System.err.println("Error: InputStream for back image is null.");
            }
        } catch (Exception e) {
            System.err.println("Error loading back image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void resetGame() {
        // Reset cardRandomizer to "back.jpg"
        oddsLow.setText("");
        oddsHigh.setText("");
        try {
            InputStream inputStream = getClass().getResourceAsStream("/cards/cardbg.jpg");
            if (inputStream != null) {
                Image backImage = new Image(inputStream);
                cardRandomizer.setImage(backImage);
            } else {
                System.err.println("Error: InputStream for back image is null.");
            }
        } catch (Exception e) {
            System.err.println("Error loading back image: " + e.getMessage());
            e.printStackTrace();
        }
        skipCounter = 1;
    }

    public void skipCard(ActionEvent actionEvent) {
        // Check if the skip counter has reached the limit
        cardSound();
        if (skipCounter < 7) {
            initialized(); // Call initialized method
            skipCounter++; // Increment the skip counter
        } else {
            // Disable the button if the limit is reached
            btnSkip.setDisable(true);
        }
    }
    public void disableSkipButton(){
        if (skipCounter >= 7) {
            btnSkip.setDisable(true);
        }
    }

    public void setOddsOfCard(){
        int currentCardVal = cardValuesMap.get(cardImages[cardNumber1]);
        if(currentCardVal == 1){
            oddsHigh.setText("1.10");
            oddsLow.setText("12.87");
        } else if(currentCardVal == 2){
            oddsHigh.setText("1.13");
            oddsLow.setText("6.23");
        }else if(currentCardVal == 3){
            oddsHigh.setText("1.21");
            oddsLow.setText("4.32");
        }else if(currentCardVal == 4){
            oddsHigh.setText("1.34");
            oddsLow.setText("2.21");
        }else if(currentCardVal == 5){
            oddsHigh.setText("1.37");
            oddsLow.setText("1.84");
        }else if(currentCardVal == 6){
            oddsHigh.setText("1.42");
            oddsLow.setText("1.62");
        }else if(currentCardVal == 7){
            oddsHigh.setText("1.52");
            oddsLow.setText("1.56");
        }else if(currentCardVal == 8){
            oddsHigh.setText("1.86");
            oddsLow.setText("1.42");
        }else if(currentCardVal == 9){
            oddsHigh.setText("2.12");
            oddsLow.setText("1.39");
        }else if(currentCardVal == 10){
            oddsHigh.setText("2.44");
            oddsLow.setText("1.35");
        }else if(currentCardVal == 11){
            oddsHigh.setText("4.21");
            oddsLow.setText("1.24");
        }else if(currentCardVal == 12){
            oddsHigh.setText("6.23");
            oddsLow.setText("1.12");
        }else if(currentCardVal == 13){
            oddsHigh.setText("12.85");
            oddsLow.setText("1.10");
        } else {
            oddsLow.setText("");
            oddsLow.setText("");
        }
    }

    public void collectReward(ActionEvent actionEvent) {
        // Check if the correct guess count hits the jackpot threshold
        if (correctGuessCount == 50) {
            getJackpot();
            correctGuessCount = 0;
        }

        showCollected.setOpacity(1);
        collectSound();

        // Pause for 2 seconds before hiding the element and enabling buttons
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
        pauseTransition.setOnFinished(event -> {
            btnBack.setDisable(false);
            btnTopUp.setDisable(false);
            showCollected.setOpacity(0); // Hide the element after 2 seconds

        });
        pauseTransition.play();

        // Retrieve the final balance from the balancePayout text field
        int finalBalance = Integer.parseInt(balancePayout.getText());

        // Retrieve the current balance from the balanceID label
        int currentBalance = Integer.parseInt(balanceID.getText());

        // Update the balanceID by adding the final balance
        int updatedBalance = currentBalance + finalBalance;
        balanceID.setText(String.valueOf(updatedBalance));

        // Update the userBalance field and save it to the database
        userBalance = updatedBalance;
        updateUserBalanceInDatabase();

        // Clear the balancePayout field
        balancePayout.setText("");

        // Increment the correct guess count
        correctGuessCount++;

        // Optionally show the card for a moment
        showCardForAMoment();
    }
    public void setJackpot(){
        correctGuessCount++;
        if(correctGuessCount == 50){
            int jackpot = Integer.parseInt(jackpotID.getText());
            int initialPayout = Integer.parseInt(balancePayout.getText());

            // Calculate the updated balance by multiplying the initial payout by the jackpot
            int updatedBalance = initialPayout * jackpot;

            // Update the balancePayout field with the updated balance
            balancePayout.setText(String.valueOf(updatedBalance));
            correctGuessCount = 0;
        }
    }

    public void getJackpot() {
        // Get the jackpot and current balance values
        int currentBalance = Integer.parseInt(balanceID.getText());
        int initialPayout = Integer.parseInt(balancePayout.getText());

        // Calculate the updated balance by multiplying the initial payout by the jackpot
        int updatedBalance = initialPayout + currentBalance;
        balanceID.setText(String.valueOf(updatedBalance));
    }

    public void disableBetButtonForTwoSeconds() {
        // Disable the buttons
        betID.setDisable(true);

        // Pause for 2 seconds before enabling the buttons
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
        pauseTransition.setOnFinished(event -> {
            betID.setDisable(false);
        });
        pauseTransition.play();
    }
    public void disableCollectButton(){
        if(correctGuessCount == 3){
            correctGuessCount++;
            btnCollect.setDisable(false);
            correctGuessCount= 0;
        }
    }
    public void collectSound(){
        String s = "src/main/resources/background_musics/coins_sound.mp3";
        Media h = new Media(Paths.get(s).toUri().toString());
        mediaPlayer = new MediaPlayer(h);
        mediaPlayer.play();
    }
    public void cardSound(){
        String s = "src/main/resources/background_musics/card_sound.mp3";
        Media h = new Media(Paths.get(s).toUri().toString());
        mediaPlayer = new MediaPlayer(h);
        mediaPlayer.play();
    }
    public void winSound(){
        String s = "src/main/resources/background_musics/win_sound.mp3";
        Media h = new Media(Paths.get(s).toUri().toString());
        mediaPlayer = new MediaPlayer(h);
        mediaPlayer.play();
    }
    public void loseSound(){
        String s = "src/main/resources/background_musics/lose_sound.mp3";
        Media h = new Media(Paths.get(s).toUri().toString());
        mediaPlayer = new MediaPlayer(h);
        mediaPlayer.play();
    }
    private void updateUserBalanceInDatabase() {
        SQLHelper.updateBalance(SignInController.getUserId(), userBalance);
    }
    private double retrieveUserBalance() {
        return SQLHelper.getBalance(SignInController.getUserId());
    }


    public void onGoDeposit(ActionEvent actionEvent) {
        HiloMusicController.stopMusic();
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("deposit.fxml"));

            // Create a new scene with the loaded FXML file
            Scene scene = new Scene(root);

            // Get the stage from the event source
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

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
    public void onGoBackButton(ActionEvent actionEvent) {
        HiloMusicController.stopMusic();
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

            // Create a new scene with the loaded FXML file
            Scene scene = new Scene(root);

            // Get the stage from the event source
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

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
}
