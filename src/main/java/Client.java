import lenz.htw.tiarait.ColorChange;
import lenz.htw.tiarait.net.NetworkClient;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
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
    public static final int DELAY = 50;     //milliseconds
    public static final int BOTS = 3;

    public static void main( String[] args ) {
        String team = "fox";
        String host = "127.0.0.1";

        if(args.length == 2) {
            host = args[0];
            team = args[1];
        } else log.info("no args for host & team submitted, using defaults");
        System.out.println("\ttiarait client up and running: host=" + host + " team=" + team);

        boolean gameRunning = false;
        NetworkClient nc = new NetworkClient(host, team);
        Board board = new Board(nc); // 0-3 (ACHTUNG! andere Nummerierung als beim ColorChange)
        ColorChange cc;
        List<Stack<Integer>> stacks = new ArrayList<Stack<Integer>>() {{
            add(ERASER, new Stack<>());
            add(CUBE, new Stack<>());
            add(PYRAMID, new Stack<>());

        }};
        Thread[] threads = new Thread[BOTS];

//        log.info("testing bots location for player = " + nc.getMyPlayerNumber());
//        log.info("player0-red.bot0 x=" + nc.getX(0, 0) + " y=" + nc.getY(0, 0));
//        log.info("player0-red.bot1 x=" + nc.getX(0, 1) + " y=" + nc.getY(0, 1));
//        log.info("player0-red.bot2 x=" + nc.getX(0, 2) + " y=" + nc.getY(0, 2));
//
//        log.info("player1-blu.bot0 x=" + nc.getX(1, 0) + " y=" + nc.getY(1, 0));
//        log.info("player1-blu.bot1 x=" + nc.getX(1, 1) + " y=" + nc.getY(1, 1));
//        log.info("player1-blu.bot2 x=" + nc.getX(1, 2) + " y=" + nc.getY(1, 2));
//
//        log.info("player2-yel.bot0 x=" + nc.getX(2, 0) + " y=" + nc.getY(1, 0));
//        log.info("player2-yel.bot1 x=" + nc.getX(2, 1) + " y=" + nc.getY(1, 1));
//        log.info("player2-yel.bot2 x=" + nc.getX(2, 2) + " y=" + nc.getY(1, 2));
//
//        log.info("player3-gre.bot0 x=" + nc.getX(3, 0) + " y=" + nc.getY(1, 0));
//        log.info("player3-gre.bot1 x=" + nc.getX(3, 1) + " y=" + nc.getY(1, 1));
//        log.info("player3-gre.bot2 x=" + nc.getX(3, 2) + " y=" + nc.getY(1, 2));

        while (nc.isAlive()) {
            for(int i = 0; i < BOTS; i++) {
                int bot = i;
                boolean finalGameRunning = gameRunning;

                threads[bot] = new Thread(() -> {
                    if(stacks.get(bot).isEmpty()) {
//                        board.stop(bot);
                        stacks.get(bot).addAll(board.analyseAndGetStack(bot));
                    }
                    else if(finalGameRunning) {
                        int zz = stacks.get(bot).pop();
                        Cell move = board.getMoveVector(bot, zz);
                        nc.setMoveDirection(bot, move.x, move.y);

                        if((board.getDistanceManhattan(board.getCoords(bot), Board.getCellFromZz(zz)) < 2)) {
                            do {
//                                board.stop(bot);
                                move = board.getMoveVector(bot, zz);
                                nc.setMoveDirection(bot, move.x, move.y);
                                try {
                                    TimeUnit.MILLISECONDS.sleep(DELAY);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } while (board.getCoords(bot).zz != zz);
                        }
//                        board.stop(bot);
                    }
                });
                threads[i].start();
            }

            for (int i = 0; i < BOTS; i++) { try {
                threads[i].join(); //wait till work finished
//                    log.info("END thread[" + i + "] updated score[" + i + "] to " + scores[i]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

//            float x, y;
////            Scanner sc = new Scanner(System.in);
////            try {
////                x = Float.parseFloat(sc.nextLine());
////                y = Float.parseFloat(sc.nextLine());
////            } catch (NumberFormatException nu) {
////                log.info("NumberFormatError: hold on");
////                x=0; y=0;
////            }
////            nc.setMoveDirection(PYRAMID, x, y);
////                log.info("pyramyd coords: " + board.getCoords(PYRAMID));

            while ((cc = nc.getNextColorChange()) != null) {
                gameRunning = true;
                if(cc.newColor == 0) {
//                    log.info("content=" + board.bb[cc.x][cc.y]);
                    board.scores[board.bb[cc.x][cc.y] - 1]--;
                }
                else {
                    board.scores[cc.newColor - 1]++;
                }

                board.bb[cc.x][cc.y] = cc.newColor;
//                log.info("cc update: bb[" + cc.x + "][" + cc.y + "] = " + cc.newColor);
            }
        }
    }
}
