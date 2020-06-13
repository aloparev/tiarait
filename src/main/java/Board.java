import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Board {
    final int SIZE = 32;
    final int WALL = -1;
    int owner;
    int[] scores;
    int[][] board;

    public Board(int owner) {
        this.owner = owner;
        scores = new int[] {0, 0, 0, 0}; //plus init position
        board = new int[SIZE][SIZE];
        log.info("MyPlayerNumber=" + owner);
    }
}
