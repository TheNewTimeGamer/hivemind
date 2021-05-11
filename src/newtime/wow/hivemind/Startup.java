package newtime.wow.hivemind;

import newtime.wow.hivemind.drone.HivemindDrone;
import newtime.wow.hivemind.master.HivemindFrame;
import newtime.wow.hivemind.master.HivemindServer;

import java.io.IOException;

public class Startup {

    public static void main(String[] args) throws Exception {
        if(args.length > 0){
            if (args[0].startsWith("client=")) {
                startAsDrone(args[0].split("=")[1].split(":"));
            }
        }else{
            startAsMaster();
        }
    }

    private static void startAsDrone(String[] args) throws Exception {
        HivemindDrone drone = new HivemindDrone(args[0], Integer.parseInt(args[1]));
    }

    private static void startAsMaster() throws IOException {
        HivemindServer server = new HivemindServer(4444);
        HivemindFrame frame = new HivemindFrame(server);
    }

}
