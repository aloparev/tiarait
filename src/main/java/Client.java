import lenz.htw.tiarait.ColorChange;
import lenz.htw.tiarait.net.NetworkClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.Scanner;

@Slf4j
public class Client
{
    public static void main( String[] args )
    {
        String team = "abeta";
        String host = "127.0.0.1";
        Random rand = new Random();

        if(args.length == 2) {
            host = args[0];
            team = args[1];
        }

        System.out.println( "tiarait client up and running\n\thost=" +host+" team="+team);

        NetworkClient networkClient = new NetworkClient(host, team);

        networkClient.getMyPlayerNumber(); // 0-3 (ACHTUNG! andere Nummerierung als beim ColorChange)

        while (networkClient.isAlive()) {
// steuerung
//            float x = networkClient.getX(player, botNr);
//            float y = networkClient.getY(player, botNr);

//            double loop to identify walls
            networkClient.isWall(7, 11); //true wenn bei Koordinate 7,11 ein Hindernis steht

            Scanner sc = new Scanner(System.in);
            float x = Float.parseFloat(sc.nextLine());
            float y = Float.parseFloat(sc.nextLine());

            float xr = rand.nextFloat();
            float yr = rand.nextFloat();
            log.info("x=" + x + " y=" + y + " | xr=" + xr + " yr=" + xr);

            //eraser
//            networkClient.setMoveDirection(0, x, y);

//            pyramid
            networkClient.setMoveDirection(1, x, y);

//            eraser
//            networkClient.setMoveDirection(2, -5.1f, -0.8f);

            ColorChange cc;
            // cc in eigene Struktur einarbeiten
            while ((cc = networkClient.getNextColorChange()) != null) {
//                z.B. brett[cc.x][cc.y] = cc.newColor;
//                cc.newColor; //0 = leer, 1-4 = spieler
                log.info("cc new color=" + cc.newColor + " cc.x=" + cc.x + " cc.y=" + cc.y);
            }
        }
    }
}
