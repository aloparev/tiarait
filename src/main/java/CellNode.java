/**
 * node cell additionally holds dijkstra related data
 */
public class CellNode extends Cell {
    int weight;
    int dist; //total distance from source
    int prev; //previous cell

    public CellNode(int x, int y, int weight) {
        super(x, y);
        this.weight = weight;
        this.dist = Integer.MAX_VALUE; //infinity
        this.prev = -1; //undefined
    }

    public CellNode(Cell that) {
        super(that.x, that.y, that.zz);
        this.weight = 0; //if not provided, set to zero
        this.dist = Integer.MAX_VALUE; //infinity
        this.prev = -1; //undefined
    }

    public CellNode(int zz, int weight, int dist, int prev) {
        super(zz);
        this.weight = weight;
        this.dist = dist;
        this.prev = prev;
    }
}
