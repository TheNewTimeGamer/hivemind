package Hivemind;


import java.io.IOException;

import Hivemind.drone.HivemindDrone;
import Hivemind.master.HivemindServer;

public class Startup {

    public static void startBoth(String ip, int port) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                startAsMaster(port);
            }
        });
        thread.start();
        startAsDrone(ip, port);
    }

    public static void startAsDrone(String ip, int port) {
        try {
            new HivemindDrone(ip,port);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void startAsMaster(int port) {
        try {
            HivemindServer server = new HivemindServer(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
