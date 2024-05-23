package com.example.casinoroyale;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class BlackjackGame extends Application {
    @FXML
    public ImageView ivBackground;
    @FXML
    public Button btnHit, btnStay, btnPlayAgain, btnBet;
    @FXML
    public HBox hbButtons;
    @FXML
    public ImageView ivHiddenCard;
    @FXML
    public HBox hbDealerHand;
    @FXML
    public ImageView ivDealerHand1, ivDealerHand2, ivDealerHand3, ivDealerHand4, ivDealerHand5, ivDealerHand6;
    @FXML
    public ImageView ivPlayerHand1, ivPlayerHand2, ivPlayerHand3, ivPlayerHand4, ivPlayerHand5, ivPlayerHand6, ivPlayerHand7;
    @FXML
    private TextField tfBet;
    @FXML
    private Label labelStatus, labelPlayer, labelDealer, labelBalance;


    private static class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) { //A J Q K
                if (value.equals("A")) {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value); //2-10
        }

        public boolean isAce() {
            return Objects.equals(value, "A");
        }

        public String getImagePath() {
            return "/BlackJack/" + this + ".png";
        }
    }
    ArrayList<Card> deck;
    Random random = new Random(); //shuffle deck

    //dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    //player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;
    int balance = 1000;
    int currentBet = 0;

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BlackjackGame.class.getResource("blackjack_game.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Black Jack");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

    @FXML
    public void btnHitOnAction(){
        if (playerHand.size() < 7) { // Ensure there is space for another card
            Card card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
            // Update the player's hand images
            ImageView[] playerHandViews = {ivPlayerHand1, ivPlayerHand2, ivPlayerHand3, ivPlayerHand4, ivPlayerHand5, ivPlayerHand6, ivPlayerHand7};
            Image cardImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getImagePath())));
            playerHandViews[playerHand.size() - 1].setImage(cardImg);
            if (reducePlayerAce() > 21) { //A + 2 + J --> 1 + 2 + J
                labelPlayer.setText("Player's Hand: " + reducePlayerAce());
                applyTypewriterEffect(labelStatus, "You Exceeded.");
                btnHit.setDisable(true);
            }else{
                labelPlayer.setText("Player's Hand: " + reducePlayerAce());
                applyTypewriterEffect(labelStatus, "Do you want to hit another card?");
            }
        }

    }

    @FXML
    public void btnStayOnAction(){
        btnHit.setDisable(true);
        btnStay.setDisable(true);
        Image hiddenCardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(hiddenCard.getImagePath())));
        ivHiddenCard.setImage(hiddenCardImage);

        if(dealerHand.size() < 7){
            while(dealerSum < 17){
                Card card = deck.remove(deck.size() - 1);
                dealerSum += card.getValue();
                dealerAceCount += card.isAce() ? 1 : 0;
                dealerHand.add(card);

                ImageView[] dealerImageViews = {ivDealerHand1, ivDealerHand2, ivDealerHand3, ivDealerHand4, ivDealerHand5, ivDealerHand6};
                for (int i = 0; i < dealerHand.size() && i < dealerImageViews.length; i++) {
                    Image cardImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getImagePath())));
                    dealerImageViews[dealerHand.size() - 1].setImage(cardImg);
                }
            }
        }

        dealerSum = reduceDealerAce();
        playerSum = reducePlayerAce();
        System.out.println("STAY: ");
        System.out.println(dealerSum);
        System.out.println(playerSum);

        String message = "";
        if (playerSum > 21) {
            message = "You Lose!";
            labelDealer.setText("Dealer's Hand: " + reduceDealerAce());
        }
        else if (dealerSum > 21) {
            message = "You Win!";
            balance += currentBet * 2;
            labelDealer.setText("Dealer's Hand: " + reduceDealerAce());

        }
        //both you and dealer <= 21
        else if (playerSum == dealerSum) {
            message = "Tie!";
            labelDealer.setText("Dealer's Hand: " + reduceDealerAce());
        }
        else if (playerSum > dealerSum) {
            message = "You Win!";
            balance += currentBet * 2;
            labelDealer.setText("Dealer's Hand: " + reduceDealerAce());
        }
        else {
            message = "You Lose!";
            labelDealer.setText("Dealer's Hand: " + reduceDealerAce());
        }

        applyTypewriterEffect(labelStatus, message + " Click Play Again to start a new game.");
        labelBalance.setText("" + balance);
        btnPlayAgain.setVisible(true);
    }
    @FXML
    public void btnBetOnAction() {
        String betText = tfBet.getText();
        int betAmount = Integer.parseInt(betText);
        if (!betText.isEmpty() && betText.matches("\\d+")) { // Check if the text is not null, not empty, and is a number
            try {
                labelBalance.setText(String.valueOf(balance));
                if(balance>=betAmount) {
                    if (betAmount > 10) {
                        currentBet = betAmount;
                        balance -= betAmount;
                        labelBalance.setText(String.valueOf(balance));
                        Image background = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/BlackJack/BACK.png")));
                        ivHiddenCard.setImage(background);
                        applyTypewriterEffect(labelStatus, "Do you want to hit another card?");

                        startGame(); // Start the game if the bet is valid

                        // Draw dealer's hand
                        ImageView[] dealerImageViews = {ivDealerHand1, ivDealerHand2, ivDealerHand3, ivDealerHand4, ivDealerHand5, ivDealerHand6};
                        for (int i = 0; i < dealerHand.size() && i < dealerImageViews.length; i++) {
                            Card card = dealerHand.get(i);
                            Image cardImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getImagePath())));
                            dealerImageViews[i].setImage(cardImg);
                        }
                        // Draw player's hand
                        ImageView[] playerHandViews = {ivPlayerHand1, ivPlayerHand2, ivPlayerHand3, ivPlayerHand4, ivPlayerHand5, ivPlayerHand6, ivPlayerHand7};
                        for (int i = 0; i < playerHand.size() && i < playerHandViews.length; i++) {
                            Card card = playerHand.get(i);
                            Image cardImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getImagePath())));
                            playerHandViews[i].setImage(cardImg);
                        }
                        labelPlayer.setText("Player's Hand: " + reducePlayerAce());
                        labelDealer.setText("Dealer's Hand: ??");
                        labelPlayer.setOpacity(1);
                        labelDealer.setOpacity(1);
                        //                if(reduceDealerAce() == 21){
                        //                    btnHit.setDisable(true);
                        //                }
                        btnHit.setDisable(false);
                        btnStay.setDisable(false);
                        btnPlayAgain.setVisible(false);
                        tfBet.setText("");
                        tfBet.setPromptText("Enter Bet Amount:");
                        btnBet.setDisable(true);
                        tfBet.setDisable(true);
                    } else {
                        // Handle invalid bet amount (e.g., zero or negative)
                        applyTypewriterEffect(labelStatus, "Bet amount must be greater than 10.");
                    }
                }else{
                    applyTypewriterEffect(labelStatus, "Bet amount must be greater than your balance.");
                }
            } catch (NullPointerException e) {
                System.err.println("Error loading image: " + e.getMessage());

            }catch (NumberFormatException e) {
                // Handle parsing error (unlikely due to regex check but safe to include)
                applyTypewriterEffect(labelStatus, "Invalid Amount");
            }
        } else {
            // Handle invalid input case (e.g., show an error message to the user)
            applyTypewriterEffect(labelStatus, "Invalid Amount");
        }

    }

    @FXML
    public void btnPlayAgainOnAction() {
        // Reset the game state
        applyTypewriterEffect(labelStatus, "Enter Bet amount. ");
        tfBet.setDisable(false);
        tfBet.setText("");
        tfBet.setPromptText("Enter Bet Amount:");
        ivHiddenCard.setImage(null);
        ivDealerHand1.setImage(null);
        ivDealerHand2.setImage(null);
        ivDealerHand3.setImage(null);
        ivDealerHand4.setImage(null);
        ivDealerHand5.setImage(null);
        ivDealerHand6.setImage(null);
        ivPlayerHand1.setImage(null);
        ivPlayerHand2.setImage(null);
        ivPlayerHand3.setImage(null);
        ivPlayerHand4.setImage(null);
        ivPlayerHand5.setImage(null);
        ivPlayerHand6.setImage(null);
        ivPlayerHand7.setImage(null);
        btnPlayAgain.setVisible(false);
        btnHit.setDisable(true);
        btnStay.setDisable(true);
        btnBet.setDisable(false);
        tfBet.setDisable(false);
        labelPlayer.setText("Player's Hand: ");
        labelDealer.setText("Dealer's Hand: ");
        labelPlayer.setOpacity(0);
        labelDealer.setOpacity(0);
    }

    //METHOD CALLS
    public void startGame() {
        //deck
        buildDeck();
        shuffleDeck();

        //dealer
        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size()-1); //remove card at last index
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size()-1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("DEALER:");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);


        //player
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size()-1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        System.out.println("PLAYER: ");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);
    }

    public void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                Card card = new Card(value, type);
                deck.add(card);
            }
        }

        System.out.println("BUILD DECK:");
        System.out.println(deck);
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }

        System.out.println("AFTER SHUFFLE");
        System.out.println(deck);
    }
    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }

    // ANIMATIONS FOR TEXTS
    private void applyFadeTransition(Label label, String message) {
        label.setText(message);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), label);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(1);
        fadeTransition.setAutoReverse(false);
        fadeTransition.play();

    }

    private void applyTypewriterEffect(Label label, String message) {
        label.setText(""); // Clear the label's text
        final int[] index = {0};
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(20), // Adjust the speed of typing here
                event -> {
                    if (index[0] < message.length()) {
                        label.setText(label.getText() + message.charAt(index[0]));
                        index[0]++;
                    } else {
                        timeline.stop();
                    }
                }
        );

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

}
