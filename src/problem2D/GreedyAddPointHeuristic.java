package problem2D;

import java.util.*;

public class GreedyAddPointHeuristic {

    private List<Point2D> inputPoints;
    private List<Polygon2D> foundPolygons;
    private Polygon2D maxPolygon = null;
    private double maxArea = 0;
    private Random random;
    private boolean triangles; // if true this heuristic only constructs triangles
    public long timeInit;


    public GreedyAddPointHeuristic(List<Point2D> inputPoints, boolean triangles) {
        this.inputPoints = inputPoints;
        this.foundPolygons = new ArrayList<>();
        this.random = new Random();
        this.triangles = triangles;
        Collections.shuffle(inputPoints);
    }

    public List<Polygon2D> solve(long timeInMillis) {
        // search multiple times for empty, convex polygons
        long startTime = System.currentTimeMillis();
        int i = 0;
        while(true){
            if(System.currentTimeMillis()-startTime>timeInMillis)
               break;
            i++;
            Polygon2D polygon = searchForPolygonAttemptMaxArea();
            if (polygon != null && polygon.hasAtLeastThreePoints()) {
                foundPolygons.add(polygon);
                if(polygon.calculateArea()>maxArea){
                    maxPolygon = polygon;
                    maxArea = polygon.area;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("GREEDY ADD HEURISTIC");
        //System.out.print("Max polygon: " + maxPolygon);
        System.out.println("Area: " + maxPolygon.area);
        System.out.println("Time (ms): " + (endTime - startTime)+ "    Iterations: " +i);
        foundPolygons.add(0,maxPolygon); // used for init solution
        return foundPolygons;
    }

    public List<Polygon2D> solveIterations(int iterations) {
        // search multiple times for empty, convex polygons
        long startTime = System.currentTimeMillis();
        for(int i = 0; i<iterations; i++ ){
            i++;
            Polygon2D polygon = searchForPolygonAttemptMaxArea();
            if (polygon != null && polygon.hasAtLeastThreePoints()) {
                foundPolygons.add(polygon);
                if(polygon.calculateArea()>maxArea){
                    maxPolygon = polygon;
                    maxArea = polygon.area;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("GREEDY ADD INIT HEURISTIC");
        //System.out.print("Max polygon: " + maxPolygon);
        System.out.println("Area: " + maxPolygon.area);
        System.out.println("Time (ms): " + (endTime - startTime)+ "    Iterations: " +iterations);
        timeInit = endTime-startTime;
        foundPolygons.add(0,maxPolygon); // used for init solution
        return foundPolygons;
    }

    public Polygon2D searchForPolygonAttemptMaxArea() {
        List<Point2D> usablePoints = new ArrayList<>(inputPoints);
        Polygon2D polygon = new Polygon2D();

        // pick random beginpair, construct polygon
        int index = random.nextInt(usablePoints.size());
        Point2D start = usablePoints.remove(index);
        index = random.nextInt(usablePoints.size());
        Point2D end = usablePoints.remove(index);
        polygon.addPoint2DToEnd(start);
        polygon.addPoint2DToEnd(end);

        // search for 3th point, choose point that adds most area
        polygon = addThirdPoint(polygon, usablePoints, start, end);
        if (polygon == null)
            return null; // no point added

        if(triangles) // if triangle mode, stop here
            return polygon;


        // keep adding points that maximizes added area until no more points can be added
        boolean noPointFound = false;
        do {
            Polygon2D newPolygon = addPointToMaximizeArea(polygon, usablePoints);
            if (newPolygon == null) {
                noPointFound = true;
            } else {
                polygon = new Polygon2D(newPolygon);
            }


        } while (!noPointFound);

        return polygon;

    }

    public Polygon2D addThirdPoint(Polygon2D polygon, List<Point2D> usablePoints, Point2D start, Point2D end) {
        // divide list in list for points on left side of start to end, and other side
        List<Point2D> leftSide = new ArrayList<>();
        List<Point2D> rightSide = new ArrayList<>();

        for (Point2D p : usablePoints) {
            if (p.calculateAreaGreedyAdd(start, end) > 0) {
                leftSide.add(p);
            } else if (p.isOnLeftOfVectorReturnCrossproduct(start, end) < 0) {
                rightSide.add(p);
            }
            // when area is zero, point is on the line, cant be a potential point to add
        }


        Point2D bestPointLeft = findBestPointForList(leftSide, start, end, true);
        Point2D bestPointRight = findBestPointForList(rightSide, start, end, false);
        Point2D bestPoint = null;

        if (bestPointLeft != null && bestPointRight != null) {
            if (bestPointLeft.areaForGreedyAdd > Math.abs(bestPointRight.areaForGreedyAdd)) {
                bestPoint = bestPointLeft;
                polygon.addPoint2DToEnd(bestPoint);
                usablePoints.remove(bestPoint);
            } else {
                bestPoint = bestPointRight;
                polygon.point2DList.add(1, bestPoint);
                usablePoints.remove(bestPoint);
            }
        } else if (bestPointLeft != null) {
            bestPoint = bestPointLeft;
            polygon.addPoint2DToEnd(bestPoint);
            usablePoints.remove(bestPoint);
        } else if (bestPointRight != null) {
            bestPoint = bestPointRight;
            polygon.point2DList.add(1, bestPoint);
            usablePoints.remove(bestPoint);
        } else
            return null; // has only 2 points

        return polygon;
    }

    // tries adding a point, returns the new polygon with the added point if it finds one
    // returns null if it does not find a point
    public Polygon2D addPointToMaximizeArea(Polygon2D polygon, List<Point2D> usablePoints) {

        // A point can be added to 1 edge only
        // index j is list for edge ij (from point index i to point index y)
        List<List<Point2D>> pointsPossibleForEdgeList = initEdgeList(polygon, usablePoints);
        double maxArea = 0;
        Point2D maxPoint = null;
        int indexJOfBestEdge = -1;

        // search for point that adds most area
        for (int j = 0, i = pointsPossibleForEdgeList.size() - 1; j < pointsPossibleForEdgeList.size(); i = j++) {
            List<Point2D> pointsPossibleForEdge = pointsPossibleForEdgeList.get(j);
            Point2D pointI = polygon.point2DList.get(i);
            Point2D pointJ = polygon.point2DList.get(j);

            Iterator<Point2D> it = pointsPossibleForEdge.iterator();
            while (it.hasNext()) {
                Point2D p = it.next();
                if (p.calculateAreaGreedyAdd(pointI,pointJ)==0) {
                    it.remove();
                }
            }


            // find point that adds most area for edge ij
            Point2D bestPointForEdgeIJ = findBestPointForList(pointsPossibleForEdge, pointI, pointJ, false);

            if (bestPointForEdgeIJ != null) {
                if (Math.abs(bestPointForEdgeIJ.areaForGreedyAdd) > maxArea) {
                    maxArea = bestPointForEdgeIJ.areaForGreedyAdd;
                    maxPoint = bestPointForEdgeIJ;
                    indexJOfBestEdge = j;
                }
            }
        }

        if (maxPoint == null) // no point to add
            return null;

        polygon.point2DList.add(indexJOfBestEdge, maxPoint);
        return polygon;
    }

    private List<List<Point2D>> initEdgeList(Polygon2D polygon, List<Point2D> usablePoints) {
        int polygonSizeList = polygon.point2DList.size();

        List<List<Point2D>> pointsPossibleForEdgeList = new ArrayList<>();
        for (int i = 0; i < polygonSizeList; i++) {
            pointsPossibleForEdgeList.add(new ArrayList<>());
        }

        for (Point2D p : usablePoints) {
            for (int j = 0, i = polygonSizeList - 1; j < polygonSizeList; i = j++) {
                // from i to j, h = point before i, k = point after j, in triangle h = k
                // traverse edges in order
                int h = (i - 1 + polygonSizeList) % polygonSizeList;
                int k = (j + 1) % polygonSizeList;

                if (p.isOnLeftOfVector(polygon.point2DList.get(h), polygon.point2DList.get(i))
                        && p.isOnLeftOfVector(polygon.point2DList.get(j), polygon.point2DList.get(k))
                        && p.isOnLeftOfVector(polygon.point2DList.get(j), polygon.point2DList.get(i))) {
                    pointsPossibleForEdgeList.get(j).add(p); // points can be added to the edge i j (edge ij on index j in list)
                    break;
                }
            }
        }

        return pointsPossibleForEdgeList;

    }

    private Point2D findBestPointForList(List<Point2D> points, Point2D start, Point2D end, boolean positive) {
        int sign;
        if (positive)
            sign = 1;
        else
            sign = -1;

        Collections.sort(points, new Comparator<Point2D>() {
            // sign is -1 if areas are negative
            @Override
            public int compare(Point2D p1, Point2D p2) {
                return sign * Double.compare(p1.areaForGreedyAdd, p2.areaForGreedyAdd);
            }
        });

        ListIterator<Point2D> iter = points.listIterator();
        Set<Point2D> toRemove = new HashSet<>();
        while (iter.hasNext()) {
            Point2D p = iter.next();
            if (toRemove.contains(p)) {
                iter.remove();
            } else {
                toRemove.addAll(removeForbiddenPoints(points, iter.nextIndex(), start, end, p, positive));
            }
        }


        Point2D bestPoint = null;
        if (!points.isEmpty()) {
            bestPoint = points.get(points.size() - 1);
        }

        return bestPoint;

    }

    private Set<Point2D> removeForbiddenPoints(List<Point2D> points, int index, Point2D start, Point2D end,
                                               Point2D checkedPoint, boolean positive) {
        Set<Point2D> toRemove = new HashSet<>();

        // switch start and end for correct vectors if right list (negative areas)
        if (!positive) {
            Point2D temp = end;
            end = start;
            start = temp;
        }

        ListIterator<Point2D> iter = points.listIterator(index);
        while (iter.hasNext()) {
            Point2D point = iter.next();

            if (!point.isOnLeftOfVector(checkedPoint, start) && !point.isOnLeftOfVector(end, checkedPoint)) {
                toRemove.add(point);
            }

        }

        return toRemove;

    }
}
