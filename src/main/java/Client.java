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
        boolean switcher = true;

        if(args.length == 2) {
            host = args[0];
            team = args[1];
        } else log.info("no args for host & team submitted, using defaults");
        System.out.println("tiarait client up and running: host=" + host + " team=" + team);

        NetworkClient nc = new NetworkClient(host, team);
        Board bb = new Board(nc.getMyPlayerNumber()); // 0-3 (ACHTUNG! andere Nummerierung als beim ColorChange)
        ColorChange cc;
        Logic logic = new Logic();

        //init obstacles
        for(int i=1; i<bb.SIZE-1; i++) //x
            for(int j=1; j<bb.SIZE-1; j++) //y
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

            //manual control
            Scanner sc = new Scanner(System.in);
            try {
                x = Float.parseFloat(sc.nextLine());
                y = Float.parseFloat(sc.nextLine());
            } catch (NumberFormatException nu) {
                log.info("NumberFormatError: hold on");
                x=0; y=0;
            }

            //manual walls inspection
            if(x>-1 && x<33 && y>-1 && y<33)
                log.info("wall check for x/y: " + nc.isWall(Math.round(x), Math.round(y))); //true wenn bei Koordinate 7,11 ein Hindernis steht

            //random cube and eraser
            xr = rand.nextFloat(); yr = rand.nextFloat();
            if(switcher) {
                nc.setMoveDirection(0, xr, yr);
                nc.setMoveDirection(1, -1*xr, -1*yr);
                switcher=false;
                log.info("x=" + x + " y=" + y + " | xr=" + xr + " yr=" + yr);
            } else {
                nc.setMoveDirection(0, xr, -1*yr);
                nc.setMoveDirection(1, -1*xr, yr);
                switcher=true;
                log.info("x=" + x + " y=" + y + " | xr=" + xr + " yr=" + yr);
            }
            log.info("swith=" + switcher);

            //eraser
//            nc.setMoveDirection(0, -5.1f, -0.8f);

//            cube
//            nc.setMoveDirection(1, x, y);

//            pyramid
            nc.setMoveDirection(2, x, y);

            // cc in eigene Struktur einarbeiten
            while ((cc = nc.getNextColorChange()) != null) {
//                z.B. brett[cc.x][cc.y] = cc.newColor;
//                cc.newColor; //0 = leer, 1-4 = spieler
                log.info("cc new color=" + cc.newColor + " cc.x=" + cc.x + " cc.y=" + cc.y);
            }
        }
    }
}
