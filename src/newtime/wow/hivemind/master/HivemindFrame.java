package newtime.wow.hivemind.master;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class HivemindFrame implements Runnable {

    public final HivemindServer hiveMindServer;

    private JFrame frame;

    private Canvas canvas = new Canvas();
    private Thread renderThread = new Thread(this);

    public HivemindFrame(HivemindServer hivemindServer){
        this.hiveMindServer = hivemindServer;
        initFrame();
    }

    public void initFrame(){
        this.frame = new JFrame("Hivemind");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Should tell clients to disconnect.
        this.frame.setSize(800,600);
        this.frame.setLocationRelativeTo(null);
        this.frame.add(canvas);
        this.frame.setVisible(true);
        this.start();
    }

    private boolean running;

    public void start(){
        this.running = true;
        this.renderThread.start();
    }

    public void stop(){
        this.running = false;
    }

    public void run(){
        while(running){
            tick();
            render();
        }
    }

    private void tick(){

    }

    private void render(){
        BufferStrategy bs = canvas.getBufferStrategy();
        if(bs == null){
            canvas.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

        renderClients(g);

        g.dispose();
        bs.show();
    }

    private void renderClients(Graphics g){
        ArrayList<HivemindClient> clients = this.hiveMindServer.clients;
        for(int i = clients.size()-1; i >= 0; i--){
            HivemindClient client = clients.get(i);
            if(client != null){
                if(client.isDeadSocket()){
                    clients.remove(i);
                }
            }
        }
        for(HivemindClient client : clients){
            renderClient(client, g);
        }
    }

    private void renderClient(HivemindClient client, Graphics g){
        Point playerPosition = client.player.getPosition();

        g.setColor(Color.RED);
        g.fillOval(playerPosition.x-16, playerPosition.y-16, 32, 32);
    }

}
