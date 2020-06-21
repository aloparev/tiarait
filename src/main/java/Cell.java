public class Cell {
    int x, y; //cell 2d coords
    int zz; //cell one number coord
    int weight;
    int dist; //total distance from source
    int prev; //previous cell

    public Cell(int x, int y, int weight) {
        this.x = x;
        this.y = y;
        this.weight = weight;
        this.zz = Logic.getZz(x, y);
        this.dist = Integer.MAX_VALUE; //infinity
        this.prev = -1; //undefined
    }

    public Cell(int x, int y) {
        this(x, y, 0);
    }

    public Cell(Cell that, int weight) {
        this(that.x, that.y, weight);
    }
}
