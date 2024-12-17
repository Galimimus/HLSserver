module galimimus.org.hlsserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.httpserver;
    requires java.desktop;
    requires static lombok;
    requires com.google.gson;
    requires java.logging;


    opens galimimus.org.hlsserver to javafx.fxml;
    exports galimimus.org.hlsserver;
    opens galimimus.org.hlsserver.controllers to javafx.fxml;
    exports galimimus.org.hlsserver.controllers;
    opens galimimus.org.hlsserver.models to com.google.gson;


}