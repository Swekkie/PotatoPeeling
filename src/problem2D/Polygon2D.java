package problem2D;

import javax.naming.OperationNotSupportedException;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Polygon2D {
    public double area;
    public double xMax, xMin, yMax, yMin;
    public double longestEdge; // only used in generating init solution

    // points are listed in counter-clockwise direction
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

    // calculates the area of a polygon (only useful if convex)
    // returns it and stores it in the object
    public double calculateArea() {
        // shoelace algorithm
        double area = 0;
        int n = point2DList.size();
        for (int i = 0, j = n - 1; i < n; j=i++) {
            Point2D p1 = point2DList.get(i);
            Point2D p2 = point2DList.get(j);
            area += (p2.getX() + p1.getX()) * (p2.getY() - p1.getY());
        }
        this.area = Math.abs(area / 2);
        return this.area;

//        double area = 0;
//        int n = point2DList.size();
//        for(int i = 0,j=n-1; i<n;j=i++){
//            Point2D p1 = point2DList.get(i);
//            Point2D p2 = point2DList.get(j);
//            area+= p2.getX()*p1.getY()-p1.getX()*p2.getY();
//        }
//        this.area = Math.abs(area / 2);
//        return this.area;

    }

    // checks if the polygon is convex and non intersecting
    public boolean isConvex() {
        // find heighest point that is base point for testing non intersection
        double ymax = 0;
        int indexYmax = 0;
        for (int i = 0; i < point2DList.size(); i++) {
            Point2D p = point2DList.get(i);
            if (p.getY() > ymax) {
                ymax = p.getY();
                indexYmax = i;
            }
        }

        Point2D basePoint = point2DList.get(indexYmax);
        int x = (indexYmax + 1) % point2DList.size();
        int y = (indexYmax + 2) % point2DList.size();

        for (int i = 0, j = point2DList.size() - 1; i < point2DList.size(); j = i++) {
            int k = (i + 1) % point2DList.size();
            Point2D pointI = point2DList.get(i);
            Point2D pointJ = point2DList.get(j);
            Point2D pointK = point2DList.get(k);

            if (i < point2DList.size() - 3) {
                Point2D pointX = point2DList.get(x);
                Point2D pointY = point2DList.get(y);
                if (!pointY.isOnLeftOfVector(basePoint, pointX))
                    return false;
                x = (x + 1) % point2DList.size();
                y = (y + 1) % point2DList.size();

            }
            // check for convex
            if (!pointK.isOnLeftOfVector(pointJ, pointI))
                return false;

        }

        return true;


    }

    // Does not remove points that are part of the polygon
    // Make sure those points are not in the pointToCheck set
    // Only useful for convex polygons
    public boolean isEmptyWithoutRemove(Set<Point2D> pointsToCheck) {
        updateBoundaries(); // updates the extrema of the polygon
        for (Point2D p : pointsToCheck) {
            if (isPointInPolygon(p)) {
                return false;
            }
        }
        return true;
    }

    // Only useful for convex polygons
    // Removes points that are part of the polygon (may not be checked)
    public boolean isEmpty(List<Point2D> points) {
        updateBoundaries(); // updates the extrema of the polygon
        List<Point2D> pointsToCheck = new ArrayList<>(points);
        pointsToCheck.removeAll(point2DList); // dont check points that make up the polygon
        for (Point2D p : pointsToCheck) {
            if (isPointInPolygon(p)) {
                return false;
            }
        }
        return true;
    }

    // iterates through all points and updates the extrema
    // xMax, xMin, yMax, yMin
    public void updateBoundaries() {
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

    // only for convex polygons, check if a point is inside of the polygon
    // points on the edges are defined as inside of the polygon
    private boolean isPointInPolygon(Point2D p) {
        if (p.getX() < xMin || p.getX() > xMax || p.getY() < yMin || p.getY() > yMax) {
            return false;
        }

        // iterate counter clockwise through sides (side from point j to point i)
        // if point is on the left of all the vectors, it is inside of the polygon
        for (int i = 0, j = point2DList.size() - 1; i < point2DList.size(); j = i++) {
            if (!p.isOnLeftOfVector(point2DList.get(j), point2DList.get(i))) {
                return false;
            }
        }
        return true;
    }


    public boolean hasAtLeastThreePoints() {
        return point2DList.size() > 2;
    }


    // a polygon is valid when it has at least 3 points and has no duplicate points
    public boolean isValid() {
        if (!hasAtLeastThreePoints())
            return false;
        Set<Point2D> appeared = new HashSet<>();
        for (Point2D p : point2DList) {
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
        if (hasAtLeastThreePoints()) {
            String s = "";
            for (Point2D p : point2DList) {
                s += (p + "\n");
            }
            return s;
        } else return "Polygon has not got at least 3 points";
    }

    public String idsOfPoints() {
        String s = "";
        for (int i = 0; i < point2DList.size() - 1; i++)
            s += point2DList.get(i).getId() + " ";
        s += point2DList.get(point2DList.size() - 1).getId();

        return s;
    }
}
