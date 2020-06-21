import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Board {
    static final int SIZE = 32;
    static final int WALL = -1;
    int owner;
    TreeSet<Integer> enemies;
//    int[] scores;
    int[][] bb;

    public Board(int owner) {
        this.owner = owner;
        this.enemies = new TreeSet<Integer>() {{
                add(1);
                add(2);
                add(3);
                add(4);
                remove(owner);
            }};
//        scores = new int[] {0, 0, 0, 0}; //plus init position
        bb = new int[SIZE][SIZE];
        initWalls();
        log.info("MyPlayerNumber=" + owner);
    }

    /**
     * mark not only the walls itself, but also the margins
     * so that we can ignore them in all calculations
     */
    void initWalls() {
        //left right margins
        Arrays.fill(bb[0], WALL);
        Arrays.fill(bb[31], WALL);

        //bottom left
        Arrays.fill(bb[1], 0, 11, WALL);
        Arrays.fill(bb[2], 0, 9, WALL);
        Arrays.fill(bb[3], 0, 7, WALL);
        Arrays.fill(bb[4], 0, 6, WALL);
        Arrays.fill(bb[5], 0, 5, WALL);
        Arrays.fill(bb[6], 0, 4, WALL);
        Arrays.fill(bb[7], 0, 3, WALL);
        Arrays.fill(bb[8], 0, 3, WALL);
        Arrays.fill(bb[9], 0, 2, WALL);
        Arrays.fill(bb[10], 0, 2, WALL);
        bb[11][0]=WALL;
        bb[12][0]=WALL;
        bb[13][0]=WALL;
        bb[14][0]=WALL;
        bb[15][0]=WALL;

        //bottom right
        Arrays.fill(bb[30], 0, 11, WALL);
        Arrays.fill(bb[29], 0, 9, WALL);
        Arrays.fill(bb[28], 0, 7, WALL);
        Arrays.fill(bb[27], 0, 6, WALL);
        Arrays.fill(bb[26], 0, 5, WALL);
        Arrays.fill(bb[25], 0, 4, WALL);
        Arrays.fill(bb[24], 0, 3, WALL);
        Arrays.fill(bb[23], 0, 3, WALL);
        Arrays.fill(bb[22], 0, 2, WALL);
        Arrays.fill(bb[21], 0, 2, WALL);
        bb[20][0]=WALL;
        bb[19][0]=WALL;
        bb[18][0]=WALL;
        bb[17][0]=WALL;
        bb[16][0]=WALL;

        //top left
        Arrays.fill(bb[1], 21, SIZE, WALL);
        Arrays.fill(bb[2], 23, SIZE, WALL);
        Arrays.fill(bb[3], 25, SIZE, WALL);
        Arrays.fill(bb[4], 26, SIZE, WALL);
        Arrays.fill(bb[5], 27, SIZE, WALL);
        Arrays.fill(bb[6], 28, SIZE, WALL);
        Arrays.fill(bb[7], 29, SIZE, WALL);
        Arrays.fill(bb[8], 29, SIZE, WALL);
        Arrays.fill(bb[9], 30, SIZE, WALL);
        Arrays.fill(bb[10], 30, SIZE, WALL);
        bb[11][31]=WALL;
        bb[12][31]=WALL;
        bb[13][31]=WALL;
        bb[14][31]=WALL;
        bb[15][31]=WALL;

        //top right
        Arrays.fill(bb[30], 21, SIZE, WALL);
        Arrays.fill(bb[29], 23, SIZE, WALL);
        Arrays.fill(bb[28], 25, SIZE, WALL);
        Arrays.fill(bb[27], 26, SIZE, WALL);
        Arrays.fill(bb[26], 27, SIZE, WALL);
        Arrays.fill(bb[25], 28, SIZE, WALL);
        Arrays.fill(bb[24], 29, SIZE, WALL);
        Arrays.fill(bb[23], 29, SIZE, WALL);
        Arrays.fill(bb[22], 30, SIZE, WALL);
        Arrays.fill(bb[21], 30, SIZE, WALL);
        bb[20][31]=WALL;
        bb[19][31]=WALL;
        bb[18][31]=WALL;
        bb[17][31]=WALL;
        bb[16][31]=WALL;

//        for(int i=0; i<SIZE/2; i++)
//            for(int j=0; j<SIZE; j++)
//                log.info(i + "/" + j + "=" + board[i][j]);
    }

    double getDistance(Cell source, Cell target, boolean manhattan) {
        double xd = Math.abs(target.x-source.x);
        double yd = Math.abs(target.y-source.y);

        if(manhattan) return xd + yd;
        return Math.hypot(xd, yd);
    }

    Cell getMoveVector(Cell source, Cell target) {
        return new Cell(target.x-source.x, target.y-source.y);
    }

    Stack<Integer> dijkstra(Cell source, Cell target) {
        HashMap<Integer, Cell> allNodes = new HashMap<Integer, Cell>() {{
            put(source.zz, source);
        }};
        Stack<Cell> queue = new Stack<Cell>() {{
            add(source);
        }};

        while(!queue.isEmpty()) {
            Cell node = queue.pop();
            List<Cell> neighbors = getNeighbors(node);

            for(Cell neighbor : neighbors) {
                int nid = neighbor.zz;
                if(!allNodes.containsKey(nid))
                    allNodes.put(nid, neighbor);

                int newDistance = node.dist + neighbor.weight;
                if(newDistance < allNodes.get(nid).dist) {
                    allNodes.get(nid).dist = newDistance;
                    allNodes.get(nid).prev = node.zz;
                    queue.add(neighbor);
                }
            }
        }

        return unfoldPath(target.zz, allNodes);
    }

    Stack<Integer> unfoldPath(int target, HashMap<Integer, Cell> data) {
        Stack<Integer> path = new Stack<>();
        int newTarget = -1;
        do {
            path.push(target);
            newTarget = data.get(target).prev;
        } while(newTarget != -1);
        return path;
    }

    List<Cell> getNeighbors(Cell source) {
        List<Cell> ans = new ArrayList<>();
        if(notWall(source.x, source.y+1)) ans.add(createNode(source.x, source.y+1));
        if(notWall(source.x+1, source.y)) ans.add(createNode(source.x+1, source.y));
        if(notWall(source.x, source.y-1)) ans.add(createNode(source.x, source.y-1));
        if(notWall(source.x-1, source.y)) ans.add(createNode(source.x-1, source.y));
        return ans;
    }

    private boolean notWall(int x, int y) {
        return bb[x][y] != -1;
    }

    private Cell createNode(int x, int y) {
        return new Cell(x, y, getWeight(x, y));
    }

    private int getWeight(int x, int y) {
        int color = bb[x][y];

        if(enemies.contains(color)) return 1;
        else if(color == 0) return 2;
        else return 4;
    }
}
