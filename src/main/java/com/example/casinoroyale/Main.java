package com.example.casinoroyale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("signin.fxml")));

        // Calculate the DPI scaling factor
        double dpiScale = DPIUtil.getDPIScale();

        // Apply the scaling transformation
        Scale scale = new Scale(dpiScale, dpiScale);
        root.getTransforms().add(scale);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Sign In");
        primaryStage.setFullScreen(true);
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

    public static class DPIUtil {
        public static double getDPIScale() {
            double dpi = Toolkit.getDefaultToolkit().getScreenResolution();
            double defaultDPI = 96.0; // Default DPI for most systems

            if (dpi == 120) {
                defaultDPI = 150;
            }
            System.out.println(dpi);
            return dpi / defaultDPI;
        }
    }

}
