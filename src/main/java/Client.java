import lenz.htw.tiarait.ColorChange;
import lenz.htw.tiarait.net.NetworkClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

/**
 * player colors
 *  1=red
 *  2=blue
 *  3=yellow
 *  4=green
 *
 * bots
 *  0=eraser
 *  1=cube
 *  2=pyramid
 */
@Slf4j
public class Client {
    public static final int ERASER = 0;
    public static final int CUBE = 1;
    public static final int PYRAMID = 2;

    public static void main( String[] args ) {
        String team = "fox";
        String host = "127.0.0.1";

        if(args.length == 2) {
            host = args[0];
            team = args[1];
        } else log.info("no args for host & team submitted, using defaults");
        System.out.println("tiarait client up and running: host=" + host + " team=" + team);

        float x, y;
        float xr, yr;
        Random rand = new Random();
//        boolean switcher = true;

        NetworkClient nc = new NetworkClient(host, team);
        Board board = new Board(nc); // 0-3 (ACHTUNG! andere Nummerierung als beim ColorChange)
        ColorChange cc;
        Stack<Integer> cubeStack = new Stack<>();
        Stack<Integer> pyramidStack = new Stack<>();
        Stack<Integer> eraserStack = new Stack<>();

        //init obstacles
        for(int i=0; i<Board.SIZE-1; i++) //x
            for(int j=0; j<Board.SIZE-1; j++) //y
                if(nc.isWall(i, j)) {
//                    log.info(i + "/" + j + " isWall");
                    board.bb[i][j] = Board.WALL;
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
            //eraser >> random
            xr = (float) (rand.nextFloat() - .5);
            yr = (float) (rand.nextFloat() - .5);
            nc.setMoveDirection(ERASER, xr, yr);
            log.info("xr=" + xr + " yr=" + yr);
//            log.info("x=" + x + " y=" + y + " | xr=" + xr + " yr=" + yr);

            //cube
            if(cubeStack.isEmpty()) {
                cubeStack = board.getStack(CUBE);
                log.info("cube stack init");
            }
            else {
                int nextStep = cubeStack.pop();
                Cell coords = board.getMoveVector(CUBE, nextStep);
                nc.setMoveDirection(CUBE, coords.x, coords.y);
                log.info("cube stack pop: " + coords);
            }

            //manual control
//            Scanner sc = new Scanner(System.in);
//            try {
//                x = Float.parseFloat(sc.nextLine());
//                y = Float.parseFloat(sc.nextLine());
//            } catch (NumberFormatException nu) {
//                log.info("NumberFormatError: hold on");
//                x=0; y=0;
//            }
//
//            //manual walls inspection
//            if(x>-1 && x<Board.SIZE && y>-1 && y<Board.SIZE)
//                log.info("wall check for x/y: " + nc.isWall(Math.round(x), Math.round(y))); //true wenn bei Koordinate 7,11 ein Hindernis steht
//
////            pyramid
//            nc.setMoveDirection(PYRAMID, x, y);

            // cc in eigene Struktur einarbeiten
            while ((cc = nc.getNextColorChange()) != null) {
                board.bb[cc.x][cc.y] = cc.newColor;
//                cc.newColor; //0 = leer, 1-4 = spieler
                log.info("cc new color=" + cc.newColor + " cc.x=" + cc.x + " cc.y=" + cc.y);
            }
        }
    }


}
