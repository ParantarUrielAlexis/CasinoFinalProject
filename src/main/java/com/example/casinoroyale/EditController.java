package com.example.casinoroyale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditController {

    @FXML
    private TextField tfPrevUsername;

    @FXML
    private PasswordField pfPrevPassword;

    @FXML
    private TextField tfNewUsername;

    @FXML
    private PasswordField pfNewPassword;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleUpdate() {
        String prevUsername = tfPrevUsername.getText();
        String prevPassword = pfPrevPassword.getText();
        String newUsername = tfNewUsername.getText();
        String newPassword = pfNewPassword.getText();


        if (prevUsername.isEmpty() || prevPassword.isEmpty() || newUsername.isEmpty() || newPassword.isEmpty()) {
            messageLabel.setText("Please Enter Username & Password.");
            return;
        }

        try (Connection connection = MySQLConnection.getConnection()) {
            String updateQuery = "UPDATE users SET username = ?, password = ? WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, newUsername);
                statement.setString(2, newPassword);
                statement.setString(3, prevUsername);
                statement.setString(4, prevPassword);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    messageLabel.setText("User information updated successfully!.");

                    tfPrevUsername.clear();
                    pfPrevPassword.clear();
                    tfNewUsername.clear();
                    pfNewPassword.clear();
                } else {
                    messageLabel.setText("Failed to update user information. Please check previous username and password.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void onClickGoBack(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
