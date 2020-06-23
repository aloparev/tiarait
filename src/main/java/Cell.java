/**
 * cell holds only field coordinates
 * including unique id
 */
public class Cell {
    int x, y; //cell 2d coords
    int zz; //cell one number coord

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.zz = Logic.getZz(x, y);
    }

    public Cell(float x, float y) {
        this((int) x, (int) y);
    }
}
