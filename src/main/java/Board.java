import lenz.htw.tiarait.net.NetworkClient;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class Board {
    static final int SIZE = 32;
    static final int WALL = -1;

    int owner;
    TreeSet<Integer> enemies;
//    int[] scores;
    int[][] bb;
    NetworkClient nc;
    CellNode middle; //middle of every path search to limit inspected area

    public Board(int owner) {
        this.owner = owner;
        enemies = new TreeSet<Integer>() {{
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

    public Board(NetworkClient nc) {
        this(nc.getMyPlayerNumber() + 1);
        this.nc = nc;
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
        for(int y=SIZE-1; y>-1; y--) { //x
            System.out.println();
            for (int x = 0; x < SIZE; x++) { //y
                System.out.printf("%2d ", bb[x][y]);
            }
        }
    }

    double getDistance(Cell source, Cell target, boolean manhattan) {
        double xd = Math.abs(target.x-source.x);
        double yd = Math.abs(target.y-source.y);

        if(manhattan) return xd + yd;
        return Math.hypot(xd, yd);
    }

    Cell getMoveVector(int bot, int target) {
        Cell source = getCoords(bot);
        return new Cell(Logic.getX(target) - source.x, Logic.getY(target) - source.y);
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
                for(int i=0; i<Board.SIZE; i++) //x
                    for(int j=0; j<Board.SIZE; j++) //y
                        if(notWall(i, j) && bb[i][j] != owner)
                            return new Cell(i, j);
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

    Stack<Integer> unfoldPath(int source, int target, HashMap<Integer, CellNode> data) {
        Stack<Integer> path = new Stack<>();
        do {
            if(target != source) //get rid of current position
                path.push(target);
            target = data.get(target).prev;
        } while(target != -1);
        return path;
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
        return bb[x][y] != -1;
    }

    private boolean isReasonable(int x, int y) {
        return getDistance(new Cell(x, y), middle, true) < middle.weight * 2;
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
