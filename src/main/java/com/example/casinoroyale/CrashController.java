package com.example.casinoroyale;


import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class CrashController {
    @FXML
    public ImageView mainMenuButton;
    @FXML
    private Label currentLabel;
    @FXML
    private TextField betAmountTF;
    @FXML
    private Button startButton;
    @FXML
    private Button restartButton;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label profitLabel;
    @FXML
    private TextField autoCashOutTF;
    @FXML
    private LineChart<Number, Number> multiplierChart;
    @FXML
    private HBox multiplierLabelsHBox;
    @FXML
    private Button cashOutButton;
    @FXML
    private Label multiplierLabel;

    private double currentMultiplier = 0.0;
    private boolean isCrashed = false;
    private final Random random = new Random();
    private double cashOutMultiplier = 0;
    private boolean cashedOut = false;
    private double userBalance = 200;
    private double betAmount;
    private XYChart.Series<Number, Number> series;
    private int time = 0;
    private final AudioClip boom = new AudioClip(Objects.requireNonNull(getClass().getResource("/background_musics/boom.mp3")).toString());
    private final AudioClip cashout = new AudioClip(Objects.requireNonNull(getClass().getResource("/background_musics/cashoutSound.mp3")).toString());
    private final AudioClip crashMusic = new AudioClip(Objects.requireNonNull(getClass().getResource("/background_musics/crash-music.mp3")).toString());
    private final AudioClip multBeep = new AudioClip(Objects.requireNonNull(getClass().getResource("/background_musics/multiplier-beep.mp3")).toString());

    private final List<Label> multiplierLabels = new ArrayList<>();
    private double nextPulseThreshold = 0.5;
    private boolean gameInProgress = false;
    private ScheduledExecutorService executorService;

    @FXML
    public void initialize() {
        crashMusic.setCycleCount(AudioClip.INDEFINITE);
        crashMusic.play();
        userBalance = retrieveUserBalance();

        if (userBalance != -1) {
            System.out.println("User Balance: " + userBalance);
        }

        initializeChart();
        initializeTextFormatters();
        series = new XYChart.Series<>();
        multiplierChart.getData().add(series);
        balanceLabel.setText("₱" + String.format("%.2f", userBalance));
    }

    private double retrieveUserBalance() {
        return SQLHelper.getBalance(SignInController.getUserId());
    }

    private void initializeChart() {
        multiplierChart.getStylesheets().add(Objects.requireNonNull(getClass().getResource("chart.css")).toExternalForm());
        multiplierChart.setStyle("-fx-background-color: transparent;");
        multiplierChart.setHorizontalGridLinesVisible(false);
        multiplierChart.setVerticalGridLinesVisible(false);
    }

    private void initializeTextFormatters() {
        UnaryOperator<TextFormatter.Change> decimalFilter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("₱?\\d*(\\.\\d{0,2})?") ? change : null;
        };

        betAmountTF.setTextFormatter(getStringTextFormatter(decimalFilter));
        autoCashOutTF.setTextFormatter(new TextFormatter<>(getDecimalFilter()));
    }

    @NotNull
    private static TextFormatter<String> getStringTextFormatter(UnaryOperator<TextFormatter.Change> decimalFilter) {
        UnaryOperator<TextFormatter.Change> pesoSignFilter = change -> {
            if (change.isAdded() && !change.getControlNewText().contains("₱")) {
                change.setText("₱" + change.getText());
                change.setCaretPosition(change.getCaretPosition() + 1);
                change.setAnchor(change.getAnchor() + 1);
            }
            return change;
        };

        return new TextFormatter<>(change -> {
            change = pesoSignFilter.apply(change);
            return decimalFilter.apply(change);
        });
    }

    @NotNull
    private static UnaryOperator<TextFormatter.Change> getDecimalFilter() {
        return change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*(\\.\\d{0,2})?") ? change : null;
        };
    }

    @FXML
    public void startGameOnAction(ActionEvent ignoredEvent) {
        pulseButton(startButton, () -> {
            String betAmountText = betAmountTF.getText().trim().replaceAll("[^\\d.]", "");
            try {
                betAmount = Double.parseDouble(betAmountText);
            } catch (NumberFormatException e) {
                System.err.println("Invalid bet amount: " + betAmountText);
                return;
            }

            if (userBalance < betAmount || betAmount < 100) {
                System.err.println("Insufficient balance or bet amount too low.");
                return;
            }

            crashMusic.stop();
            userBalance -= betAmount;
            balanceLabel.setText("₱" + String.format("%.2f", userBalance));
            updateUserBalanceInDatabase();

            restartButton.setDisable(true);
            restartButton.setVisible(false);
            cashOutButton.setVisible(true);
            cashOutButton.setDisable(false);

            currentLabel.setText("Current Payout");
            time = 0;
            series.getData().clear();
            currentMultiplier = 0.0;

            gameInProgress = true;
            mainMenuButton.setDisable(true);

            startGameLoop();
        });
    }

    private void startGameLoop() {
        executorService = Executors.newScheduledThreadPool(1);

        Runnable gameTask = () -> {
            if (!isCrashed) {
                Platform.runLater(this::updateGame);
            }
        };

        executorService.scheduleAtFixedRate(gameTask, 0, 50, TimeUnit.MILLISECONDS);
    }

    private void updateGame() {
        currentMultiplier += getMultiplierIncrement(currentMultiplier);
        multiplierLabel.setText(String.format("%.2fx", currentMultiplier));
        profitLabel.setText(String.format("₱%.2f", calculateProfit(currentMultiplier)));

        series.getData().add(new XYChart.Data<>(time++, currentMultiplier));
        checkAutoCashOut();

        if (random.nextDouble() < getCrashProbability(currentMultiplier)) {
            handleCrash();
        } else if (currentMultiplier >= nextPulseThreshold) {
            pulseMultiplierLabel();
            nextPulseThreshold += (currentMultiplier >= 5.0) ? 1.0 : 0.5;
        }
    }

    private void checkAutoCashOut() {
        String autoCashOutText = autoCashOutTF.getText().trim();
        if (!autoCashOutText.isEmpty()) {
            try {
                double autoCashOutMultiplier = Double.parseDouble(autoCashOutText);
                if (currentMultiplier >= autoCashOutMultiplier) {
                    cashOutOnAction(null);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid auto cashout multiplier: " + autoCashOutText);
            }
        }
    }

    private void handleCrash() {
        isCrashed = true;
        if (executorService != null) {
            executorService.shutdown();
        }
        crashMusic.stop();
        boom.play();
        cashOutMultiplier = 1.0;

        currentLabel.setText("Round Over!");
        autoCashOutTF.setDisable(false);
        restartButton.setVisible(true);
        restartButton.setDisable(false);

        gameInProgress = false;
        mainMenuButton.setDisable(false);

        updateMultiplierLabels(currentMultiplier);
        updateUserBalanceInDatabase();

        crashMusic.setCycleCount(AudioClip.INDEFINITE);
        crashMusic.play();
    }

    private double getMultiplierIncrement(double multiplier) {
        multBeep.play();

        if (multiplier >= 8) return 0.1;
        if (multiplier >= 6) return 0.09;
        if (multiplier >= 4.5) return 0.09;
        if (multiplier >= 4.0) return 0.08;
        if (multiplier >= 3.5) return 0.05;
        if (multiplier >= 3.0) return 0.03;
        if (multiplier >= 2.0) return 0.01;
        if (multiplier >= 1.5) return 0.009;
        if (multiplier >= 1.0) return 0.008;
        if (multiplier >= 0.5) return 0.006;
        return 0.005;
    }

    private double calculateProfit(double multiplier) {
        return betAmount * (multiplier - 1);
    }

    private double getCrashProbability(double multiplier) {
        double baseProbability = 0.001;
        double multiplierFactor = 0.005;
        return baseProbability + (multiplier - 1.0) * multiplierFactor;
    }

    private void updateMultiplierLabels(double multiplier) {
        Label label = new Label(String.format("%.2fx", multiplier));
        label.setTextFill(multiplier >= 1.5 ? Color.GREEN : Color.RED);
        label.setFont(Font.font("Poppins", FontWeight.BOLD, FontPosture.REGULAR, 20));

        multiplierLabels.add(label);
        multiplierLabelsHBox.getChildren().add(label);

        if (multiplierLabels.size() > 23) {
            List<Label> toRemove = new ArrayList<>(multiplierLabels.subList(0, multiplierLabels.size() / 2));
            multiplierLabels.removeAll(toRemove);
            multiplierLabelsHBox.getChildren().removeAll(toRemove);
        }
    }

    @FXML
    public void restartGameOnAction(ActionEvent ignoredEvent) {
        restartGame();
    }

    public void restartGame() {
        if (betAmount < 100) return;

        currentMultiplier = 0.0;
        isCrashed = false;
        cashedOut = false;
        autoCashOutTF.setDisable(false);
        nextPulseThreshold = 0.5;

        startGameOnAction(null);
    }

    @FXML
    public void cashOutOnAction(ActionEvent ignoredEvent) {
        pulseButton(cashOutButton, () -> {
            if (!isCrashed && executorService != null && !cashedOut) {
                cashout.play();
                cashOutMultiplier = currentMultiplier;

                double winAmount = betAmount * cashOutMultiplier;
                System.out.println("Cashed out at multiplier: " + cashOutMultiplier);
                System.out.println("Your winnings: " + winAmount);

                userBalance += winAmount;
                balanceLabel.setText("₱" + String.format("%.2f", userBalance));

                series.getData().add(createCashOutDataPoint(time, currentMultiplier));

                isCrashed = false;
                cashedOut = true;
                autoCashOutTF.setDisable(false);

                updateUserBalanceInDatabase();

                crashMusic.setCycleCount(AudioClip.INDEFINITE);
                crashMusic.play();
            }
        });
    }

    public void halfBetOnAction(MouseEvent ignoredEvent) {
        String betAmountText = betAmountTF.getText().trim().replace("₱", "");
        try {
            betAmount = Double.parseDouble(betAmountText);
        } catch (NumberFormatException e) {
            System.err.println("Invalid bet amount: " + betAmountText);
            return;
        }
        betAmount /= 2;
        betAmountTF.setText("₱" + String.format("%.2f", betAmount));
    }

    public void doubleBetOnAction(MouseEvent ignoredEvent) {
        if (userBalance <= betAmount) return;

        String betAmountText = betAmountTF.getText().trim().replace("₱", "");
        try {
            betAmount = Double.parseDouble(betAmountText);
        } catch (NumberFormatException e) {
            System.err.println("Invalid bet amount: " + betAmountText);
            return;
        }

        betAmount *= 2;
        betAmountTF.setText("₱" + String.format("%.2f", betAmount));
    }

    public void maxBetOnAction(MouseEvent ignoredEvent) {
        betAmount = userBalance;
        betAmountTF.setText("₱" + String.format("%.2f", betAmount));
    }

    public void removeTextOnAction(MouseEvent ignoredEvent) {
        autoCashOutTF.setText("");
    }

    private void pulseButton(Button button, Runnable action) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setByX(0.3);
        st.setByY(0.3);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.setOnFinished(e -> {
            action.run();
            button.setDisable(true);
            button.setVisible(false);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        st.play();
    }

    private void pulseMultiplierLabel() {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), multiplierLabel);
        st.setByX(0.5);
        st.setByY(0.5);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    private XYChart.Data<Number, Number> createCashOutDataPoint(int x, double y) {
        XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, y);
        Circle circle = new Circle(10, Color.web("#5D4D74"));
        circle.setStyle("-fx-stroke-width: 5;");
        dataPoint.setNode(circle);

        dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                newNode.setStyle("-fx-background-color: transparent;");
            }
        });

        return dataPoint;
    }

    private void updateUserBalanceInDatabase() {
        SQLHelper.updateBalance(SignInController.getUserId(), userBalance);
    }

    public void goToMain(MouseEvent event) {
        if (gameInProgress) {
            return;
        }

        crashMusic.stop();

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
            double dpiScale = ScreenHelper.getDPIScale();
            Scale scale = new Scale(dpiScale, dpiScale);
            root.getTransforms().add(scale);
            Scene scene = new Scene(root);
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            primaryStage.setTitle("Sign In");
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
