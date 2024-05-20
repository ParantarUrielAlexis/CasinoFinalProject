package com.example.casinoroyale;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class SlotMachine implements Initializable {
    @FXML
    ImageView Card1;
    @FXML
    ImageView Card2;
    @FXML
    ImageView Card3;

    @FXML
    ImageView Card4;
    @FXML
    ImageView Card5;
    @FXML
    ImageView Card6;

    @FXML
    ImageView Card7;
    @FXML
    ImageView Card8;
    @FXML
    ImageView Card9;


    public void onSpinBTN(ActionEvent actionEvent) {
        TranslateTransition[] transition = new TranslateTransition[9];
        ImageView[] cards = new ImageView[9];

        // Assign your ImageView references to the array
        cards[0] = Card1;
        cards[1] = Card2;
        cards[2] = Card3;
        cards[3] = Card4;
        cards[4] = Card5;
        cards[5] = Card6;
        cards[6] = Card7;
        cards[7] = Card8;
        cards[8] = Card9;

        for (int i = 0; i < cards.length; i++) {
            TranslateTransition translate = new TranslateTransition();
            translate.setNode(cards[i]);
            translate.setDuration(Duration.millis(1000));
            translate.setCycleCount(TranslateTransition.INDEFINITE);
            translate.setByY(250 * ((double) i / 3 + 1)); // Adjust Y translation based on row
            translate.play();

            transition[i] = translate;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

