package com.example.casinoroyale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class RegisterController {

    @FXML
    private TextField tfUsername, tfFirstname, tfLastname, tfGender, tfEmail;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private Text showEmpty, showUsernameExists, showEmailExists, showRegistered, showFailedMessage;

    @FXML
    private CheckBox cbBtn;

    @FXML
    private TextField ShowPassword;

    @FXML
    private void initialize() {
        addPromptClearListeners(tfUsername, showEmpty);
        addPromptClearListeners(tfFirstname, showEmpty);
        addPromptClearListeners(tfLastname, showEmpty);
        addPromptClearListeners(tfGender, showEmpty);
        addPromptClearListeners(tfEmail, showEmpty);
        addPromptClearListeners(pfPassword, showEmpty);

        tfUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                showEmpty.setVisible(false);
                showUsernameExists.setVisible(false);
            }
        });
        tfFirstname.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                showEmpty.setVisible(false);
            }
        });
        tfLastname.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                showEmpty.setVisible(false);
            }
        });
        tfGender.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                showEmpty.setVisible(false);
            }
        });
        tfEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                showEmpty.setVisible(false);
                showEmailExists.setVisible(false);
            }
        });
        pfPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                showEmpty.setVisible(false);
            }
        });
    }

    private void addPromptClearListeners(TextField textField, Text promptLabel) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                promptLabel.setVisible(false);
            }
        });
    }

    private void addPromptClearListeners(PasswordField passwordField, Label promptLabel) {
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                promptLabel.setVisible(false);
            }
        });
    }


    @FXML
    private void handleRegister() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText().trim();
        String firstname = tfFirstname.getText().trim();
        String lastname = tfLastname.getText().trim();
        String gender = tfGender.getText().trim();
        String email = tfEmail.getText().trim();

        if (username.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || gender.isEmpty() || email.isEmpty()) {
            showEmpty.setOpacity(1);
            showEmpty.setVisible(true);
            return;
        }

        try (Connection connection = MySQLConnection.getConnection()) {
            String checkUsernameQuery = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement checkUsernameStatement = connection.prepareStatement(checkUsernameQuery)) {
                checkUsernameStatement.setString(1, username);
                ResultSet resultSet = checkUsernameStatement.executeQuery();
                if (resultSet.next()) {
                    showUsernameExists.setOpacity(1);
                    showUsernameExists.setVisible(true);
                    return;
                }
            }

            String checkEmailQuery = "SELECT * FROM userprofile WHERE email = ?";
            try (PreparedStatement checkEmailStatement = connection.prepareStatement(checkEmailQuery)) {
                checkEmailStatement.setString(1, email);
                ResultSet resultSet = checkEmailStatement.executeQuery();
                if (resultSet.next()) {
                    showEmailExists.setOpacity(1);
                    showEmailExists.setVisible(true);
                    return;
                }
            }

            String insertUserQuery = "INSERT INTO users (username, password, balance) VALUES (?, ?, 0)";
            try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertUserStatement.setString(1, username);
                insertUserStatement.setString(2, password);
                int rowsInserted = insertUserStatement.executeUpdate();
                if (rowsInserted > 0) {
                    ResultSet generatedKeys = insertUserStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        String insertProfileQuery = "INSERT INTO userprofile (firstname, lastname, gender, email, user_id) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement insertProfileStatement = connection.prepareStatement(insertProfileQuery)) {
                            insertProfileStatement.setString(1, firstname);
                            insertProfileStatement.setString(2, lastname);
                            insertProfileStatement.setString(3, gender);
                            insertProfileStatement.setString(4, email);
                            insertProfileStatement.setInt(5, userId);
                            int profileRowsInserted = insertProfileStatement.executeUpdate();
                            if (profileRowsInserted > 0) {
                                showRegistered.setOpacity(1);
                                showRegistered.setVisible(true);
                                tfUsername.clear();
                                pfPassword.clear();
                                tfFirstname.clear();
                                tfLastname.clear();
                                tfGender.clear();
                                tfEmail.clear();
                            } else {
                                showFailedMessage.setOpacity(1);
                                showFailedMessage.setVisible(true);
                            }
                        }
                    }
                } else {
                    showFailedMessage.setOpacity(1);
                    showFailedMessage.setVisible(true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void showPassword(){
        if(cbBtn.isSelected()){
            ShowPassword.setText(pfPassword.getText());
            ShowPassword.setVisible(true);
            pfPassword.setVisible(false);
        } else {
            pfPassword.setText(ShowPassword.getText());
            ShowPassword.setVisible(false);
            pfPassword.setVisible(true);
        }
    }

    public void handleSignIn(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signin.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("Sign In");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
