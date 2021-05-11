package newtime.wow.hivemind.master;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class HivemindFrame implements Runnable {

    public final HivemindServer hiveMindServer;

    private JFrame frame;

    private Canvas canvas = new Canvas();
    private Thread renderThread = new Thread(this);

    private int selectedClient = 0;

    HashMap<Integer, BufferedImage> mapTextures = new HashMap<Integer, BufferedImage>();

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

        drawMap(g);
        renderClients(g);

        g.dispose();
        bs.show();
    }

    private void drawMap(Graphics g){
        if(this.selectedClient < 0 || this.selectedClient >= hiveMindServer.clients.size()){
            return;
        }
        BufferedImage map = getMapTexture(this.hiveMindServer.clients.get(this.selectedClient).player.getMapID());
        if(map != null){
            g.drawImage(map, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
        }
    }

    private BufferedImage getMapTexture(int mapID){
        BufferedImage mapTexture = this.mapTextures.get(Integer.valueOf(mapID));
        if (mapTexture == null) {
            File file = new File("maps/" + mapID + ".png");
            try {
                System.out.println("No map file loaded for: " + mapID + ". Loading..");
                mapTexture = ImageIO.read(file);
                this.mapTextures.put(Integer.valueOf(mapID), mapTexture);
            }catch(Exception e) {
                System.out.println("Failed to find map: " + file.getPath());
                return null;
            }
        }
        return mapTexture;
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
        Point2D playerPosition = client.player.getPosition();
        int mapID = client.player.getMapID();

        g.setColor(Color.RED);

        double x = playerPosition.getX();
        double y = playerPosition.getY();

        x = ((x / 100) * this.canvas.getWidth()) - 16;
        y = ((y / 100) * this.canvas.getHeight()) - 16;



        g.fillOval((int)x, (int)y, 32, 32);
    }

}
