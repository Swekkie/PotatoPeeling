package problem2D;

import java.util.*;

public class RecursiveSolver {
    private List<Point2D> inputPoints; // input set points
    private List<Point2D> checkedStartPoints;
    private List<Polygon2D> foundPolygons;

    // variables for saving largest area polygon found yet
    private Polygon2D maxPolygon = null;
    private double maxArea = 0;

    public RecursiveSolver(List<Point2D> inputPoints) {
        this.inputPoints = inputPoints;
        this.checkedStartPoints = new ArrayList<>();
        this.foundPolygons = new ArrayList<>();
        //Collections.shuffle(inputPoints);
    }


    // search for all convex empty polygons and return them in a list
    // search is done in a recursive way, pruning where possible is done
    // does not do sorting of the list, or tracking largest (commented)
    public List<Polygon2D> solve() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < inputPoints.size(); i++) {

            Point2D startPoint = inputPoints.get(i);
            Polygon2D polygon = new Polygon2D();
            polygon.addPoint2DToEnd(startPoint);

            Set<Point2D> pointsLeft = new HashSet<>(inputPoints);
            pointsLeft.remove(startPoint);
            addPointRecursive(polygon, pointsLeft);

            // all polygons containing beginPoint have been searched
            // add to a list, if we come across this point (or every point in de checkedStartPoints list) again
            // we know we can prune from there, because we already went through all the polygons containing that point
            // this is the case because for example: polygon (1-2-3-4) is the same as (3-4-1-2)
            checkedStartPoints.add(inputPoints.get(i));

        }

        long endTime = System.currentTimeMillis();
        System.out.println("RECURSIVE");
        System.out.print("Max polygon: " + maxPolygon);
        System.out.println("Area: " + maxPolygon.area);
        System.out.println("Time (ms): " + (endTime-startTime));
        return foundPolygons;
    }


    public void addPointRecursive(Polygon2D polygon, Set<Point2D> pointsLeft) {
        for (Point2D newPoint : pointsLeft) {
            // if the newPoint is a point which has been a startPoint already
            // prune search because all polygons with that point have been searched already
            if (checkedStartPoints.contains(newPoint))
                continue;


            // add newPoint to polygon and update pointsLeft
            Polygon2D temp = new Polygon2D(polygon);
            temp.addPoint2DToEnd(newPoint);
            Set<Point2D> newPointsLeft = new HashSet<>(pointsLeft);
            newPointsLeft.remove(newPoint);

            // checking for convexness and emptiness is not needed when there are less than 3 points
            // just try adding a new point recursively
            if (!temp.hasAtLeastThreePoints()) {
                addPointRecursive(temp, newPointsLeft);
            }

            // check if the polygon with the new added point (temp) is convex and empty
            // if so, add to foundPolygons and call recursive function to try and add a new point
            else if (temp.isConvex() && temp.isEmptyWithoutRemove(newPointsLeft)){
                if(temp.calculateArea()>maxArea){
                    maxPolygon = temp;
                    maxArea = temp.area;
                }
                foundPolygons.add(temp);
                addPointRecursive(temp, newPointsLeft);
            }
        }

    }

}
