import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Stack;

import static org.junit.Assert.*;

public class BoardTest {
    static final double DELTA = 0.01;

    static Board board;
    static Cell cell_1229;
    static Cell cell_0929;

    @BeforeClass
    public static void setup() {
        board = new Board(1);
        cell_1229 = new Cell(12, 29);
        cell_0929 = new Cell(9, 29);
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
    public void getManhattanDistance() {
        assertEquals(2, board.getDistanceManhattan(new Cell(12, 29), new Cell(11, 28)), DELTA);
    }

    @Test
    public void getMiddle() {
        CellNode middle = board.getMiddle(cell_1229, cell_0929);
        assertEquals(10, middle.x);
        assertEquals(29, middle.y);
        assertEquals(3, middle.weight);
    }

    @Test
    public void dijkstra() {
        board.bb[11][29] = board.owner;
//        Stack<Integer> pathFrom_1229_to_0929 = board.dijkstra(cell_1229, new Cell(11, 28));
        Stack<Integer> pathFrom_1229_to_0929 = board.dijkstra(cell_1229, cell_0929);
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
        Stack<Integer> ans = new Stack<Integer>() {{
           add(937);
           add(938);
           add(939);
        }};
        assertEquals(ans, path);
        System.out.println(path);
    }

    @Test
    public void unfoldPath_44() {
        int target = 44;
        HashMap<Integer, CellNode> nodes = new HashMap<Integer, CellNode>() {{
            put(44, new CellNode(target, 0, 0, -1));
        }};
        Stack<Integer> path = board.unfoldPath(target, target, nodes);
        Stack<Integer> ans = new Stack<>();
        assertEquals(ans, path);
        System.out.println(path);
    }

    @Test
    public void dijkstra_44() {
        board.bb[14][2] = board.owner;
        board.bb[13][2] = board.owner;
        board.bb[12][2] = board.owner;
        board.bb[12][1] = board.owner;
        board.bb[13][1] = board.WALL;
        board.bb[11][1] = board.WALL;

        Stack<Integer> path = board.dijkstra(Logic.getCellFromZz(44), Logic.getCellFromZz(46));
        System.out.println(path);
    }

    @Test
    public void distanceEuclid() {
        System.out.println(board.getDistanceEuclid(622, 622));
    }
}