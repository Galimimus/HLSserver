package galimimus.org.hlsserver.server;

import galimimus.org.hlsserver.HelloApplication;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HTTPServer {
    private static boolean IS_ACTIVE = false;
    private static HttpServer server;
    static final Logger log = Logger.getLogger(HelloApplication.class.getName());
    //static final SettingsSingleton ss = SettingsSingleton.getInstance();

    public static void startHttpServer() {
        try {
            if (!IS_ACTIVE) {
                server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
                server.createContext("/", new Handler());
                //server.createContext("/all", new AllHandler());
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
                //server = HttpServer.create(new InetSocketAddress("localhost", 80), 0);
                //server.createContext("/", new Handler());
                //server.createContext("/all", new AllHandler());
                //server.setExecutor(null);
                //server.setExecutor(new ThreadPoolExecutor(THREADS_AMOUNT, MAX_THREADS_AMOUNT, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY)));
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
            if("GET".equalsIgnoreCase(t.getRequestMethod())){
                Path resp = Paths.get("/home/galimimus/IdeaProjects/HLSserver/server/","index.html");
                try {
                    t.sendResponseHeaders(200, Files.size(resp));
                    OutputStream output = t.getResponseBody();
                    Files.copy(resp, output);
                    output.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if("POST".equalsIgnoreCase(t.getRequestMethod())){
                File directoryPath = new File("/home/galimimus/IdeaProjects/HLSserver/server/");
                String[] contents = directoryPath.list();
                String str_contents = "";
                int len = 0;
                if (contents != null) len = contents.length;
                for(int i = 0; i < len; i++){
                    str_contents += "\"" + contents[i] + "\"";
                    if(i < len - 1) str_contents+=",";
                }
                try {

                    String resp = String.format("{\"videos\":[" + str_contents + "]}", "POST");
                    t.getResponseHeaders().add("Content-Type", "application/json");
                    t.sendResponseHeaders(200, resp.length());
                    OutputStream output = t.getResponseBody();
                    output.write(resp.getBytes());
                    output.close();
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