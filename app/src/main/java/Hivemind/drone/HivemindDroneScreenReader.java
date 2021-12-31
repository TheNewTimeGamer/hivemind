package Hivemind.drone;

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
    private int maximumHealth = 100;

    private int mana;
    private int maximumMana = 100;

    private int xp;
    private int maximumXp = 100;

    private int level;

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

    public void setMapID(Color color){
        double red = color.getRed();
        double green = color.getGreen();

        red /= 255;
        green /= 255;

        int redPartial = (int) (red * 100);
        int greenPartial = (int) (green * 100);

        this.mapID = (redPartial * 100) + greenPartial;
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

    public int getXp(){
        poll();
        return this.xp;
    }

    public int getMaximumXp(){
        poll();
        return this.maximumXp;
    }

    public int getLevel(){
        poll();
        return this.level;
    }

    private Color getColorAt(int x, int y){
        return new Color(this.screen.getRGB(x, y));
    }

    private boolean readScreen() {
        Dimension screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
        this.screen = this.robot.createScreenCapture(new Rectangle(0,0,screenResolution.width,screenResolution.height));

        Color positionFace = getColorAt(8,2);
        this.setPositionFace(positionFace);

        Color mapID = getColorAt(24,2);
        this.setMapID(mapID);

        Color healthMana = getColorAt(40,2);
        this.health = (int) (healthMana.getRed() / 255) * 100;
        this.mana = (int) (healthMana.getGreen() / 255) * 100;

        Color levelXp = getColorAt(56,2);

        double levelColor = levelXp.getRed();
        double xpColor = levelXp.getGreen();
        double xpMaxColor = levelXp.getBlue();

        this.level = (int) ((levelColor / 255) * 100);
        this.xp = (int) ((xpColor / 255) * 100);

        return true;
    }

}
