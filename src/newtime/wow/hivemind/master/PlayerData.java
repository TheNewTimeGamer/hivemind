package newtime.wow.hivemind.master;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;

public class PlayerData {

    public static int
            SPELL_POWER_MANA            = 0,
            SPELL_POWER_RAGE            = 1,
            SPELL_POWER_FOCUS           = 2,
            SPELL_POWER_ENERGY          = 3,
            SPELL_POWER_COMBO_POINTS    = 4,
            SPELL_POWER_RUNES           = 5,
            SPELL_POWER_RUNIC_POWER     = 6,
            SPELL_POWER_SOUL_SHARDS     = 7,
            SPELL_POWER_LUNAR_POWER     = 8,
            SPELL_POWER_HOLY_POWER      = 9,
            SPELL_POWER_ALTERNATE_POWER = 10,
            SPELL_POWER_MAELSTROM       = 11,
            SPELL_POWER_CHI             = 12,
            SPELL_POWER_INSANITY        = 13,
            SPELL_POWER_OBSOLETE        = 14,
            SPELL_POWER_OBSOLETE2       = 15,
            SPELL_POWER_ARCANE_CHARGES  = 16,
            SPELL_POWER_FURY            = 17,
            SPELL_POWER_PAIN            = 18;

    public final HivemindClient hivemindClient;

    public PlayerData(HivemindClient hivemindClient){
        this.hivemindClient = hivemindClient;
    }

    private int currentMapID;
    private Point position = new Point(0,0);
    private double face;
    private long pollLimit = 100;
    private long lastPoll = 0;
    private int pollingErrorCount = 0;
    private int pollingErrorLimit = 5;

    private Resource health = new Resource(0,100);
    private Resource mana = new Resource(0,100);

    /**
     Check if it's time for a new poll request.
     If it is time for a new poll request - Send a new poll request.
     On failure, increment pollingErrorCount.
     If pollingErrorCount exceeds pollingErrorLimit, Kill the client.
    */
    public boolean poll(){
        if(System.currentTimeMillis() - lastPoll > pollLimit){
            if(sendPollRequest()){
                pollingErrorCount = 0;
                return true;
            }
            pollingErrorCount++;
            if(pollingErrorCount > pollingErrorLimit) {
                System.err.println("PlayerData: pollingErrorCount exceeded pollingErrorLimit.");
                this.hivemindClient.disconnect();
            }
            return false;
        }
        return true;
    }

    /**
     Send a poll request.
     Retrieve a response of length POLL_RESPONSE_MESSAGE_LENGTH.
     (Specified in HivemindServer)

     @return  boolean Whether or not a valid response was received.
    */
    public boolean sendPollRequest(){
        byte[] responseBuffer = new byte[HivemindServer.POLL_RESPONSE_MESSAGE_LENGTH];
        try {
            hivemindClient.out.write(HivemindServer.POLL_MESSAGE);
            hivemindClient.out.flush();
            int count = hivemindClient.in.read(responseBuffer);
            if(count < HivemindServer.POLL_RESPONSE_MESSAGE_LENGTH){
                System.err.println("PlayerData: count less than minimum POLL_RESPONSE_MESSAGE_LENGTH. (" + count + "/" + HivemindServer.POLL_RESPONSE_MESSAGE_LENGTH + ")");
                return false;
            }
        } catch (IOException e) {
            System.err.println("PlayerData: Exception during poll request.");
            this.hivemindClient.disconnect();
            return false;
        }
        return processPollResponse(responseBuffer);
    }

    public boolean processPollResponse(byte[] response){
        try {
            ByteBuffer buffer = ByteBuffer.wrap(response);
            this.currentMapID = buffer.getInt();
            this.position.x = buffer.getInt();
            this.position.y = buffer.getInt();
            this.face = buffer.getDouble();

            this.health.current = buffer.getInt();
            this.health.maximum = buffer.getInt();

            this.mana.current = buffer.getInt();
            this.mana.maximum = buffer.getInt();
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public Point getPosition(){
        poll();
        return this.position;
    }

    public double getFace(){
        return this.face;
    }

    public int getHealth(){
        poll();
        return this.health.current;
    }

    public int getMaximumHealth(){
        poll();
        return this.health.maximum;
    }

    public int getMana(){
        poll();
        return this.mana.current;
    }

    public int getMaximumMana(){
        poll();
        return this.mana.maximum;
    }

}

class Resource {

    public int current;
    public int maximum;

    public Resource(int current, int maximum){
        this.current = current;
        this.maximum = maximum;
    }
}
