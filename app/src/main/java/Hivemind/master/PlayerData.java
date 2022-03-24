package Hivemind.master;

import java.util.Random;

public class PlayerData {

    public String playerID;

    public int mapID;
    public double x, y;
    public double face;

    public long pollLimit = 100;
    public long lastPoll = 0;
    public int pollingErrorCount = 0;
    public int pollingErrorLimit = 5;

    public Resource health = new Resource(0,100);
    public Resource mana = new Resource(0,100);
    public Resource xp = new Resource(0,100);
    public Resource level = new Resource(0,60);

    public int[] rotation = null;

    public PlayerData(){
        this.playerID = "C:" + Math.abs(new Random().nextLong() / 2);
    }

    public PlayerData(String playerID){
        this.playerID = playerID;
    }

}
