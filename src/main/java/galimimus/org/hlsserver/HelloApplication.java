package galimimus.org.hlsserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.LogManager;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("server_window.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("HLS server application");
        stage.setScene(scene);
        try {
            LogManager.getLogManager().readConfiguration(
                    HelloApplication.class.getResourceAsStream("logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e);
        }
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}