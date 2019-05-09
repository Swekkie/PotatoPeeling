package problem2D;

import java.util.*;


// points are listed in counter-clockwise direction
public class Polygon2D {
    public double area;
    private double xMax, xMin, yMax, yMin;
    public List<Point2D> point2DList;

    public Polygon2D() {
        point2DList = new ArrayList<>();
    }

    public Polygon2D(Polygon2D p) {
        point2DList = new ArrayList<>(p.getPoint2DList());
    }

    public Polygon2D(List<Point2D> point2DList) {
        this.point2DList = new ArrayList<>(point2DList);
    }

    public void addPoint2DToEnd(Point2D point) {
        point2DList.add(point);
    }

    public int numberOfPoints() {
        return point2DList.size();
    }

    public Point2D getFirstPoint() {
        return point2DList.get(0);
    }

    public void removeLastPoint() {
        point2DList.remove(point2DList.size() - 1);
    }

    public double calculateArea() {
        // shoelace algorithm
        double area = 0;
        int n = point2DList.size();
        int j = n - 1;
        for (int i = 0; i < n; i++) {
            Point2D p1 = point2DList.get(i);
            Point2D p2 = point2DList.get(j);
            double temp = (p2.getX() + p1.getX()) * (p2.getY() - p1.getY());
            area += (p2.getX() + p1.getX()) * (p2.getY() - p1.getY());
            j = i;
        }
        this.area = Math.abs(area / 2);
        return this.area;
    }

    public boolean isConvex() {
        for(int i = 0, j = point2DList.size() - 1; i < point2DList.size(); j = i++){
            int k = i + 1;
            if(k == point2DList.size())
                k = 0;
            if(!point2DList.get(k).isOnLeftOfVector(point2DList.get(j),point2DList.get(i)))
                return false;
        }

        return true;
    }

    public boolean isEmpty(List<Point2D> points) {
        updateBoundaries();
        List<Point2D> pointsToCheck = new ArrayList<>(points);
        pointsToCheck.removeAll(point2DList); // dont check points that make up the polygon
        for (Point2D p : pointsToCheck) {
            if (isPointInPolygon(p)) {
                return false;
            }
        }
        return true;
    }

    public boolean isFeasible(List<Point2D> allPoints){
        if(!isValid())
            return false;

        List<Point2D> usablePoints = new ArrayList<>(allPoints);
        for(int i = 0, j = point2DList.size() - 1; i < point2DList.size(); j = i++){
            int k = i + 1;
            if(k == point2DList.size())
                k = 0;

            Point2D start = point2DList.get(j);
            Point2D to = point2DList.get(i);
            Point2D next = point2DList.get(k);
            //usablePoints.remove(start);
            //usablePoints.remove(to);

            // remove points on right of vector
            ListIterator<Point2D> iter = usablePoints.listIterator();
            while (iter.hasNext()) {
                if (!iter.next().isOnLeftOfVector(start, to)) {
                    iter.remove();
                }
            }

            if(!usablePoints.contains(next))
                return false;
        }

        // check for emptyness
        usablePoints.removeAll(point2DList);
        for(Point2D p: usablePoints){
            if(p.isOnLeftOfVector(point2DList.get(0),point2DList.get(1))){
                return false;
            }
        }
        return true;
    }

    private void updateBoundaries() {
        xMax = Double.MIN_VALUE;
        yMax = Double.MIN_VALUE;
        xMin = Double.MAX_VALUE;
        yMin = Double.MAX_VALUE;

        for (Point2D p : point2DList) {
            xMax = Math.max(xMax, p.getX());
            xMin = Math.min(xMin, p.getX());
            yMax = Math.max(yMax, p.getY());
            yMin = Math.min(yMin, p.getY());

        }
    }

    // only for convex polygons
    private boolean isPointInPolygon(Point2D p) {
        if (p.getX() < xMin || p.getX() > xMax || p.getY() < yMin || p.getY() > yMax) {
            return false;
        }

        // iterate counter clockwise through sides (side from point j to point i)
        // if point is on the left of all the vectors, it is inside of the polygon
        for (int i = 0, j = point2DList.size() - 1; i < point2DList.size(); j = i++) {
            if (!p.isOnLeftOfVector(point2DList.get(j), point2DList.get(i))){
                return false;
            }
        }
        return true;
    }

    // a polygon is valid when it has at least 3 points and has no duplicate points
    public boolean isValid() {
        if (point2DList.size() < 3)
            return false;
        Set<Point2D> appeared = new HashSet<>();
        for (Point2D p: point2DList) {
            if (!appeared.add(p)) {
                return false;
            }
        }
        return true;
    }

    public List<Point2D> getPoint2DList() {
        return point2DList;
    }

    @Override
    public String toString() {
        if (isValid()) {
            String s = "";
            for (Point2D p : point2DList) {
                s += (p + "\n");
            }
            return s;
        } else return "Polygon is invalid";
    }

    public String idsOfPoints() {
        String s = "";
        for (int i = 0; i < point2DList.size() - 1; i++)
            s += point2DList.get(i).getId() + " ";
        s += point2DList.get(point2DList.size() - 1).getId();

        return s;
    }
}
