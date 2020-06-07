import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Board {
    int owner;
    int[] scores;
    TreeSet<Integer> fre;
    TreeSet<Integer> wal;
    TreeSet<Integer> red;
    TreeSet<Integer> gre;
    TreeSet<Integer> blu;
    TreeSet<Integer> yel;

    public Board(int owner, int[] walls) {
        this.owner = owner;
        scores = new int[] {0, 0, 0, 0}; //plus init position
        wal = new TreeSet<>();
        fre = new TreeSet<>();

        for(int wall : walls) {
            wal.add(wall);
            fre.remove(wall);
        }
    }
}
