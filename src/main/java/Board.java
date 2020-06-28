import lenz.htw.tiarait.net.NetworkClient;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * direction scrutiny is always clockwise,
 * beginning at 12
 */
@Slf4j
public class Board {
    static final int SIZE = 32;
    static final int WALL = -1;

    int owner;
    TreeSet<Integer> enemies;
    TreeSet<Integer> unreachable;
//    int[] scores;
    int[][] bb;
    NetworkClient nc;
    CellNode middle; //middle of every path search to limit inspected area
    Random rand;

    public Board(int owner) {
        this.owner = owner;
        log.info("MyPlayerNumber=" + owner);

        enemies = new TreeSet<Integer>() {{
            add(1);
            add(2);
            add(3);
            add(4);
            remove(owner);
        }};
//        unreachable = new TreeSet<>();
//        scores = new int[] {0, 0, 0, 0}; //plus init position
        bb = new int[SIZE][SIZE];
        rand = new Random();
        initWalls();
        initObstacles();
    }

    public Board(NetworkClient nc) {
        this(nc.getMyPlayerNumber());
        this.nc = nc;
    }

    /**
     * marks obstacles within arena,
     * including badly accessible cells
     */
    void initObstacles() {
        log.info("init obstacles");
        for(int yy=0; yy<Board.SIZE; yy++)
            for (int xx = 0; xx < Board.SIZE; xx++)
                if(nc.isWall(xx, yy) || moreThanOneWallNearby(xx, yy)) {
                    bb[xx][yy] = Board.WALL;
                }
        printArena();
    }

