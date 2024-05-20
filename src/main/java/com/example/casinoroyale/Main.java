package com.example.casinoroyale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("signin.fxml"));

        Scene scene = new Scene(root);


        primaryStage.setTitle("Sign In");

        primaryStage.setScene(scene);

        primaryStage.show();
    }
    public static void createTable() {
        try (Connection c = MySQLConnection.getConnection();
             Statement statement = c.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL," +
                    "password VARCHAR(50) NOT NULL," +
                    "balance DOUBLE NOT NULL)";
            String createTableQuery2 = "CREATE TABLE IF NOT EXISTS userprofile (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "firstname VARCHAR(50) NOT NULL," +
                    "lastname VARCHAR(50) NOT NULL," +
                    "gender VARCHAR(50) NOT NULL," +
                    "email VARCHAR(50) NOT NULL," +
                    "user_id INT NOT NULL," +  // Foreign key column
                    "FOREIGN KEY (user_id) REFERENCES users(id))";
            statement.execute(createTableQuery);
            statement.execute(createTableQuery2);
            System.out.println("Tables Created Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        createTable();
        launch();
    }
}
