module galimimus.org.hlsserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires jdk.httpserver;
    requires java.desktop;
    requires ffmpeg;


    opens galimimus.org.hlsserver to javafx.fxml;
    exports galimimus.org.hlsserver;
    opens galimimus.org.hlsserver.controllers to javafx.fxml;
    exports galimimus.org.hlsserver.controllers;
}