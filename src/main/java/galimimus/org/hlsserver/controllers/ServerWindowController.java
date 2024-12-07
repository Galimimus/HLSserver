package galimimus.org.hlsserver.controllers;

import galimimus.org.hlsserver.HelloApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static galimimus.org.hlsserver.server.HTTPServer.startHttpServer;
import static galimimus.org.hlsserver.server.HTTPServer.stopHttpServer;
import static galimimus.org.hlsserver.helpers.ProcessMP4.processMP4;


public class ServerWindowController {
    static final Logger log = Logger.getLogger(HelloApplication.class.getName());

    @FXML
    public TextField input_port;

    @FXML
    public Spinner spinner_ts_len;

    @FXML
    public TextField input_host;
    @FXML
    private TextArea text_logs;

    @FXML
    public void StartServer(ActionEvent actionEvent) {
        //log.logp(Level.SEVERE, "HTTPServer", "startHttpServer", e.toString());
        startHttpServer();
    }

    @FXML
    public void RestartServer(ActionEvent actionEvent) throws InterruptedException {
        stopHttpServer();
        Thread.sleep(200);
        startHttpServer();
    }

    @FXML
    public void DownloadVideo(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select mp4 file to stream to other users");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Video Files", "*.mp4"));
        Stage tmpStage = new Stage();
        File selected = fileChooser.showOpenDialog(tmpStage);
        int ts_len = 10;
        if (spinner_ts_len.getValue() != null) ts_len = (int) spinner_ts_len.getValue();
        if((ts_len < 1) || (ts_len > 180)){
            ts_len = 10;
            log.logp(Level.WARNING, "ServerWindowController", "DownloadVideo", "Incorrect value of .ts file len. Enter value between 1 and 180 s. ts length value set to 10s.");
        }
        System.out.println(ts_len);
        if (selected != null) processMP4(selected, Integer.toString(ts_len));

    }

    @FXML
    public void StopServer(ActionEvent actionEvent) throws InterruptedException {
        stopHttpServer();
        Thread.sleep(200);
    }

    public void RenewTextArea(ActionEvent actionEvent) {
        String new_text = "";
        try {
            new_text = Files.readString(Paths.get("/home/galimimus/IdeaProjects/HLSserver/server_log.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!new_text.isEmpty()) text_logs.setText(new_text);
    }
}
