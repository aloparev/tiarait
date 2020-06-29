import lenz.htw.tiarait.ColorChange;
import lenz.htw.tiarait.net.NetworkClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * player colors ( != player number)
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

    //milliseconds
    public static final int CUBE_DELAY = 800;
    public static final int PYRAMID_DELAY = 800;

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
        int position = -1;
        int lastPosition;
        int zz;

        log.info("testing bots location for player = " + nc.getMyPlayerNumber());
        log.info("player1-red.bot0 x=" + nc.getX(0, 0) + " y=" + nc.getY(0, 0));
        log.info("player1-red.bot1 x=" + nc.getX(0, 1) + " y=" + nc.getY(0, 1));
        log.info("player1-red.bot2 x=" + nc.getX(0, 2) + " y=" + nc.getY(0, 2));

        log.info("player2-blu.bot0 x=" + nc.getX(1, 0) + " y=" + nc.getY(1, 0));
        log.info("player2-blu.bot1 x=" + nc.getX(1, 1) + " y=" + nc.getY(1, 1));
        log.info("player2-blu.bot2 x=" + nc.getX(1, 2) + " y=" + nc.getY(1, 2));

        log.info("player3-yel.bot0 x=" + nc.getX(2, 0) + " y=" + nc.getY(1, 0));
        log.info("player3-yel.bot1 x=" + nc.getX(2, 1) + " y=" + nc.getY(1, 1));
        log.info("player3-yel.bot2 x=" + nc.getX(2, 2) + " y=" + nc.getY(1, 2));

        log.info("player4-gre.bot0 x=" + nc.getX(3, 0) + " y=" + nc.getY(1, 0));
        log.info("player4-gre.bot1 x=" + nc.getX(3, 1) + " y=" + nc.getY(1, 1));
        log.info("player4-gre.bot2 x=" + nc.getX(3, 2) + " y=" + nc.getY(1, 2));

        while (nc.isAlive()) {
            //eraser
//            if(eraserStack.isEmpty()) {
//                eraserStack = board.analyseAndGetStack(ERASER);
//                board.stop(ERASER);
//                log.info("eraser stack init: " + cubeStack);
//            }
//            else if(gameRunning) {
//                log.info("eraser stack: " + cubeStack);
//                zz = eraserStack.pop();
//
//                while(board.getDistanceEuclid(board.getCoords(ERASER), Logic.getCellFromZz(zz)) > 1) {
//                    lastPosition = position;
//                    position = board.getCoords(ERASER).zz;
//                    move = board.getMoveVector(ERASER, zz);
//
//                    if(lastPosition == position) {
//                        board.sendRandomly(ERASER);
//                    }
//                    else {
//                        nc.setMoveDirection(ERASER, move.x, move.y);
//                    }
//                    TimeUnit.MILLISECONDS.sleep(CUBE_DELAY);
//                }
//            }

            //cube
            if(cubeStack.isEmpty()) {
                cubeStack = board.analyseAndGetStack(CUBE);
//                board.sendRandomly(CUBE);
                log.info("cube stack init: " + cubeStack);
                board.stop(CUBE);
            }
            else if(gameRunning) {
                log.info("cube stack: " + cubeStack);
                zz = cubeStack.pop();
//                log.info("cube zz: " + zz);
//                log.info("board.getDistanceEuclid(board.getCoords(CUBE), Logic.getCellFromZz(zz)): " + board.getDistanceEuclid(board.getCoords(CUBE), Logic.getCellFromZz(zz)));

                move = board.getMoveVector(CUBE, zz);
                nc.setMoveDirection(CUBE, move.x, move.y);
                log.info("stepping from " + board.getCoords(CUBE) + " to " + zz);

                //slow down approaching
                if((board.getDistanceManhattan(board.getCoords(CUBE), Logic.getCellFromZz(zz)) < 2)) {
                    do {
//                        lastPosition = position;
//                        position = board.getCoords(CUBE).zz;
                        board.stop(CUBE);
                        move = board.getMoveVector(CUBE, zz);
                        nc.setMoveDirection(CUBE, move.x, move.y);
                        TimeUnit.MILLISECONDS.sleep(100);
                    } while(board.getCoords(CUBE).zz != zz);
//                    log.info("on my way to the next cell");
                }

                board.stop(CUBE);
                //trouble here
//                while(board.getDistanceEuclid(board.getCoords(CUBE), Logic.getCellFromZz(zz)) > 1) {
//                    lastPosition = position;
//                    position = board.getCoords(CUBE).zz;
//                    move = board.getMoveVector(CUBE, zz);
//
//                    if(lastPosition == position) {
//                        board.sendRandomly(CUBE);
//                        log.info("CUBE RANDOM");
//                    }
//                    else {
//                        nc.setMoveDirection(CUBE, move.x, move.y);
//                        log.info("CUBE REAL: position=" + position + " lastPosition" + lastPosition + " target=" + zz);
//                    }
//                    TimeUnit.MILLISECONDS.sleep(CUBE_DELAY);
//                }
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
//                log.info("cc update: bb[" + cc.x + "][" + cc.y + "] = " + cc.newColor);
            }
        }
    }
}
