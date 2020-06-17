public class Logic {
    double getDistance(Cell source, Cell target, boolean manhattan) {
        double xd = Math.abs(target.x-source.x);
        double yd = Math.abs(target.y-source.y);

        if(manhattan)
            return xd + yd;

        return Math.hypot(xd, yd);
    }

    Cell getMoveVector(Cell source, Cell target) {
        return new Cell(target.x-source.x, target.y-source.y);
    }


}
