import lenz.htw.tiarait.ColorChange;
import lenz.htw.tiarait.net.NetworkClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * color change / player number
 *  0=empty
 *  1=red               0
 *  2=blue              1
 *  3=yellow            2
 *  4=green             3
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
    public static final int DELAY = 40;     //milliseconds

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
        Cell move;
        int zz;

        log.info("testing bots location for player = " + nc.getMyPlayerNumber());
        log.info("player0-red.bot0 x=" + nc.getX(0, 0) + " y=" + nc.getY(0, 0));
        log.info("player0-red.bot1 x=" + nc.getX(0, 1) + " y=" + nc.getY(0, 1));
        log.info("player0-red.bot2 x=" + nc.getX(0, 2) + " y=" + nc.getY(0, 2));

        log.info("player1-blu.bot0 x=" + nc.getX(1, 0) + " y=" + nc.getY(1, 0));
        log.info("player1-blu.bot1 x=" + nc.getX(1, 1) + " y=" + nc.getY(1, 1));
        log.info("player1-blu.bot2 x=" + nc.getX(1, 2) + " y=" + nc.getY(1, 2));

        log.info("player2-yel.bot0 x=" + nc.getX(2, 0) + " y=" + nc.getY(1, 0));
        log.info("player2-yel.bot1 x=" + nc.getX(2, 1) + " y=" + nc.getY(1, 1));
        log.info("player2-yel.bot2 x=" + nc.getX(2, 2) + " y=" + nc.getY(1, 2));

        log.info("player3-gre.bot0 x=" + nc.getX(3, 0) + " y=" + nc.getY(1, 0));
        log.info("player3-gre.bot1 x=" + nc.getX(3, 1) + " y=" + nc.getY(1, 1));
        log.info("player3-gre.bot2 x=" + nc.getX(3, 2) + " y=" + nc.getY(1, 2));

        while (nc.isAlive()) {
//=============================================================================
//====================================ERASER===================================
//=============================================================================
            if(eraserStack.isEmpty()) {// && !lastScores.equals(board.enemies)) {
                eraserStack = board.analyseAndGetStack(ERASER);
//                lastScores = board.scores.clone();
//                lastScores = new HashMap<>(board.enemies);
//                log.info("eraser stack init: " + cubeStack);
            }
            else if(gameRunning) {
//                log.info("eraser stack: " + cubeStack);
                zz = eraserStack.pop();
                move = board.getMoveVector(ERASER, zz);
                nc.setMoveDirection(ERASER, move.x, move.y);

                if((board.getDistanceManhattan(board.getCoords(ERASER), Logic.getCellFromZz(zz)) < 2)) {
                    do {
                        board.stop(ERASER);
                        move = board.getMoveVector(ERASER, zz);
                        nc.setMoveDirection(ERASER, move.x, move.y);
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                    } while(board.getCoords(ERASER).zz != zz);
                }
                board.stop(CUBE);
            }

//============================================================================
//=====================================CUBE===================================
//============================================================================
            if(cubeStack.isEmpty()) {
                cubeStack = board.analyseAndGetStack(CUBE);
//                board.sendRandomly(CUBE);
//                log.info("cube stack init: " + cubeStack);
                board.stop(CUBE);
            }
            else if(gameRunning) {
//                log.info("cube stack: " + cubeStack);
                zz = cubeStack.pop();
//                log.info("cube zz: " + zz);
//                log.info("board.getDistanceEuclid(board.getCoords(CUBE), Logic.getCellFromZz(zz)): " + board.getDistanceEuclid(board.getCoords(CUBE), Logic.getCellFromZz(zz)));

                move = board.getMoveVector(CUBE, zz);
                nc.setMoveDirection(CUBE, move.x, move.y);
//                log.info("stepping from " + board.getCoords(CUBE) + " to " + zz);

                //slow down approaching
                if((board.getDistanceManhattan(board.getCoords(CUBE), Logic.getCellFromZz(zz)) < 2)) {
                    do {
//                        lastPosition = position;
//                        position = board.getCoords(CUBE).zz;
                        board.stop(CUBE);
                        move = board.getMoveVector(CUBE, zz);
                        nc.setMoveDirection(CUBE, move.x, move.y);
                        TimeUnit.MILLISECONDS.sleep(DELAY);
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

//============================================================================
//====================================PYRAMID=================================
//============================================================================
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

            if(pyramidStack.isEmpty()) {
                pyramidStack = board.analyseAndGetStack(PYRAMID);
            }
            else if(gameRunning) {
//                log.info("eraser stack: " + cubeStack);
                zz = pyramidStack.pop();
                move = board.getMoveVector(PYRAMID, zz);
                nc.setMoveDirection(PYRAMID, move.x, move.y);

                if((board.getDistanceManhattan(board.getCoords(PYRAMID), Logic.getCellFromZz(zz)) < 2)) {
                    do {
                        board.stop(PYRAMID);
                        move = board.getMoveVector(PYRAMID, zz);
                        nc.setMoveDirection(PYRAMID, move.x, move.y);
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                    } while(board.getCoords(PYRAMID).zz != zz);
                }
                board.stop(PYRAMID);
            }

//            log.info("scores=" + Arrays.toString(board.scores));
            while ((cc = nc.getNextColorChange()) != null) {
                gameRunning = true;

//                log.info("enemy=" + board.bb[cc.x][cc.y] + " count=" + board.enemies.get(board.bb[cc.x][cc.y]));

//                if(cc.newColor == 0 && board.enemies.containsKey(board.bb[cc.x][cc.y])) {
                if(cc.newColor == 0) {
//                    log.info("content=" + board.bb[cc.x][cc.y]);
                    board.scores[board.bb[cc.x][cc.y] - 1]--;
//                    board.enemies.put(board.bb[cc.x][cc.y], board.enemies.get(board.bb[cc.x][cc.y])-1);
                }
                else {
//                    board.enemies.put(board.bb[cc.x][cc.y], board.enemies.get(board.bb[cc.x][cc.y]) + 1);
                    board.scores[cc.newColor - 1]++;
                }

                board.bb[cc.x][cc.y] = cc.newColor;
//                log.info("cc update: bb[" + cc.x + "][" + cc.y + "] = " + cc.newColor);
            }
        }
    }
}
