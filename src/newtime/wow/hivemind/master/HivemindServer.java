package newtime.wow.hivemind.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HivemindServer implements Runnable {

    public static final int POLL_RESPONSE_MESSAGE_LENGTH = 1024;
    public static final byte[] POLL_MESSAGE = "hivemind://poll".getBytes();

    public Thread listeningThread = new Thread(this);
    public ServerSocket serverSocket;

    public ArrayList<HivemindClient> clients = new ArrayList<HivemindClient>();

    public HivemindServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.start();
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
