package Hivemind.master;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HivemindServer implements Runnable {

    public static final int POLL_RESPONSE_MESSAGE_LENGTH = 1024;
    public static final byte[] POLL_MESSAGE = "hivemind://poll".getBytes();

    public Thread listeningThread = new Thread(this);
    public ServerSocket serverSocket;

    public ArrayList<HivemindClient> clients = new ArrayList<HivemindClient>();

    public HttpServer apiServer;

    public HivemindServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.start();

        this.apiServer = HttpServer.create(new InetSocketAddress(80), 0);
        this.apiServer.createContext("/", new StaticHandler());
        this.apiServer.createContext("/clients", new HivemindApiHandler(this));
        this.apiServer.setExecutor(null);
        this.apiServer.start();
    }

    boolean running = false;

    public void start(){
        this.running = true;
        this.listeningThread.start();
    }

    public void stop(){
        this.running = false;
    }

    public void run(){
        while(running){
            try {
                listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listen() throws IOException {
        Socket socket = this.serverSocket.accept();
        if(socket != null){
            this.clients.add(new HivemindClient(socket));
        }
    }

}

class HivemindApiHandler implements HttpHandler {

    HivemindServer server;

    public HivemindApiHandler(HivemindServer server) {
        this.server = server;
    }

    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if(query == null){
            sendClients(exchange);
            return;
        }
        int clientID = Integer.parseInt(query.split("=")[1]);
        sendClient(exchange, clientID);
        return;
    }

    public void sendClient(HttpExchange exchange, int index) {
        server.clients.get(index).player.poll();
        PlayerData playerData = server.clients.get(index).player.playerData;
        Gson gson = new Gson();
        byte[] data = gson.toJson(playerData).getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        try {
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendClients(HttpExchange exchange) {
        PlayerData[] playerData = new PlayerData[this.server.clients.size()];
        for(int i = 0; i < this.server.clients.size(); i++){
            this.server.clients.get(i).player.poll();
            playerData[i] = this.server.clients.get(i).player.playerData;
        }
        Gson gson = new Gson();
        String json = "Failure.";
        try{
            json = gson.toJson(playerData);
        }catch(Exception e){
            e.printStackTrace();
        }
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        try {
            exchange.sendResponseHeaders(200, json.getBytes().length);
            exchange.getResponseBody().write(json.getBytes());
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}