package com.example.casinoroyale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("signin.fxml")));

        // Calculate the DPI scaling factor
        double dpiScale = ScreenHelper.getDPIScale();

        // Apply the scaling transformation
        Scale scale = new Scale(dpiScale, dpiScale);
        root.getTransforms().add(scale);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Sign In");
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        SQLHelper.createTable();
        launch();
    }
}