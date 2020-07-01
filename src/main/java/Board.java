import lenz.htw.tiarait.net.NetworkClient;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * board-brain class
 * when inspecting cells, the direction is always clockwise: 12-3-6-9
 */
@Slf4j
public class Board {

    //=======================================
    //===========BOARD INIT==================
    //=======================================

    static final int SIZE = 32;
    static final int WALL = -1;

    int owner;
    TreeSet<Integer> enemies;
//    HashMap<Integer, Integer> enemies;
//    TreeSet<Integer> visitedCube;
    int[] scores = new int[] {0, 0, 0, 0};
    int[][] bb;
    NetworkClient nc;
    CellNode middle; //middle of every path search to limit inspected area
    Random rand;
//    int cubeLine;
//    int zz;

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
//        enemies = new HashMap<>();
//        for(int i=1; i<5; i++)
//            if(owner != i) enemies.put(i, 0);
        log.info("enemies=" + enemies);

//        visitedCube = new TreeSet<>();
//        log.info("scores=" + Arrays.toString(scores));

        bb = new int[SIZE][SIZE];
        rand = new Random();
//        cubeLine = 1;
        initWalls();
    }

    public Board(NetworkClient nc) {
        this(nc.getMyPlayerNumber() + 1);
        this.nc = nc;
        initObstacles();
    }

    /**
     * marks obstacles within arena,
     * including badly accessible cells
     */
    void initObstacles() {
//        log.info("init obstacles");
        for(int yy=1; yy<Board.SIZE-1; yy++)
            for (int xx=1; xx < Board.SIZE-1; xx++) {
                if(nc.isWall(xx, yy)) bb[xx][yy] = Board.WALL;
                if(!nc.isWall(xx, yy) && moreThanTwoWallsAround(xx, yy)) bb[xx][yy] = Board.WALL;
            }
//        printArena();
    }

    boolean moreThanTwoWallsAround(int x, int y) {
        int walls = 0;

        if (rangeOk(x, y+1) && nc.isWall(x, y + 1)) walls++;
        if (rangeOk(x+1, y) && nc.isWall(x + 1, y)) walls++;
        if (rangeOk(x, y-1) && nc.isWall(x, y - 1)) walls++;
        if (rangeOk(x-1, y) && nc.isWall(x - 1, y)) walls++;

//        log.info("walls around: " + x + "/" + y + " (" + Logic.getZz(x, y) + ") >> " + walls);
        return walls > 1;
    }

    boolean rangeOk(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
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
//                if(enemyClose(x, y)) System.out.printf("%d/%d = enemy", x, y);
//                else System.out.printf("%2d ", 0);
            }
        }
        System.out.println();
    }

    void printCellColor(int x, int y) {
        log.info("printCellColor: owner=" + owner + " | bb[" + x + "][" + y + "]=" + bb[x][y]);
    }

    //=======================================
    //===========MOVING CMD==================
    //=======================================

    float getRandom() {
        return (float) (rand.nextFloat() - .5);
    }

    void stop(int bot) {
        nc.setMoveDirection(bot, 0, 0);
    }

    void sendRandomly(int bot) {
        nc.setMoveDirection(bot, getRandom(), getRandom());
    }

    //=======================================
    //===========DISTANCING==================
    //=======================================

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

    Cell getCoords(int bot) {
        return getCoords(owner, bot);
    }

    Cell getCoords(int player, int bot) {
        return new Cell(nc.getX(player-1, bot), nc.getY(player-1, bot));
    }

    //=======================================
    //================LOGIC==================
    //=======================================

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
            ans = new Cell(-1, -1);

        //top left corner
        if ((ta.x == so.x + 1 && ta.y == so.y + 1)
                || (ta.x == so.x - 1 && ta.y == so.y - 1))
            ans = new Cell(-1, 1);

//        log.info("gotMoveVector: " + ans);
        return ans;
    }

    Stack<Integer> analyseAndGetStack(int bot) {
        return dijkstra(getCoords(bot), findTarget(bot));
    }

    Cell findTarget(int bot) {
        switch (bot) {
            case 0:
                int strongestEnemy = -1, maxScore = -1;

                for(int enemy = 0; enemy < scores.length; enemy++) {
                    if (enemy+1 == owner) continue;

                    if(scores[enemy] > maxScore) {
                        maxScore = scores[enemy];
                        strongestEnemy = enemy+1;
                    }
                }

                if(strongestEnemy == -1) return new Cell(getRandom(), getRandom());

//                log.info("strongestEnemy=" + strongestEnemy);
                Cell myPosition = getCoords(Client.ERASER);
                Cell enemyCube = getCoords(strongestEnemy, Client.CUBE);
                Cell enemyPyramid = getCoords(strongestEnemy, Client.PYRAMID);

                if(getDistanceManhattan(myPosition, enemyCube) < getDistanceManhattan(myPosition, enemyPyramid))
                    return enemyCube;
                else
                    return enemyPyramid;

            case 1:
                for(int y = 1; y < SIZE; y++) {
                    for (int x = 1; x < SIZE; x++) {
                        if (notWall(x, y) && bb[x][y] != owner && noEnemyAround(x, y)) {
//                            log.info("found cube target = " + x + "/" + y);
                            return new Cell(x, y);
                        }
                    }
                }

            case 2:
            default: return getCoords(bot);
        }
    }


    boolean noEnemyAround(int x, int y) {
        if (enemyClose(x, y)) return false;

        if (enemyClose(x, y + 1)) return false;
        if (enemyClose(x+1, y + 1)) return false;

        if (enemyClose(x + 1, y)) return false;
        if (enemyClose(x + 1, y-1)) return false;

        if (enemyClose(x, y - 1)) return false;
        if (enemyClose(x-1, y - 1)) return false;

        if (enemyClose(x - 1, y)) return false;
        if (enemyClose(x - 1, y+1)) return false;

        return true;
    }

    boolean enemyClose(int x, int y) {
        if(!rangeOk(x, y)) return true;

        for(int enemy : enemies)
            for(int bot=0; bot < 3; bot++)
                if(Math.round(nc.getX(enemy-1, bot)) == x && Math.round(nc.getY(enemy-1, bot)) == y)
                    return true;

        return false;
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

    private boolean notWall(int x, int y) {
        if(rangeOk(x, y))
            return bb[x][y] != -1;
        else
            return false;
    }

    private boolean isReasonable(int x, int y) {
        return getDistanceManhattan(new Cell(x, y), middle) < middle.weight * 2;
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
        Stack<Integer> path = new Stack<>();

        try {
            do {
                if (target != source) { //get rid of current position
                    path.push(target);
                    target = nodes.get(target).prev;
                }
            } while (nodes.get(target).prev != -1);
        } catch (NullPointerException ee) {
            log.info("unfoldPath NullPointer");
            return new Stack<Integer>() {{
                add(Logic.getZz(getRandom(), getRandom()));
            }};
        }

//        log.info("unfolding: source=" + source + ", target=" + target + ", path=" + path);
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

    private CellNode createNode(int x, int y) {
        return new CellNode(x, y, getWeight(x, y));
    }

    private int getWeight(int x, int y) {
        int color = bb[x][y];

        if(enemies.contains(color)) return 1;
        else if(color == 0) return 10;
        else return 100;
    }
}
