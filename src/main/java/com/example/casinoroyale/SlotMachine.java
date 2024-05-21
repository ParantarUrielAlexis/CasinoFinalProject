package com.example.casinoroyale;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
    ImageView State;


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

        Collections.shuffle(imagePaths1);
        Collections.shuffle(imagePaths2);
        Collections.shuffle(imagePaths3);

        Card1.setImage(new Image(imagePaths1.get(0)));
        Card2.setImage(new Image(imagePaths2.get(0)));
        Card3.setImage(new Image(imagePaths3.get(0)));

        if (checkAllSame()) {
            // Jackpot! Display jackpot message
            State.setImage(new Image("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\Jackpot.gif"));
            System.out.println("Jackpot!");  // Replace with your UI update for Jackpot
        } else if (checkTwoSame()) {
            // Two of the same! Display win 100 message
            State.setImage(new Image("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\YouWin.gif"));
            System.out.println("Win 100!");  // Replace with your UI update for win 100
        } else {
            // No win
            State.setImage(new Image("C:\\Users\\kent espia\\IdeaProjects\\CasinoFinalProject\\src\\main\\resources\\SlotMachine\\YouLost.gif"));
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

    }


}
