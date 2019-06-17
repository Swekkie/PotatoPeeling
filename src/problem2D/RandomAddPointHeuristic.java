package problem2D;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class RandomAddPointHeuristic {

    private List<Point2D> inputPoints; // input set points
    private List<Polygon2D> foundPolygons;
    private Random random;

    public RandomAddPointHeuristic(List<Point2D> inputPoints, Random random) {
        this.inputPoints = inputPoints;
        this.foundPolygons = new ArrayList<>();
        this.random = random;
    }

    public List<Polygon2D> solve(int attempts) {
        // search multiple times for empty,convex polygons
        for (int i = 0; i < attempts; i++) {
            Polygon2D polygon = searchForPolygonAttempt();
            if (polygon.hasAtLeastThreePoints())
                foundPolygons.add(polygon);
        }
        return foundPolygons;
    }

    public Polygon2D searchForPolygonAttempt() {
        List<Point2D> usablePoints = new ArrayList<>(inputPoints);
        Polygon2D polygon = new Polygon2D();

        // pick random beginpair
        int index = random.nextInt(usablePoints.size());
        Point2D start = usablePoints.remove(index);
        index = random.nextInt(usablePoints.size());
        Point2D end = usablePoints.remove(index);
        polygon.addPoint2DToEnd(start);
        polygon.addPoint2DToEnd(end);

        // remove points that are eliminated because of the fact that we want convex polygons
        // we choose counter clockwise, points on the left side (left of rising vector) are usable
        ListIterator<Point2D> iter = usablePoints.listIterator();
        while (iter.hasNext()) {
            if (!iter.next().isOnLeftOfVector(start, end)) {
                iter.remove();
            }
        }

        while (!usablePoints.isEmpty()) {
            // pick next point
            Point2D first = polygon.getFirstPoint();
            Point2D secondLast = polygon.getPoint2DList().get(polygon.numberOfPoints() - 1); // second last after adding a new point
            Point2D lastAdded = usablePoints.remove(0);
            polygon.addPoint2DToEnd(lastAdded);
            List<Point2D> tempCopy = new ArrayList<>(usablePoints);

            iter = usablePoints.listIterator();
            while (iter.hasNext()) {
                Point2D p = iter.next();
                if (!p.isOnLeftOfVector(secondLast, lastAdded)) {
                    // points on "clockwise" side of the vector
                    iter.remove();
                } else {
                    if (!p.isOnLeftOfVector(first, lastAdded)) {
                        // points inside polygon with new added point
                        // remove new added point and try adding a new point
                        polygon.removeLastPoint();
                        usablePoints = tempCopy;
                        break;
                    }
                }
            }

        }
        return polygon;
    }

}
