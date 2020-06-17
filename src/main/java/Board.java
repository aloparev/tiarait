import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Board {
    final int SIZE = 32;
    final int WALL = -1;
    int owner;
//    int[] scores;
    int[][] board;

    public Board(int owner) {
        this.owner = owner;
//        scores = new int[] {0, 0, 0, 0}; //plus init position
        board = new int[SIZE][SIZE];
        initWalls();
        log.info("MyPlayerNumber=" + owner);
    }

    /**
     * mark not only the walls itself, but also the margins
     * so that we can ignore them in all calculations
     */
    void initWalls() {
        //left right margins
        Arrays.fill(board[0], WALL);
        Arrays.fill(board[31], WALL);

        //bottom left
        Arrays.fill(board[1], 0, 11, WALL);
        Arrays.fill(board[2], 0, 9, WALL);
        Arrays.fill(board[3], 0, 7, WALL);
        Arrays.fill(board[4], 0, 6, WALL);
        Arrays.fill(board[5], 0, 5, WALL);
        Arrays.fill(board[6], 0, 4, WALL);
        Arrays.fill(board[7], 0, 3, WALL);
        Arrays.fill(board[8], 0, 3, WALL);
        Arrays.fill(board[9], 0, 2, WALL);
        Arrays.fill(board[10], 0, 2, WALL);
        board[11][0]=WALL;
        board[12][0]=WALL;
        board[13][0]=WALL;
        board[14][0]=WALL;
        board[15][0]=WALL;

        //bottom right
        Arrays.fill(board[30], 0, 11, WALL);
        Arrays.fill(board[29], 0, 9, WALL);
        Arrays.fill(board[28], 0, 7, WALL);
        Arrays.fill(board[27], 0, 6, WALL);
        Arrays.fill(board[26], 0, 5, WALL);
        Arrays.fill(board[25], 0, 4, WALL);
        Arrays.fill(board[24], 0, 3, WALL);
        Arrays.fill(board[23], 0, 3, WALL);
        Arrays.fill(board[22], 0, 2, WALL);
        Arrays.fill(board[21], 0, 2, WALL);
        board[20][0]=WALL;
        board[19][0]=WALL;
        board[18][0]=WALL;
        board[17][0]=WALL;
        board[16][0]=WALL;

        //top left
        Arrays.fill(board[1], 21, SIZE, WALL);
        Arrays.fill(board[2], 23, SIZE, WALL);
        Arrays.fill(board[3], 25, SIZE, WALL);
        Arrays.fill(board[4], 26, SIZE, WALL);
        Arrays.fill(board[5], 27, SIZE, WALL);
        Arrays.fill(board[6], 28, SIZE, WALL);
        Arrays.fill(board[7], 29, SIZE, WALL);
        Arrays.fill(board[8], 29, SIZE, WALL);
        Arrays.fill(board[9], 30, SIZE, WALL);
        Arrays.fill(board[10], 30, SIZE, WALL);
        board[11][31]=WALL;
        board[12][31]=WALL;
        board[13][31]=WALL;
        board[14][31]=WALL;
        board[15][31]=WALL;

        //top right
        Arrays.fill(board[30], 21, SIZE, WALL);
        Arrays.fill(board[29], 23, SIZE, WALL);
        Arrays.fill(board[28], 25, SIZE, WALL);
        Arrays.fill(board[27], 26, SIZE, WALL);
        Arrays.fill(board[26], 27, SIZE, WALL);
        Arrays.fill(board[25], 28, SIZE, WALL);
        Arrays.fill(board[24], 29, SIZE, WALL);
        Arrays.fill(board[23], 29, SIZE, WALL);
        Arrays.fill(board[22], 30, SIZE, WALL);
        Arrays.fill(board[21], 30, SIZE, WALL);
        board[20][31]=WALL;
        board[19][31]=WALL;
        board[18][31]=WALL;
        board[17][31]=WALL;
        board[16][31]=WALL;

//        for(int i=0; i<SIZE/2; i++)
//            for(int j=0; j<SIZE; j++)
//                log.info(i + "/" + j + "=" + board[i][j]);
    }
}
