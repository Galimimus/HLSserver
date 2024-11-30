package galimimus.org.hlsserver.controllers;

import galimimus.org.hlsserver.HelloApplication;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//import javax.swing.*;
//import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import static galimimus.org.hlsserver.server.HTTPServer.startHttpServer;
import static galimimus.org.hlsserver.server.HTTPServer.stopHttpServer;
import static galimimus.org.hlsserver.helpers.ProcessMP4.processMP4;


public class ServerWindowController {
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

        //JFileChooser jFileChooser = new JFileChooser();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select mp4 file to stream to other users");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Video Files", "*.mp4"));
        Stage tmpStage = new Stage();
        File selected = fileChooser.showOpenDialog(tmpStage);
        //tmpStage.showAndWait();
        if (selected != null) processMP4(selected);
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
