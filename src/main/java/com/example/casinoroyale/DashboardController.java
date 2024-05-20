package com.example.casinoroyale;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class DashboardController {

    @FXML
    private Label updatedLabel, deleteLabel, failedDeleteLabel, failedUpdateLabel, userSelectedLabel, insertLabel;

    @FXML
    private TableView<Admin> userTableView;

    @FXML
    private TableColumn<Admin, String> firstNameColumn;

    @FXML
    private TableColumn<Admin, String> lastNameColumn;

    @FXML
    private TableColumn<Admin, String> genderColumn;

    @FXML
    private TableColumn<Admin, String> emailColumn;

    @FXML
    private TableColumn<Admin, String> usernameColumn;

    @FXML
    private TableColumn<Admin, String> passwordColumn;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnInsert;

    @FXML
    private void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        firstNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstname()));
        lastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastname()));
        genderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        btnDelete.setOnAction(event -> {
            clearPromptMessages(updatedLabel, deleteLabel, failedDeleteLabel, failedUpdateLabel, userSelectedLabel, insertLabel);
            handleDelete();
        });

        btnUpdate.setOnAction(event -> {
            clearPromptMessages(updatedLabel, deleteLabel, failedDeleteLabel, failedUpdateLabel, userSelectedLabel, insertLabel);
            handleUpdate();
        });

        btnInsert.setOnAction(event -> {
            clearPromptMessages(updatedLabel, deleteLabel, failedDeleteLabel, failedUpdateLabel, userSelectedLabel, insertLabel);
            handleInsert();
        });
        populateTable();

    }

    private void populateTable() {
        try (Connection connection = MySQLConnection.getConnection()) {
            String selectQuery = "SELECT u.id, u.username, u.password, p.firstname, p.lastname, p.gender, p.email " +
                    "FROM users u " +
                    "INNER JOIN userprofile p ON u.id = p.user_id";
            try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Admin admin = new Admin(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            resultSet.getString("gender"),
                            resultSet.getString("email")
                    );
                    userTableView.getItems().add(admin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearPromptMessages(Label... labels) {
        for (Label label : labels) {
            label.setVisible(false);
            label.setOpacity(0);
        }
    }

    @FXML
    public void handleUpdate() {
        clearPromptMessages(updatedLabel, deleteLabel, failedDeleteLabel, failedUpdateLabel, userSelectedLabel);
        Admin selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Update Information");
            dialog.setHeaderText("Update Information for " + selectedUser.getUsername());
            dialog.setContentText("New Password:");
            Optional<String> passwordResult = dialog.showAndWait();

            if (passwordResult.isPresent()) {
                String newPassword = passwordResult.get();
                dialog.setTitle("Update Information");
                dialog.setHeaderText("Update Information for " + selectedUser.getUsername());
                dialog.setContentText("New Email Address:");
                Optional<String> emailResult = dialog.showAndWait();

                if (emailResult.isPresent()) {
                    String newEmail = emailResult.get();
                    try (Connection connection = MySQLConnection.getConnection()) {
                        connection.setAutoCommit(false);
                        String updatePasswordQuery = "UPDATE users SET password = ? WHERE id = ?";
                        try (PreparedStatement passwordStatement = connection.prepareStatement(updatePasswordQuery)) {
                            passwordStatement.setString(1, newPassword);
                            passwordStatement.setInt(2, selectedUser.getId());
                            passwordStatement.executeUpdate();
                        }
                        String updateEmailQuery = "UPDATE userprofile SET email = ? WHERE user_id = ?";
                        try (PreparedStatement emailStatement = connection.prepareStatement(updateEmailQuery)) {
                            emailStatement.setString(1, newEmail);
                            emailStatement.setInt(2, selectedUser.getId());
                            emailStatement.executeUpdate();
                        }
                        connection.commit();
                        connection.setAutoCommit(true);
                        updatedLabel.setOpacity(1);
                        updatedLabel.setVisible(true);
                        selectedUser.setPassword(newPassword);
                        selectedUser.setEmail(newEmail);
                        userTableView.refresh();
                    } catch (SQLException e) {
                        System.err.println("Error updating information: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Update canceled by the user.");
                }
            } else {
                System.out.println("Update canceled by the user.");
            }
        } else {
            userSelectedLabel.setOpacity(1);
            userSelectedLabel.setVisible(true);
        }
    }

    @FXML
    public void handleDelete() {
        clearPromptMessages(updatedLabel, deleteLabel, failedDeleteLabel, failedUpdateLabel, userSelectedLabel);
        Admin selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Deletion");
            confirmationDialog.setHeaderText("Are you sure you want to delete this user?");
            confirmationDialog.setContentText("This action cannot be undone.");

            confirmationDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try (Connection connection = MySQLConnection.getConnection()) {
                        connection.setAutoCommit(false);
                        String deleteProfileQuery = "DELETE FROM userprofile WHERE user_id = ?";
                        try (PreparedStatement profileStatement = connection.prepareStatement(deleteProfileQuery)) {
                            profileStatement.setInt(1, selectedUser.getId());
                            int profileRowsDeleted = profileStatement.executeUpdate();
                            String deleteUserQuery = "DELETE FROM users WHERE username = ?";
                            try (PreparedStatement userStatement = connection.prepareStatement(deleteUserQuery)) {
                                userStatement.setString(1, selectedUser.getUsername());
                                int userRowsDeleted = userStatement.executeUpdate();
                                if (profileRowsDeleted > 0 && userRowsDeleted > 0) {
                                    connection.commit();
                                    deleteLabel.setOpacity(1);
                                    deleteLabel.setVisible(true);
                                    userTableView.getItems().remove(selectedUser);
                                } else {
                                    connection.rollback();
                                    failedDeleteLabel.setOpacity(1);
                                    failedDeleteLabel.setVisible(true);
                                }
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println("Error deleting user: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } else {
            userSelectedLabel.setOpacity(1);
            userSelectedLabel.setVisible(true);
        }
    }


    @FXML
    public void handleLogOut(ActionEvent actionEvent) {
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

    @FXML
    public void handleInsert() {
        clearPromptMessages(updatedLabel, deleteLabel, failedDeleteLabel, failedUpdateLabel, userSelectedLabel, insertLabel);

        Dialog<Admin> dialog = new Dialog<>();
        dialog.setTitle("Insert New User");
        dialog.setHeaderText("Enter user details:");

        ButtonType insertButtonType = new ButtonType("Insert", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(insertButtonType, ButtonType.CANCEL);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField genderField = new TextField();
        genderField.setPromptText("Gender");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        dialog.getDialogPane().setContent(new VBox(10, usernameField, passwordField, firstNameField,
                lastNameField, genderField, emailField));

        Node insertButton = dialog.getDialogPane().lookupButton(insertButtonType);
        insertButton.setDisable(true);

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            insertButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == insertButtonType) {
                return new Admin(-1,
                        usernameField.getText(),
                        passwordField.getText(),
                        firstNameField.getText(),
                        lastNameField.getText(),
                        genderField.getText(),
                        emailField.getText());
            }
            return null;
        });

        Optional<Admin> result = dialog.showAndWait();
        result.ifPresent(newUser -> {
            try (Connection connection = MySQLConnection.getConnection()) {
                String insertUserQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
                try (PreparedStatement userStatement = connection.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS)) {
                    userStatement.setString(1, newUser.getUsername());
                    userStatement.setString(2, newUser.getPassword());
                    int insertedRows = userStatement.executeUpdate();

                    if (insertedRows > 0) {
                        ResultSet generatedKeys = userStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1);
                            String insertProfileQuery = "INSERT INTO userprofile (user_id, firstname, lastname, gender, email) VALUES (?, ?, ?, ?, ?)";
                            try (PreparedStatement profileStatement = connection.prepareStatement(insertProfileQuery)) {
                                profileStatement.setInt(1, userId);
                                profileStatement.setString(2, newUser.getFirstname());
                                profileStatement.setString(3, newUser.getLastname());
                                profileStatement.setString(4, newUser.getGender());
                                profileStatement.setString(5, newUser.getEmail());
                                int profileInsertedRows = profileStatement.executeUpdate();

                                if (profileInsertedRows > 0) {
                                    newUser.setId(userId);
                                    userTableView.getItems().add(newUser);
                                    insertLabel.setOpacity(1);
                                    insertLabel.setVisible(true);
                                } else {
                                    failedUpdateLabel.setOpacity(1);
                                    failedUpdateLabel.setVisible(true);
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error inserting user: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

}