    boolean moreThanOneWallNearby(int x, int y) {
        int walls = 0;

        if(bb[x][y+1] == -1) walls++;
        if(bb[x+1][y] == -1) walls++;
        if(bb[x][y-1] == -1) walls++;
        if(bb[x-1][y] == -1) walls++;

        return walls > 1;
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

    void printArena() {
        for(int y=SIZE-1; y>-1; y--) {
            System.out.println();
            for (int x = 0; x < SIZE; x++) {
                System.out.printf("%2d ", bb[x][y]);
            }
        }
    }

    void printCellColor(int x, int y) {
        System.out.println("owner=" + owner + " | bb[" + x + "][" + y + "]=" + bb[x][y]);
    }

    float getRandom() {
        return (float) (rand.nextFloat() - .5);
    }

    void stop(int bot) {
        nc.setMoveDirection(bot, 0, 0);
    }

    void sendRandomly(int bot) {
        nc.setMoveDirection(bot, getRandom(), getRandom());
    }

    void detachFromWall(int bot) {
        Cell pos = getCoords(bot);

//        if(nc.isWall(pos.x, pos.y+1))
            nc.setMoveDirection(bot, 0, -1);

//        if(nc.isWall(pos.x+1, pos.y))
            nc.setMoveDirection(bot, -1, 0);

//        if(nc.isWall(pos.x, pos.y-1))
            nc.setMoveDirection(bot, 0, 1);

//        if(nc.isWall(pos.x-1, pos.y))
            nc.setMoveDirection(bot, 1, 0);
    }

    double getDistanceManhattan(Cell source, Cell target) {
        double xd = Math.abs(target.x-source.x);
        double yd = Math.abs(target.y-source.y);
        return xd + yd;
    }

    double getDistanceManhattan(int source, int target) {
        return getDistanceManhattan(Logic.getCellFromZz(source), Logic.getCellFromZz(target));
    }

    double getDistanceEuclid(Cell source, Cell target) {
        double xd = Math.abs(target.x-source.x);
        double yd = Math.abs(target.y-source.y);
        return Math.hypot(xd, yd);
    }

    double getDistanceEuclid(int source, int target) {
        return getDistanceEuclid(Logic.getCellFromZz(source), Logic.getCellFromZz(target));
    }

    Cell getMoveVector(int bot, int targetInit) {
        Cell so = getCoords(bot);
        Cell ta = Logic.getCellFromZz(targetInit);
        Cell ans = new Cell(getRandom(), getRandom());

        //up
        if(ta.y > so.y)
            ans = new Cell(0, 1);

        //right
        if(ta.x > so.x)
            ans = new Cell(1, 0);

        //down
        if(ta.y < so.y)
            ans = new Cell(0, -1);

        //left
        if(ta.x < so.x)
            ans = new Cell(-1, 0);

        //top right corner
        if ((ta.x == so.x - 1 && ta.y == so.y + 1)
                || (ta.x == so.x + 1 && ta.y == so.y - 1))
            ans = new Cell(1, 1);

        //bottom right corner
        if ((ta.x == so.x - 1 && ta.y == so.y - 1)
                || (ta.x == so.x + 1 && ta.y == so.y + 1))
            ans = new Cell(1, -1);

        //bottom left corner
        if ((ta.x == so.x + 1 && ta.y == so.y - 1)
                || (ta.x == so.x - 1 && ta.y == so.y + 1))
            ans = new Cell(1, 1);

        //top left corner
        if ((ta.x == so.x + 1 && ta.y == so.y + 1)
                || (ta.x == so.x - 1 && ta.y == so.y - 1))
            ans = new Cell(1, -1);

        log.info("getMoveVector: move=" + ans);
        return ans;
    }

    Cell getMoveVector(float sourceX, float sourceY, int target) {
        return new Cell(Logic.getX(target) - sourceX, Logic.getY(target) - sourceY);
    }

    Stack<Integer> getStack(int bot) {
        return evaluate(bot);
    }

    Stack<Integer> evaluate(int bot) {
        return dijkstra(getCoords(bot), findTarget(bot));
    }

    Cell findTarget(int bot) {
        switch (bot) {
            case 0:

            //find the next cell from someone else
            case 1:
//                for(int i=0; i<Board.SIZE; i++) //x
//                    for(int j=0; j<Board.SIZE; j++) //y
//                        if(notWall(i, j) && bb[i][j] != owner)
//                            return new Cell(i, j);
                for(int y=0; y<SIZE; y++) {
                    for (int x = 0; x < SIZE; x++) {
                        if (notWall(x, y) && bb[x][y] != owner) {
                            printCellColor(x, y);
                            log.info("cube target = " + x + "/" + y);
                            return new Cell(x, y);
                        }
                    }
                }
            case 2:
            default: return getCoords(bot);
        }
    }

    Cell getCoords(int bot) {
        return new Cell(nc.getX(owner, bot), nc.getY(owner, bot));
    }

    /**
     * calculates path from the current bot position to the given target
     */
    Stack<Integer> dijkstra(Cell sourceInit, Cell targetInit) {
        middle = getMiddle(sourceInit, targetInit);
        CellNode source = new CellNode(sourceInit);
        CellNode target = new CellNode(targetInit);
        source.dist = 0; //init distance

        HashMap<Integer, CellNode> nodes = new HashMap<Integer, CellNode>() {{
            put(source.zz, source);
        }};
//        Stack<CellNode> queue = new Stack<CellNode>() {{
//            add(source);
//        }};
        Queue<CellNode> queue = new LinkedList<CellNode>() {{
            add(source);
        }};
        Set<Integer> examinedNodes = new HashSet<>();

        while(!queue.isEmpty()) {
            CellNode node = queue.remove();
            List<CellNode> neighbors = getNeighbors(node);

            for(CellNode neighbor : neighbors) {
                int nid = neighbor.zz;
                if(!nodes.containsKey(nid))
                    nodes.put(nid, neighbor);

                //relaxation
                int newDistance = node.dist + neighbor.weight;
                if(newDistance < nodes.get(nid).dist) {
                    nodes.get(nid).dist = newDistance;
                    nodes.get(nid).prev = node.zz;

                    //infinite loop protection
                    if(!examinedNodes.contains(neighbor.zz)) {
                        queue.add(neighbor);
                        examinedNodes.add(neighbor.zz);
                    }
                }
            }
        }

        return unfoldPath(source.zz, target.zz, nodes);
    }

    CellNode getMiddle(Cell sourceInit, Cell targetInit) {
        int x = (sourceInit.x + targetInit.x) / 2;
        int y = (sourceInit.y + targetInit.y) / 2;
        int xd = Math.abs(targetInit.x - sourceInit.x);
        int yd = Math.abs(targetInit.y - sourceInit.y);
        int weight = Math.max(xd, yd);
        return new CellNode(x, y, weight);
    }

    Stack<Integer> unfoldPath(int source, int target, HashMap<Integer, CellNode> nodes) {
//        if(nodes.size() == 1)
//            markAsWallAndReturnRandom(target);

        Stack<Integer> path = new Stack<>();

        try {
            do {
                if (target != source) { //get rid of current position
                    path.push(target);
                    target = nodes.get(target).prev;
                }
            } while (nodes.get(target).prev != -1);
        } catch (NullPointerException ee) {
            return markAsWallAndReturnRandom(target);
        }

        log.info("unfolding: source=" + source + ", target=" + target + ", path=" + path);
        return path;
    }

    Stack<Integer> markAsWallAndReturnRandom(int zz) {
        int x = Logic.getX(zz);
        int y = Logic.getY(zz);
        bb[x][y] = WALL;

        return new Stack<Integer>() {{
            add(Logic.getZz(getRandom(), getRandom()));
        }};
    }

    /**
     * clockwise from 12 to 3 to 6 and 9
     */
    List<CellNode> getNeighbors(Cell source) {
        List<CellNode> ans = new ArrayList<>();

        if(notWallAndNotTooFar(source.x, source.y+1))
            ans.add(createNode(source.x, source.y+1));
        if(notWallAndNotTooFar(source.x+1, source.y))
            ans.add(createNode(source.x+1, source.y));
        if(notWallAndNotTooFar(source.x, source.y-1))
            ans.add(createNode(source.x, source.y-1));
        if(notWallAndNotTooFar(source.x-1, source.y))
            ans.add(createNode(source.x-1, source.y));

        return ans;
    }

    private boolean notWallAndNotTooFar(int x, int y) {
        return notWall(x, y) && isReasonable(x, y);
    }

    private boolean notWallAndNotTooFar(Cell cell) {
        return notWall(cell.x, cell.y) && isReasonable(cell.x, cell.y);
    }

    private boolean notWall(int x, int y) {
        if(x > -1 && x < SIZE && y > -1 && y < SIZE)
            return bb[x][y] != -1;
        else
            return false;
    }

    private boolean isReasonable(int x, int y) {
        return getDistanceManhattan(new Cell(x, y), middle) < middle.weight * 2;
    }

    private CellNode createNode(int x, int y) {
        return new CellNode(x, y, getWeight(x, y));
    }

    private int getWeight(int x, int y) {
        int color = bb[x][y];

        if(enemies.contains(color)) return 1;
        else if(color == 0) return 2;
        else return 64;
    }
}
