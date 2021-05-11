package newtime.wow.hivemind.drone;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class HivemindDroneScreenReader {

    private Robot robot;

    public HivemindDroneScreenReader() throws AWTException {
        this.robot = new Robot();
    }

    private BufferedImage screen;
    private long pollLimit = 100;
    private long lastPoll = 0;

    private int mapID;
    private double face;

    private Point2D position = new Point2D.Double(0,0);

    private int health;
    private int maximumHealth;

    private int mana;
    private int maximumMana;

    private boolean poll(){
        if(System.currentTimeMillis() - lastPoll > pollLimit){
            lastPoll = System.currentTimeMillis();
            return readScreen();
        }
        return true;
    }

    public int getMapID(){
        poll();
        return this.mapID;
    }

    public double getFace(){
        poll();
        return this.face;
    }

    public Point2D getPosition(){
        poll();
        return this.position;
    }

    public void setPositionFace(Color color){
        double red = (double)color.getRed();
        double green = (double)color.getGreen();
        double blue = (double)color.getBlue();

        this.position.setLocation((red/255)*100,(green/255)*100);
        this.face = (blue/255)*(Math.PI*2);
    }

    public int getHealth(){
        poll();
        return this.health;
    }

    public int getMaximumHealth(){
        poll();
        return this.maximumHealth;
    }

    public int getMana(){
        poll();
        return this.mana;
    }

    public int getMaximumMana(){
        poll();
        return this.maximumMana;
    }

    private boolean readScreen() {
        Dimension screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
        this.screen = this.robot.createScreenCapture(new Rectangle(0,0,screenResolution.width,screenResolution.height));

        Color positionFace = new Color(this.screen.getRGB(32,0));
        this.setPositionFace(positionFace);

        return true;
    }

}
