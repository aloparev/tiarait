import lenz.htw.tiarait.ColorChange;
import lenz.htw.tiarait.net.NetworkClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

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

    public static void main( String[] args ) throws InterruptedException {
        String team = "fox";
        String host = "127.0.0.1";

        if(args.length == 2) {
            host = args[0];
            team = args[1];
        } else log.info("no args for host & team submitted, using defaults");
        System.out.println("\ttiarait client up and running: host=" + host + " team=" + team);

        float x, y;
        boolean gameRunning = false;

        NetworkClient nc = new NetworkClient(host, team);
        Board board = new Board(nc); // 0-3 (ACHTUNG! andere Nummerierung als beim ColorChange)
        ColorChange cc;
        Stack<Integer> cubeStack = new Stack<>();
        Stack<Integer> pyramidStack = new Stack<>();
        Stack<Integer> eraserStack = new Stack<>();

        Cell move = null;
        Cell lastMove = null;
        int position = -1;
        int lastPosition;
        int stack = -1;
        int lastStack = -1;



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
            if(gameRunning) {
                board.sendRandomly(ERASER);
//                log.info("xr=" + xr + " yr=" + yr);
//            log.info("x=" + x + " y=" + y + " | xr=" + xr + " yr=" + yr);
            }

            //cube
            if(cubeStack.isEmpty()) {
                cubeStack = board.analyseAndGetStack(CUBE);
//
//                if(cubeStack.size() == 1) {
//                    lastStack = stack;
//                    stack = cubeStack.peek();
//                }
//
//                if(lastStack == stack)
                board.sendRandomly(CUBE);

                log.info("cube stack init: " + cubeStack);
                board.stop(CUBE);
            }
            else if(gameRunning) {
                log.info("cube stack: " + cubeStack);
                int zz = cubeStack.pop();

                while(board.getDistanceEuclid(board.getCoords(CUBE), Logic.getCellFromZz(zz)) > 1) {
//                    lastMove = move;
                    lastPosition = position;
                    position = board.getCoords(CUBE).zz;
                    move = board.getMoveVector(CUBE, zz);

                    if(lastPosition == position) {
                        board.sendRandomly(CUBE);
                        log.info("RANDOM");
                    }
                    else {
                        nc.setMoveDirection(CUBE, move.x, move.y);
                        log.info("REAL: position=" + position + " lastPosition" + lastPosition + " target=" + zz);
                    }
                    TimeUnit.MILLISECONDS.sleep(800);
                }
//                board.stop(CUBE);
            }

            //manual control PYRAMID
//            Scanner sc = new Scanner(System.in);
//            try {
//                x = Float.parseFloat(sc.nextLine());
//                y = Float.parseFloat(sc.nextLine());
//            } catch (NumberFormatException nu) {
//                log.info("NumberFormatError: hold on");
//                x=0; y=0;
//            }
//            nc.setMoveDirection(PYRAMID, x, y);
//                log.info("pyramyd coords: " + board.getCoords(PYRAMID));

            while ((cc = nc.getNextColorChange()) != null) {
                gameRunning = true;
                board.bb[cc.x][cc.y] = cc.newColor - 1;
                log.info("cc update: bb[" + cc.x + "][" + cc.y + "] = " + cc.newColor);
            }
        }
    }


}
