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

    public Cell(int zz) {
        this.x = Logic.getX(zz);
        this.y = Logic.getY(zz);
        this.zz = zz;
    }

    public Cell(int x, int y, int zz) {
        this.x = x;
        this.y = y;
        this.zz = zz;
    }

    public Cell(float x, float y) {
        this((int) x, (int) y);
    }

    @Override
    public String toString() {
        return "Cell{" + x + "/" + y + " (" + zz + ")" + '}';
    }
}
