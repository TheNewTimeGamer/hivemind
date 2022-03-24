package Hivemind.master;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.sun.net.httpserver.*;

public class HttpService {

    public static String rootDirectory = "app/static/";

    public static final HashMap<String,String> MIME = new HashMap<String, String>();
    static {
        MIME.put("appcache", "text/cache-manifest");
        MIME.put("css", "text/css");
        MIME.put("gif", "image/gif");
        MIME.put("html", "text/html");
        MIME.put("js", "application/javascript");
        MIME.put("json", "application/json");
        MIME.put("jpg", "image/jpeg");
        MIME.put("jpeg", "image/jpeg");
        MIME.put("mp4", "video/mp4");
        MIME.put("pdf", "application/pdf");
        MIME.put("png", "image/png");
        MIME.put("svg", "image/svg+xml");
        MIME.put("xlsm", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME.put("xml", "application/xml");
        MIME.put("zip", "application/zip");
        MIME.put("md", "text/plain");
        MIME.put("txt", "text/plain");
        MIME.put("php", "text/plain");
    };

    public static boolean sendErrorResponse(HttpExchange exchange, int status, String contentType, byte[] buffer){
        return HttpService.serve(exchange, status, contentType, buffer);
    }

    public static boolean serve(HttpExchange exchange, int status, String contentType, byte[] buffer) {
        if(contentType == null){
            contentType = "text/html";
        }
        try{
            exchange.getResponseHeaders().add("Content-Length", ""+buffer.length);
            exchange.sendResponseHeaders(status, buffer.length);
            DataOutputStream out = new DataOutputStream(exchange.getResponseBody());
            out.write(buffer);
            out.flush();
            out.close();
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

class StaticHandler implements HttpHandler {

    public void handle(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equals("GET")){
            HttpService.sendErrorResponse(exchange, 405, null, "Method Not Allowed".getBytes());
            return;
        }
        String path = exchange.getRequestURI().getPath();
        if(!path.startsWith("/")){
            path = "/" + path;
        }

        if(path.equals("/")){
            path = "/index.html";
        }

        File file = new File(HttpService.rootDirectory + path);
        System.out.println("Serving: " + file.getAbsolutePath());
        if(!file.exists()) {
            HttpService.sendErrorResponse(exchange, 404, null, "File Not Found.".getBytes());
            return;
        }
        if(file.isDirectory()){
            HttpService.sendErrorResponse(exchange, 404, null, "File Not Found.".getBytes());
            return;
        }
        if(!file.isFile()){
            HttpService.sendErrorResponse(exchange, 404, null, "File Not Found.".getBytes());
            return;
        }

        byte[] buffer = new byte[(int)file.length()];
        boolean success = FileManager.tryGetFile(file, buffer);
        if(!success){
            HttpService.sendErrorResponse(exchange, 500, null, "Internal Server Error.".getBytes());
            return;
        }

        String fileExtension = FileManager.getExtension(file);

        HttpService.serve(exchange, 200, HttpService.MIME.get(fileExtension), buffer);

        // TO-DO: Check for keep-alive header.

        exchange.close();
    }
    
}
