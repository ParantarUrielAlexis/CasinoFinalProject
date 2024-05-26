package com.example.casinoroyale;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class HelloController {
    private static double originalWidth = 462.0;
    private static double originalHeight = 701.0;
    @FXML
    public Label helloUser;
    @FXML
    public Label balance;
    @FXML
    public Button btnBlackJack;
    private int userId;

    public void initialize() {
        this.userId = SignInController.getUserId();
        fetchAndSetUsername();
        fetchAndSetBalance();
    }

    private void fetchAndSetUsername() {
        try (Connection connection = SQLHelper.getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT firstname FROM userprofile WHERE id = " + userId;
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                String username = resultSet.getString("firstname");
                username = capitalizeFirstLetter(username);
                setHelloUser(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors appropriately
        }
    }

    private void fetchAndSetBalance() {
        try (Connection connection = SQLHelper.getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT balance FROM users WHERE id = " + userId;
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                double balanceValue = resultSet.getDouble("balance");
                setBalance(balanceValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors appropriately
        }
    }

    private void setBalance(double balanceValue) {
        balance.setText("Balance: ₱" + String.format("%.2f", balanceValue));
    }

    private String capitalizeFirstLetter(String username) {
        if (username == null || username.isEmpty()) {
            return username;
        }
        return username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
    }

    private void setHelloUser(String username) {
        helloUser.setText("Hello, " + username);
    }

    @FXML
    public void onHiloBTNClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hi-lo.fxml"));
        music();

        // code for user balance
        try (Connection c = SQLHelper.getConnection();
             Statement statement = c.createStatement()) {

            String selectQuery = "SELECT * FROM users where id = " + userId;
            ResultSet result = statement.executeQuery(selectQuery);

            if (result.next()) {
                HiloController.userBalance = result.getInt("balance");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(HiloController.userBalance);
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);

            HiloController hiloController = loader.getController();
            hiloController.setForeground();

            // Set the background color of the scene to black
            scene.setFill(Color.BLACK);

            // Get the stage from the event source
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            primaryStage.setTitle("Hilo");
            primaryStage.setScene(scene);

            // Store the original width and height of the stage
            originalWidth = primaryStage.getWidth();
            originalHeight = primaryStage.getHeight();

            // Set up a listener for stage size changes
            primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> adjustAnchorPane(primaryStage, root));
            primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> adjustAnchorPane(primaryStage, root));

            // Set up a listener for stage minimized and maximized events
            primaryStage.iconifiedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    // Store the current position of the stage when minimized
                    primaryStage.setX(primaryStage.getX());
                    primaryStage.setY(primaryStage.getY());
                    // Restore the original size
                    primaryStage.setWidth(originalWidth);
                    primaryStage.setHeight(originalHeight);
                }
            });

            primaryStage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    // Restore the position of the stage when maximized
                    primaryStage.setX(0);
                    primaryStage.setY(0);
                }
            });

            // Adjust anchor pane initially
            adjustAnchorPane(primaryStage, root);

            primaryStage.show();
        } catch (IOException e) {
            // Handle any IOException that occurs during loading
            e.printStackTrace();
            // You might want to show an error message to the user here
        }
    }

    @FXML
    public void onCrashBTNClick(ActionEvent event) {
        showSplashScreen(event);
    }

    private void showSplashScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("splash-view.fxml"));
            Pane splashPane = loader.load();
            Scene splashScene = new Scene(splashPane, 1920, 1080);
            splashScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("chart.css")).toExternalForm());

            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            primaryStage.setScene(splashScene);
            primaryStage.setFullScreen(true);
            primaryStage.show();

            // Get the MediaView from the FXML
            MediaView splashMediaView = (MediaView) splashPane.lookup("#splashMediaView");
            ProgressBar progressBar = (ProgressBar) splashPane.lookup("#progressBar");

            // Load and play the video
            String videoPath = Objects.requireNonNull(getClass().getResource("/background/crash4k.mp4")).toExternalForm();
            Media media = new Media(videoPath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            splashMediaView.setMediaPlayer(mediaPlayer);

            // Wait until the media is ready before binding the progress bar
            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                if (newStatus == MediaPlayer.Status.READY) {
                    ReadOnlyObjectProperty<Duration> durationProperty = mediaPlayer.totalDurationProperty();
                    progressBar.progressProperty().bind(
                            Bindings.createDoubleBinding(
                                    () -> mediaPlayer.getCurrentTime().toMillis() / durationProperty.get().toMillis(),
                                    mediaPlayer.currentTimeProperty(),
                                    durationProperty
                            )
                    );
                }
            });

            mediaPlayer.setOnEndOfMedia(() -> showMainScreen(primaryStage));
            mediaPlayer.play();
        } catch (Exception e) {
            throw new RuntimeException("Splash screen did not load", e);
        }
    }

    private void showMainScreen(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("crash-view.fxml")));

            // Calculate the DPI scaling factor
            double dpiScale = ScreenHelper.getDPIScale();

            // Apply the scaling transformation
            Scale scale = new Scale(dpiScale, dpiScale);
            root.getTransforms().add(scale);

            Scene scene = new Scene(root, 1920, 1080);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("chart.css")).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Main screen did not load", e);
        }
    }
    


    // Method to adjust the anchor pane to stay in the middle
    static void adjustAnchorPane(Stage stage, Parent root) {
        double screenWidth = stage.getWidth();
        double screenHeight = stage.getHeight();

        double anchorPaneWidth = originalWidth; // Use originalWidth instead of root.getLayoutBounds().getWidth()
        double anchorPaneHeight = originalHeight; // Use originalHeight instead of root.getLayoutBounds().getHeight()

        double layoutX = (screenWidth - anchorPaneWidth) / 2;
        double layoutY = (screenHeight - anchorPaneHeight) / 2;

        root.setLayoutX(layoutX);
        root.setLayoutY(layoutY);

        // Set the preferred size of the root node to its original size
        root.resize(originalWidth, originalHeight);
    }
    @FXML
    public void onSlotMachineBTNClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("SlotMachine.fxml")));
            SlotMachine.getDPI(event, root);


        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
        }

    }
    private void showMainScreenBlackJack(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("blackjack_main.fxml")));

            // Calculate the DPI scaling factor
            double dpiScale = ScreenHelper.getDPIScale();

            // Apply the scaling transformation
            Scale scale = new Scale(dpiScale, dpiScale);
            root.getTransforms().add(scale);

            Scene scene = new Scene(root, 1920, 1080);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("chart.css")).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Main screen did not load", e);
        }
    }
    private void showSplashScreenBlackJack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("splash-view.fxml"));
            Pane splashPane = loader.load();
            Scene splashScene = new Scene(splashPane, 1920, 1080);
            splashScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("chart.css")).toExternalForm());

            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            primaryStage.setScene(splashScene);
            primaryStage.setFullScreen(true);
            primaryStage.show();

            // Get the MediaView from the FXML
            MediaView splashMediaView = (MediaView) splashPane.lookup("#splashMediaView");
            ProgressBar progressBar = (ProgressBar) splashPane.lookup("#progressBar");

            // Load and play the video
            String videoPath = Objects.requireNonNull(getClass().getResource("/background/BlackJack.mp4")).toExternalForm();
            Media media = new Media(videoPath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            splashMediaView.setMediaPlayer(mediaPlayer);

            // Wait until the media is ready before binding the progress bar
            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                if (newStatus == MediaPlayer.Status.READY) {
                    ReadOnlyObjectProperty<Duration> durationProperty = mediaPlayer.totalDurationProperty();
                    progressBar.progressProperty().bind(
                            Bindings.createDoubleBinding(
                                    () -> mediaPlayer.getCurrentTime().toMillis() / durationProperty.get().toMillis(),
                                    mediaPlayer.currentTimeProperty(),
                                    durationProperty
                            )
                    );
                }
            });

            mediaPlayer.setOnEndOfMedia(() -> showMainScreenBlackJack(primaryStage));
            mediaPlayer.play();
        } catch (Exception e) {
            throw new RuntimeException("Splash screen did not load", e);
        }
    }


    @FXML
    public void btnBlackJackOnAction(ActionEvent event) {
        showSplashScreenBlackJack(event);
        BlackJackBackGround();
        try (Connection c = SQLHelper.getConnection();
             Statement statement = c.createStatement()) {

            String selectQuery = "SELECT * FROM users where id = " + userId;
            ResultSet result = statement.executeQuery(selectQuery);

            if (result.next()) {
                BlackjackGame.balance = result.getInt("balance");
                BlackjackGame.name = result.getString("username");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors appropriately
        }
    }


    MediaPlayer mediaPlayer;

    public void BlackJackBackGround() {
        String musicFilePath = "src/main/resources/background_musics/blackjack_main.mp3";
        Media h = new Media(Paths.get(musicFilePath).toUri().toString());
        mediaPlayer = new MediaPlayer(h);


        mediaPlayer.play();
    }

    public void music() {
        String s = "src/main/resources/background_musics/jazz.mp3";
        Media h = new Media(Paths.get(s).toUri().toString());
        mediaPlayer = new MediaPlayer(h);

        // Add event handler for end of media
        mediaPlayer.setOnEndOfMedia(() -> {
            // Rewind the media to the beginning
            mediaPlayer.seek(Duration.ZERO);
            // Play the media again
            mediaPlayer.play();
        });

        mediaPlayer.play();
    }




    public void goDeposit(ActionEvent event) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("deposit.fxml"));
            Parent root = loader.load();
            
            DepositController depositController = loader.getController();
            depositController.initialize();

            // Create a new scene with the loaded FXML file
            Scene scene = new Scene(root);

            // Get the stage from the event source
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene on the primary stage
            primaryStage.setTitle("Transaction");
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.show();

        } catch (IOException e) {
            // Handle any IOException that occurs during loading
            e.printStackTrace();
            // You might want to show an error message to the user here
        }
    }
}
