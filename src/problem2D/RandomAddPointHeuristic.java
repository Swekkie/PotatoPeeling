package problem2D;

import java.util.*;

public class RandomAddPointHeuristic {

    private List<Point2D> inputPoints; // input set points
    private List<Polygon2D> foundPolygons;
    private Random random;
    private Polygon2D maxPolygon = null;
    private double maxArea = 0;

    public RandomAddPointHeuristic(List<Point2D> inputPoints) {
        this.inputPoints = inputPoints;
        this.foundPolygons = new ArrayList<>();
        this.random = new Random();
    }

    public List<Polygon2D> solve(long timeInMillis) {
        // search multiple times for empty,convex polygons
        long startTime = System.currentTimeMillis();
        int i = 0;
        while (System.currentTimeMillis() - startTime < timeInMillis) {
            i++;
            Polygon2D polygon = searchForPolygonAttempt();
            if (polygon.hasAtLeastThreePoints()) {
                foundPolygons.add(polygon);

                // track maxPolygon
                if (polygon.calculateArea() > maxArea) {
                    maxPolygon = polygon;
                    maxArea = polygon.area;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("RANDOM ADD HEURISTIC");
        //System.out.print("Max polygon: " + maxPolygon);
        System.out.println("Area: " + maxPolygon.area);
        System.out.println("Time (ms): " + (endTime - startTime) + "    Iterations: " +i);
        return foundPolygons;
    }

    public Polygon2D searchForPolygonAttempt() {
        List<Point2D> usablePoints = new ArrayList<>(inputPoints);
        Polygon2D polygon = new Polygon2D();

        // pick random beginpair
        Collections.shuffle(usablePoints);
        Point2D start = usablePoints.remove(0);
        Point2D end = usablePoints.remove(0);
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
            boolean pointAdded = false;
            Point2D secondLast = polygon.getPoint2DList().get(polygon.numberOfPoints() - 1); // second last after adding a new point

            iter = usablePoints.listIterator();

            // add new point, if valid, pointAdded is true, if not, remove it and try other
            while (iter.hasNext()) {
                Point2D newPoint = iter.next();
                iter.remove();
                polygon.addPoint2DToEnd(newPoint);
                if(polygon.isEmpty(usablePoints)){
                    pointAdded = true;
                    break;
                }
                else{
                    polygon.removeLastPoint();
                }
            }
            if(pointAdded) {
                // remove points on right of new edge formed by newPoint
                iter = usablePoints.listIterator();
                while (iter.hasNext()) {
                    if (!iter.next().isOnLeftOfVector(secondLast, polygon.getPoint2DList().get(polygon.numberOfPoints() - 1))) {
                        iter.remove();
                    }
                }
            }
        }
        return polygon;
    }

}
