package com.example.casinoroyale;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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

        promptCheck(tfUsername, showUsernameExists, tfFirstname);
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
        promptCheck(tfEmail, showEmailExists, pfPassword);
    }

    private void promptCheck(TextField tfUsername, Text showUsernameExists, TextField tfFirstname) {
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
    }

    private void addPromptClearListeners(TextField textField, Text promptLabel) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
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

        try (Connection connection = SQLHelper.getConnection()) {
            if (isUsernameExists(connection, username)) {
                showUsernameExists.setOpacity(1);
                showUsernameExists.setVisible(true);
                return;
            }

            if (isEmailExists(connection, email)) {
                showEmailExists.setOpacity(1);
                showEmailExists.setVisible(true);
                return;
            }

            int userId = SQLHelper.insertUser(connection, username, password);
            if (userId > 0 && SQLHelper.insertUserProfile(connection, userId, firstname, lastname, gender, email)) {
                showRegistered.setOpacity(1);
                showRegistered.setVisible(true);
                clearForm();
            } else {
                showFailedMessage.setOpacity(1);
                showFailedMessage.setVisible(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isUsernameExists(Connection connection, String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    private boolean isEmailExists(Connection connection, String email) throws SQLException {
        String query = "SELECT * FROM userprofile WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    private void clearForm() {
        tfUsername.clear();
        pfPassword.clear();
        tfFirstname.clear();
        tfLastname.clear();
        tfGender.clear();
        tfEmail.clear();
    }

    public void showPassword() {
        checkBoxSelect(cbBtn, ShowPassword, pfPassword);
    }

    static void checkBoxSelect(CheckBox cbBtn, TextField showPassword, PasswordField pfPassword) {
        if (cbBtn.isSelected()) {
            showPassword.setText(pfPassword.getText());
            showPassword.setVisible(true);
            pfPassword.setVisible(false);
        } else {
            pfPassword.setText(showPassword.getText());
            showPassword.setVisible(false);
            pfPassword.setVisible(true);
        }
    }

    public void handleSignIn(MouseEvent actionEvent) {
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
