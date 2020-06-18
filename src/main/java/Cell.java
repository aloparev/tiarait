public class Cell {
    int x, y, weight, zz;

    public Cell(int x, int y, int weight) {
        this.x = x;
        this.y = y;
        this.weight = weight;
        this.zz = Logic.getZz(x, y);
    }

    public Cell(int x, int y) {
        this(x, y, 0);
    }

    public Cell(Cell that, int weight) {
        this(that.x, that.y, weight);
    }
}
