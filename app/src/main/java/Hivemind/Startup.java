package Hivemind;


import java.io.IOException;

import Hivemind.drone.HivemindDrone;
import Hivemind.master.HivemindServer;

public class Startup {

    public static void start(String[] args) throws Exception {
        if(args.length > 0){
            System.out.println(" As drone.");
            if (args[0].startsWith("client=")) {
                Startup.startAsDrone(args[0].split("=")[1].split(":"));
            }
        }else{
            System.out.println(" As server.");
            Startup.startAsMaster();
        }
    }

    public static void startAsDrone(String[] args) throws Exception {
        new HivemindDrone(args[0], Integer.parseInt(args[1]));
    }

    public static void startAsMaster() throws IOException {
        HivemindServer server = new HivemindServer(4444);
    }

}
