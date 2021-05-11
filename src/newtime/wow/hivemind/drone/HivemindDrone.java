package newtime.wow.hivemind.drone;

import newtime.wow.hivemind.master.HivemindServer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class HivemindDrone implements Runnable {

    Socket socket;
    InputStream in;
    OutputStream out;

    Thread listeningThread = new Thread(this);

    boolean deadSocket = false;

    HivemindDroneScreenReader screenReader = new HivemindDroneScreenReader();

    public HivemindDrone(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        this.in = this.socket.getInputStream();
        this.out = this.socket.getOutputStream();
        this.start();
    }

    boolean running;

    public void start(){
        this.running = true;
        this.listeningThread.start();
    }

    public void stop(){
        this.running = false;
    }

    long lastMessage = System.currentTimeMillis();
    long lastMessageTimeout = 5000;

    public void run(){
        while(running){
            try {
                listen();
            } catch (IOException e) {
                System.err.println("Could not listen to server. (Suicide)");
                System.exit(-1);
            }
        }
    }

    public void listen() throws IOException {
        byte[] buffer = new byte[HivemindServer.POLL_RESPONSE_MESSAGE_LENGTH];
        int count = in.read(buffer);
        if(count > 0){
            lastMessage = System.currentTimeMillis();
        }else if(System.currentTimeMillis() - lastMessage > lastMessageTimeout){
            this.disconnect();
        }
        String trimmed = new String(buffer).trim();
        if(trimmed.equals(new String(HivemindServer.POLL_MESSAGE).trim())){
            sendPollData();
        }
    }

    private void sendPollData() throws IOException {
        Point position = screenReader.getPosition();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.putInt(screenReader.getMapID());
        buffer.putInt(position.x);
        buffer.putInt(position.y);
        buffer.putDouble(screenReader.getFace());
        buffer.putInt(screenReader.getHealth());
        buffer.putInt(screenReader.getMaximumHealth());
        buffer.putInt(screenReader.getMana());
        buffer.putInt(screenReader.getMaximumMana());
        out.write(buffer.array());
        out.flush();
    }

    public void disconnect(){
        this.deadSocket = true;
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isDeadSocket(){
        return this.deadSocket;
    }

}
