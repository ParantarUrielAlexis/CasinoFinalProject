package com.example.casinoroyale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepositController {

    @FXML
    public Text balanceID;

    @FXML
    public TextField deposit_id, withdraw_id;

    @FXML
    public Button btnDeposit, btnWithdraw;

    private int userId;

    public void initialize(int userId) {
        this.userId = userId;
        updateBalance();
    }

    public void goMainMenu(ActionEvent event) {
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

            // Create a new scene with the loaded FXML file
            Scene scene = new Scene(root);

            // Get the stage from the event source
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene on the primary stage
            primaryStage.setTitle("Sign In");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            // Handle any IOException that occurs during loading
            e.printStackTrace();
            // You might want to show an error message to the user here
        }
    }

    public void depositMoney(ActionEvent actionEvent) {
        // Get the deposited amount
        double depositAmount = Double.parseDouble(deposit_id.getText());

        // Connect to the database
        try (Connection connection = MySQLConnection.getConnection()) {
            // Update the user's balance in the database
            String updateBalanceQuery = "UPDATE users SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement updateBalanceStatement = connection.prepareStatement(updateBalanceQuery)) {
                updateBalanceStatement.setDouble(1, depositAmount);
                updateBalanceStatement.setInt(2, userId);
                int rowsUpdated = updateBalanceStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    // Balance updated successfully
                    updateBalance();
                } else {
                    // No user with the specified ID found
                    System.out.println("User not found");
                    // You might want to show an error message to the user here
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // You might want to show an error message to the user here
        }
    }

    public void handleDeposit(ActionEvent event) {
        depositMoney(event);
    }

    public void withdrawMoney(ActionEvent actionEvent) {
        // Get the withdrawal amount
        double withdrawAmount = Double.parseDouble(withdraw_id.getText());

        // Connect to the database
        try (Connection connection = MySQLConnection.getConnection()) {
            // Check if the withdrawal amount is valid
            if (withdrawAmount <= 0) {
                System.out.println("Invalid withdrawal amount");
                // You might want to show an error message to the user here
                return;
            }

            // Retrieve the current balance from the database
            String getBalanceQuery = "SELECT balance FROM users WHERE id = ?";
            try (PreparedStatement getBalanceStatement = connection.prepareStatement(getBalanceQuery)) {
                getBalanceStatement.setInt(1, userId);
                ResultSet resultSet = getBalanceStatement.executeQuery();
                if (resultSet.next()) {
                    double currentBalance = resultSet.getDouble("balance");

                    // Check if the user has sufficient balance for withdrawal
                    if (currentBalance >= withdrawAmount) {
                        // Update the user's balance in the database
                        String updateBalanceQuery = "UPDATE users SET balance = balance - ? WHERE id = ?";
                        try (PreparedStatement updateBalanceStatement = connection.prepareStatement(updateBalanceQuery)) {
                            updateBalanceStatement.setDouble(1, withdrawAmount);
                            updateBalanceStatement.setInt(2, userId);
                            int rowsUpdated = updateBalanceStatement.executeUpdate();

                            if (rowsUpdated > 0) {
                                // Balance updated successfully
                                updateBalance();
                            } else {
                                // No user with the specified ID found
                                System.out.println("User not found");
                                // You might want to show an error message to the user here
                            }
                        }
                    } else {
                        // Insufficient balance
                        System.out.println("Insufficient balance for withdrawal");
                        // You might want to show an error message to the user here
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // You might want to show an error message to the user here
        }
    }

    public void handleWithdraw(ActionEvent actionEvent) {
        withdrawMoney(actionEvent);
    }

    private void updateBalance() {
        try (Connection connection = MySQLConnection.getConnection()) {
            // Retrieve the current balance from the database
            String getBalanceQuery = "SELECT balance FROM users WHERE id = ?";
            try (PreparedStatement getBalanceStatement = connection.prepareStatement(getBalanceQuery)) {
                getBalanceStatement.setInt(1, userId);
                ResultSet resultSet = getBalanceStatement.executeQuery();
                if (resultSet.next()) {
                    double updatedBalance = resultSet.getDouble("balance");

                    // Update the balance displayed on the UI
                    balanceID.setText(String.valueOf(updatedBalance));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // You might want to show an error message to the user here
        }
    }

}
