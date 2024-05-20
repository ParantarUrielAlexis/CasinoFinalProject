package com.example.casinoroyale;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.UnaryOperator;

public class CrashController {

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
    private Timeline timeline;
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

    private final List<Label> multiplierLabels = new ArrayList<>();
    private double nextPulseThreshold = 0.5;

    @FXML
    public void initialize() {
        // Initialize user balance
        userBalance = retrieveUserBalance();

        // Use the userBalance variable as needed
        if (userBalance != -1) {
            System.out.println("User Balance: " + userBalance);
        }

        // Other initialization code
        initializeChart();
        initializeTextFormatters();
        series = new XYChart.Series<>();
        multiplierChart.getData().add(series);
        balanceLabel.setText("₱" + String.format("%.2f", userBalance));
    }

    private int retrieveUserBalance() {
        int userBalance = -1; // Default value in case of error
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement("SELECT balance FROM users WHERE id = ?")) {

            // Get the user ID from the SignInController
            int userId = SignInController.getUserId();

            // Set the user ID parameter in the SQL query
            preparedStatement.setInt(1, userId);

            // Execute the query and get the result set
            ResultSet result = preparedStatement.executeQuery();

            // Check if a result is returned and get the balance
            if (result.next()) {
                userBalance = result.getInt("balance");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userBalance;
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

            timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
                autoCashOutTF.setDisable(true);
                if (!isCrashed) {
                    updateGame();
                }
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        });
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
        timeline.stop();
        boom.play();
        cashOutMultiplier = 1.0;

        currentLabel.setText("Round Over!");
        autoCashOutTF.setDisable(false);
        restartButton.setVisible(true);
        restartButton.setDisable(false);

        updateMultiplierLabels(currentMultiplier);
        updateUserBalanceInDatabase();
    }

    private double getMultiplierIncrement(double multiplier) {
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
            if (!isCrashed && timeline != null && !cashedOut) {
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
            }
        });
    }

    public void halfBetOnAction(MouseEvent ignoredEvent) {
        // Get the current bet amount from the text field
        String betAmountText = betAmountTF.getText().trim().replace("₱", "");
        try {
            betAmount = Double.parseDouble(betAmountText);
        } catch (NumberFormatException e) {
            System.err.println("Invalid bet amount: " + betAmountText);
            return;
        }
        // Halve the bet amount
        betAmount /= 2;
        // Update the text field with the new bet amount
        betAmountTF.setText("₱" + String.format("%.2f", betAmount));
    }

    public void doubleBetOnAction(MouseEvent ignoredEvent) {
        if (userBalance <= betAmount) return;

        // Get the current bet amount from the text field
        String betAmountText = betAmountTF.getText().trim().replace("₱", "");
        try {
            betAmount = Double.parseDouble(betAmountText);
        } catch (NumberFormatException e) {
            System.err.println("Invalid bet amount: " + betAmountText);
            return;
        }

        // Double the bet amount
        betAmount *= 2;
        // Update the text field with the new bet amount
        betAmountTF.setText("₱" + String.format("%.2f", betAmount));
    }

    public void maxBetOnAction(MouseEvent ignoredEvent) {
        // Set betAmount to userBalance
        betAmount = userBalance;
        // Update the text field with the new bet amount
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
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement("UPDATE users SET balance = ? WHERE id = ?")) {

            // Get the user ID from the SignInController
            int userId = SignInController.getUserId();

            // Set the balance and user ID parameters in the SQL query
            preparedStatement.setDouble(1, userBalance);
            preparedStatement.setInt(2, userId);

            // Execute the update
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
