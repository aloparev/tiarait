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
        String team = "fox";
        String host = "127.0.0.1";

        float x, y;
        float xr, yr;
        Random rand = new Random();
        boolean negative = true;

        if(args.length == 2) {
            host = args[0];
            team = args[1];
        } else log.info("no args for host & team submitted, using defaults");
        System.out.println("tiarait client up and running: host=" + host + " team=" + team);

        NetworkClient nc = new NetworkClient(host, team);
        Board bb = new Board(nc.getMyPlayerNumber()); // 0-3 (ACHTUNG! andere Nummerierung als beim ColorChange)

        for(int i=0; i<bb.SIZE; i++) //x
            for(int j=0; j<bb.SIZE; j++) //y
                if(nc.isWall(i, j)) {
//                    log.info(i + "/" + j + " isWall");
                    bb.board[i][j] = bb.WALL;
                }

        /*
        log.info("testing enemy location, [1-4].bots");
        log.info("player1-red.bot0 x=" + nc.getX(0, 0) + " y=" + nc.getY(0, 0));
        log.info("player1-red.bot1 x=" + nc.getX(0, 1) + " y=" + nc.getY(0, 1));
        log.info("player1-red.bot2 x=" + nc.getX(0, 2) + " y=" + nc.getY(0, 2));

        log.info("player2-blu.bot0 x=" + nc.getX(1, 0) + " y=" + nc.getY(1, 0));
        log.info("player2-blu.bot1 x=" + nc.getX(1, 1) + " y=" + nc.getY(1, 1));
        log.info("player2-blu.bot2 x=" + nc.getX(1, 2) + " y=" + nc.getY(1, 2));

        log.info("player3-yel.bot0 x=" + nc.getX(1, 0) + " y=" + nc.getY(1, 0));
        log.info("player3-yel.bot1 x=" + nc.getX(1, 1) + " y=" + nc.getY(1, 1));
        log.info("player3-yel.bot2 x=" + nc.getX(1, 2) + " y=" + nc.getY(1, 2));

        log.info("player4-gre.bot0 x=" + nc.getX(1, 0) + " y=" + nc.getY(1, 0));
        log.info("player4-gre.bot1 x=" + nc.getX(1, 1) + " y=" + nc.getY(1, 1));
        log.info("player4-gre.bot2 x=" + nc.getX(1, 2) + " y=" + nc.getY(1, 2));
*/
        while (nc.isAlive()) {
// steuerung
//            float x = nc.getX(player, botNr);
//            float y = nc.getY(player, botNr);

//            double loop to identify walls

            Scanner sc = new Scanner(System.in);
            try {
                x = Float.parseFloat(sc.nextLine());
                y = Float.parseFloat(sc.nextLine());
            } catch (NumberFormatException nu) {
                log.info("NumberFormatError: hold on");
                x=0; y=0;
            }

            if(x>-1 && x<33 && y>-1 && y<33)
                log.info("wall check for x/y: " + nc.isWall(Math.round(x), Math.round(y))); //true wenn bei Koordinate 7,11 ein Hindernis steht

            xr = rand.nextFloat(); yr = rand.nextFloat();
            if(negative) {
                nc.setMoveDirection(0, xr, yr);
                nc.setMoveDirection(1, -1*xr, -1*yr);

                negative=false;
            } else {
                nc.setMoveDirection(0, xr, -1*yr);
                nc.setMoveDirection(1, -1*xr, yr);

                negative=true;
            }
            log.info("x=" + x + " y=" + y + " | xr=" + xr + " yr=" + yr);

            //eraser
//            nc.setMoveDirection(0, -5.1f, -0.8f);

//            cube
//            nc.setMoveDirection(1, x, y);

//            pyramid
            nc.setMoveDirection(2, x, y);

            ColorChange cc;
            // cc in eigene Struktur einarbeiten
            while ((cc = nc.getNextColorChange()) != null) {
//                z.B. brett[cc.x][cc.y] = cc.newColor;
//                cc.newColor; //0 = leer, 1-4 = spieler
                log.info("cc new color=" + cc.newColor + " cc.x=" + cc.x + " cc.y=" + cc.y);
            }
        }
    }
}
