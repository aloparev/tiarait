import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Stack;

import static org.junit.Assert.*;

public class BoardTest {
    static Board board;

    @Before
    public void setup() {
        board = new Board(1);
    }

    @Test
    public void printArena() {
        board.bb[12][29] = Board.WALL;
        board.printArena();
    }

    @Test
    public void getZz() {
        assertEquals(146, Logic.getZz(18, 4));
        assertEquals(395, Logic.getZz(11, 12));
    }

    @Test
    public void getXy() {
        Cell one = Logic.getXy(146);
        Cell two = Logic.getXy(395);

        assertEquals(18, one.x);
        assertEquals(4, one.y);

        assertEquals(11, two.x);
        assertEquals(12, two.y);
    }

    @Test
    public void dijkstra() {
        board.bb[11][29] = 2;
        Stack<Integer> pathFrom_1229_to_0929 = board.dijkstra(new Cell(12, 29), new Cell(9, 29));
        System.out.println(pathFrom_1229_to_0929);
    }

    @Test
    public void unfoldPath() {
        int target = 937; //9/29
        HashMap<Integer, CellNode> nodes = new HashMap<Integer, CellNode>() {{
            put(937, new CellNode(target, 2, 6, 938));
            put(938, new CellNode(938, 2, 4, 939));
            put(939, new CellNode(939, 2, 2, 940));
            put(940, new CellNode(938, 0, 0, -1));
        }};
        Stack<Integer> path = board.unfoldPath(940, target, nodes);
        System.out.println(path);
    }
}