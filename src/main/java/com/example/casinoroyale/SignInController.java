package com.example.casinoroyale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignInController {

    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private Text showIncorrect, showEmptyFields;

    @FXML
    private CheckBox cbBtn;

    @FXML
    private TextField ShowPassword;

    // used to get the userID so we can get balance
    static int userID;

    public static int getUserId() {
        return userID;
    }

    @FXML
    protected void initialize() {
        tfUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                showEmptyFields.setVisible(false);
                showIncorrect.setVisible(false);
            }
        });

        pfPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                showEmptyFields.setVisible(false);
                showIncorrect.setVisible(false);
            }
        });
        tfUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                showEmptyFields.setVisible(false);
                showIncorrect.setVisible(false);
            }
        });

        pfPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                showEmptyFields.setVisible(false);
                showIncorrect.setVisible(false);
            }
        });
    }

    @FXML
    protected void handleSignIn(ActionEvent event) {
        String username = tfUsername.getText();
        String password = pfPassword.getText();

        if (username.equals("admin") && password.equals("123456")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) tfUsername.getScene().getWindow();
                stage.setTitle("Admin Dashboard");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            showEmptyFields.setOpacity(1);
            showEmptyFields.setVisible(true);
            return;
        }

        try (Connection c = MySQLConnection.getConnection();
             Statement statement = c.createStatement()) {
            String selectQuery = "SELECT * FROM users";
            ResultSet result = statement.executeQuery(selectQuery);
            while (result.next()) {
                String dbUsername = result.getString("username");
                String dbPassword = result.getString("password");
                if (username.equals(dbUsername) && password.equals(dbPassword)) {
                    userID = result.getInt("id");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                    try {
                        Parent root = loader.load();
                        HelloController helloController = loader.getController();
                        helloController.initialize(userID);
                        Scene scene = new Scene(root);
                        Stage stage = (Stage) tfUsername.getScene().getWindow();
                        stage.setScene(scene);
                        stage.setTitle("User Area");
                        stage.show();
                        return;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            showIncorrect.setOpacity(1);
            showIncorrect.setVisible(true);
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

    public void handleRegister(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("Registration");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
