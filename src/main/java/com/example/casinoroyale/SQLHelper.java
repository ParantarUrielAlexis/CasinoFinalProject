package com.example.casinoroyale;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLHelper {

    private static final String URL = "jdbc:mysql://localhost:3306/casino_royaledb";
    private static final String USERNAME = "lance";
    private static final String PASSWORD = "lance123";

    public static Connection getConnection() {
        return getConnection(URL, USERNAME, PASSWORD);
    }

    @Nullable
    static Connection getConnection(String url, String username, String password) {
        Connection c = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static double getBalance(int userId) {
        double userBalance = -1; // Default value in case of error
        try (Connection c = getConnection();
             PreparedStatement preparedStatement = c.prepareStatement("SELECT balance FROM users WHERE id = ?")) {

            preparedStatement.setInt(1, userId);
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                userBalance = result.getDouble("balance");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userBalance;
    }

    public static void updateBalance(int userId, double userBalance) {
        try (Connection c = getConnection();
             PreparedStatement preparedStatement = c.prepareStatement("UPDATE users SET balance = ? WHERE id = ?")) {

            preparedStatement.setDouble(1, userBalance);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable() {
        try (Connection c = getConnection();
             Statement statement = c.createStatement()) {

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL," +
                    "password VARCHAR(50) NOT NULL," +
                    "balance DOUBLE NOT NULL)";

            String createUserProfileTable = "CREATE TABLE IF NOT EXISTS userprofile (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "firstname VARCHAR(50) NOT NULL," +
                    "lastname VARCHAR(50) NOT NULL," +
                    "gender VARCHAR(50) NOT NULL," +
                    "email VARCHAR(50) NOT NULL," +
                    "user_id INT NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))";

            statement.execute(createUsersTable);
            statement.execute(createUserProfileTable);

            System.out.println("Tables Created Successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static int insertUser(Connection connection, String username, String password) throws SQLException {
        String query = "INSERT INTO users (username, password, balance) VALUES (?, ?, 0)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, password);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    static boolean insertUserProfile(Connection connection, int userId, String firstname, String lastname, String gender, String email) throws SQLException {
        String query = "INSERT INTO userprofile (firstname, lastname, gender, email, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstname);
            statement.setString(2, lastname);
            statement.setString(3, gender);
            statement.setString(4, email);
            statement.setInt(5, userId);
            return statement.executeUpdate() > 0;
        }
    }

    public static void goToSignIn(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(SQLHelper.class.getResource("signin.fxml"));
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
