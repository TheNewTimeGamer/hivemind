package Hivemind.master;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Player {

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

    public Player(HivemindClient hivemindClient){
        this.hivemindClient = hivemindClient;
    }

    public PlayerData playerData = new PlayerData();

    /**
     Check if it's time for a new poll request.
     If it is time for a new poll request - Send a new poll request.
     On failure, increment pollingErrorCount.
     If pollingErrorCount exceeds pollingErrorLimit, Kill the client.
    */
    public boolean poll(){
        if(System.currentTimeMillis() - this.playerData.lastPoll > this.playerData.pollLimit){
            if(sendPollRequest()){
                this.playerData.pollingErrorCount = 0;
                return true;
            }
            this.playerData.pollingErrorCount++;
            if(this.playerData.pollingErrorCount > this.playerData.pollingErrorLimit) {
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
            this.playerData.mapID = buffer.getInt();
            this.playerData.x = buffer.getDouble();
            this.playerData.y = buffer.getDouble();
            this.playerData.face = buffer.getDouble();
            
            this.playerData.health.current = buffer.getInt();
            this.playerData.health.maximum = buffer.getInt();

            this.playerData.mana.current = buffer.getInt();
            this.playerData.mana.maximum = buffer.getInt();

            this.playerData.xp.current = buffer.getInt();
            this.playerData.xp.maximum = buffer.getInt();

            this.playerData.level.current = buffer.getInt();
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public Point2D getPosition(){
        poll();
        return new Point2D.Double(this.playerData.x, this.playerData.y);
    }

    public double getFace(){
        return this.playerData.face;
    }

    public int getMapID(){
        poll();
        return this.playerData.mapID;
    }

    public int getHealth(){
        poll();
        return this.playerData.health.current;
    }

    public int getMaximumHealth(){
        poll();
        return this.playerData.health.maximum;
    }

    public int getMana(){
        poll();
        return this.playerData.mana.current;
    }

    public int getMaximumMana(){
        poll();
        return this.playerData.mana.maximum;
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
