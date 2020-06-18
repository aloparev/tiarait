public class Logic {
    static int getZz(int x, int y) {
        return x + Board.SIZE *y;
    }

    static Cell getXy(int zz) {
        return new Cell(zz% Board.SIZE, zz/ Board.SIZE);
    }
}
