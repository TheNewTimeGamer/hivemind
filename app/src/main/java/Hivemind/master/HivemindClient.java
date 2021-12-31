package Hivemind.master;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HivemindClient {

    public final Socket socket;
    public final InputStream in;
    public final OutputStream out;

    public Player player;

    private boolean deadSocket = false;

    public HivemindClient(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();

        this.player = new Player(this);

        if(!this.player.poll()){
            System.err.println("HivemindClient: Initial poll request failed. (Disconnecting)");
            this.disconnect();
            return;
        }
        System.out.println("HivemindClient: connected and successfully polled.");
    }

    public void disconnect(){
        this.deadSocket = true;
        try {
            this.socket.close();
        } catch (IOException e) {}
    }

    public boolean isDeadSocket(){
        return this.deadSocket;
    }

}
