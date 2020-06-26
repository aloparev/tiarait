import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Logic {
    static int getZz(int x, int y) {
        return x + Board.SIZE * y;
    }

    static int getZz(float x, float y) {
        return (int) (x + Board.SIZE * y);
    }

    static Cell getCellFromZz(int zz) {
        return new Cell(getX(zz), getY(zz));
    }

    static Cell getXy(int zz) {
        return new Cell(zz % Board.SIZE, zz / Board.SIZE);
    }

    static int getX(int zz) {
        return zz % Board.SIZE;
    }

    static int getY(int zz) {
        return zz / Board.SIZE;
    }

    static <T> List<T> reverseList(List<T> list) {
        return IntStream.range(0, list.size())
                .map(i -> (list.size() - 1 - i))	// IntStream
                .mapToObj(list::get)				// Stream<T>
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
