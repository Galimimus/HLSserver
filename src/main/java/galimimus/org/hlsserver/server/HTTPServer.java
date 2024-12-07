package galimimus.org.hlsserver.server;

import com.google.gson.Gson;
import galimimus.org.hlsserver.HelloApplication;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import galimimus.org.hlsserver.models.FoldersPaths;
import galimimus.org.hlsserver.models.Server;
import galimimus.org.hlsserver.models.Video;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static galimimus.org.hlsserver.helpers.Helper.readFromResourceStream;

public class HTTPServer {
    private static boolean IS_ACTIVE = false;
    private static HttpServer server;
    static final Logger log = Logger.getLogger(HelloApplication.class.getName());
    static Gson g = new Gson();
    static Server settings_server = g.fromJson(readFromResourceStream(Paths.get("settings_server.json")), Server.class);
    static FoldersPaths settings_paths = g.fromJson(readFromResourceStream(Paths.get("settings_paths.json")), FoldersPaths.class);
    static Video[] settings_videos = g.fromJson(readFromResourceStream(Paths.get("videos.json")), Video[].class);

    public static void startHttpServer() {
        try {
            if (!IS_ACTIVE) {

                System.out.println(settings_server.SETTINGS_HOST + "\n" + settings_server.SETTINGS_HOST + "\n" + settings_paths.SETTINGS_SERVER_PATH + "\n" + settings_paths.SETTINGS_FFMPEG_PATH);
                server = HttpServer.create(new InetSocketAddress(settings_server.SETTINGS_HOST, settings_server.SETTINGS_PORT), 0);

                server.createContext("/", new Handler());
                server.setExecutor(null);
                //server.setExecutor(new ThreadPoolExecutor(THREADS_AMOUNT, MAX_THREADS_AMOUNT, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY)));
                server.start();
                IS_ACTIVE = true;
                log.logp(Level.INFO, "HTTPServer", "startHttpServer", "HTTP server started");
            } else {
                log.logp(Level.INFO, "HTTPServer", "startHttpServer", "HTTP server is already running");
            }

        } catch (Exception e) {
            log.logp(Level.SEVERE, "HTTPServer", "startHttpServer", e.toString());
            throw new RuntimeException(e);
        }
    }


    public static void stopHttpServer() {
        try {
            if (IS_ACTIVE) {
                server.stop(2);
                IS_ACTIVE = false;
                log.logp(Level.INFO, "HTTPServer", "startHttpServer", "HTTP server stopped");
            } else {
                log.logp(Level.INFO, "HTTPServer", "startHttpServer", "HTTP server is already stopped");
            }

        } catch (Exception e) {
            log.logp(Level.SEVERE, "HTTPServer", "startHttpServer", e.toString());
            throw new RuntimeException(e);
        }
    }


    static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            //log.logp(Level.INFO, "Handler", "handle", "Handler acting!!! Request method: " + t.getRequestMethod() + " request uri: " + t.getRequestURI());

            if("GET".equalsIgnoreCase(t.getRequestMethod())){
                //log.logp(Level.INFO, "Handler", "handle", "Process GET Request");

                //String uri = t.getRequestURI().toString().split("8000")[1];
                Path resp = Paths.get(settings_paths.SETTINGS_SERVER_PATH, t.getRequestURI().toString());
                try {
                    //log.logp(Level.INFO, "Handler", "handle", "Start try block");
                    byte[] fileBytes = Files.readAllBytes(resp);
                    t.sendResponseHeaders(200, fileBytes.length);
                    OutputStream output = t.getResponseBody();
                    //Files.copy(resp, output);
                    output.write(fileBytes);
                    output.close();
                    log.logp(Level.INFO, "Handler", "handle", "Request method: " + t.getRequestMethod() + ". Sent response: " + resp); //+ "\n" + Files.readAllLines(resp));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if("POST".equalsIgnoreCase(t.getRequestMethod())){
                File directoryPath = new File(settings_paths.SETTINGS_SERVER_PATH);
                String[] contents = directoryPath.list();
                String str_contents = "";
                int len = 0;
                if (contents != null) len = contents.length;
                for(int i = 0; i < len; i++){
                    str_contents += "\"" + contents[i] + "\"";
                    if(i < len - 1) str_contents+=",";
                }
                try {
                    System.out.println(g.toJson(settings_videos));
                    String resp = String.format(g.toJson(settings_videos), "POST");
                    t.getResponseHeaders().add("Content-Type", "application/json");
                    t.sendResponseHeaders(200, resp.length());
                    OutputStream output = t.getResponseBody();
                    output.write(resp.getBytes());
                    output.close();
                    log.logp(Level.INFO, "Handler", "handle", "Request method: " + t.getRequestMethod() + ". Sent response: " + resp); //+ "\n" + Files.readAllLines(resp));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                log.logp(Level.WARNING, "DownloadHandler", "handle", "Method not allowed ");
                try {
                    t.sendResponseHeaders(405, 0); // Method Not Allowed
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}