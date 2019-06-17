package problem2D;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SetOf2DPoints {

    private List<Point2D> pointList;
    private List<Polygon2D> foundPolygons;
    private Random random;

    public SetOf2DPoints() {
        this.pointList = new ArrayList<>();
        this.foundPolygons = new ArrayList<>();

    }

    public SetOf2DPoints(List<Point2D> points, Random random) {
        this.pointList = new ArrayList<>(points);
        this.foundPolygons = new ArrayList<>();
        this.random = random;

        for (int i = 1; i <= pointList.size(); i++) {
            pointList.get(i - 1).id = i;
        }

    }

    public void addPoint(Point2D p) {
        pointList.add(p);
    }

    @Override
    public String toString() {
        String s = "";
        for (Point2D p : pointList) {
            s += (p + "\n");
        }
        return s;
    }

    public void deleteFoundPolygons() {
        foundPolygons.clear();
    }

    public void writePointsToFile(String pathName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathName)));
        for (Point2D p : pointList) {
            bw.write(p.toStringFile());
            bw.newLine();
        }
        bw.close();
    }

    public void writePolygonsToFile(String pathName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathName)));
        for (Polygon2D p : foundPolygons) {
            bw.write(p.idsOfPoints());
            bw.newLine();
        }
        bw.close();
    }

    public void writeFiftyLargestPolygonsToFile(String pathName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathName)));
        for (int i = 0; i < 50; i++) {
            bw.write(foundPolygons.get(i).idsOfPoints());
            bw.newLine();
        }
        bw.close();
    }

    public void printInfoFoundPolygonsToConsole() {
        System.out.println("start sorting");
        Collections.sort(foundPolygons, new Comparator<Polygon2D>() {
            public int compare(Polygon2D p1, Polygon2D p2) {
                return -Double.compare(p1.calculateArea(), p2.calculateArea());
            }
        });
        System.out.println("Number of found polygons: " + foundPolygons.size());
        System.out.println(foundPolygons.get(0));
        System.out.println("Largest area: " + foundPolygons.get(0).calculateArea());
    }

    public void solve() {
//        RecursiveSolver rs = new RecursiveSolver(pointList);
//        foundPolygons = rs.solve();
//        printInfoFoundPolygonsToConsole();
        StarshapedSolver sv = new StarshapedSolver(pointList);
        foundPolygons = sv.solve();
        printInfoFoundPolygonsToConsole();
        System.out.println("done");
        GreedyAddPointHeuristic gh = new GreedyAddPointHeuristic(pointList, random, true);
        foundPolygons = gh.solve(12000);
        printInfoFoundPolygonsToConsole();
        System.out.println("done");
    }


    // INITIAL SOLUTION FOR HEURISTICS
/*
    public Polygon2D getInitialSolution1(){
        Polygon2D bestInitialSolution = null;
        double longestEdge = 0;
        for (Point2D kernelPoint : pointList) {
            List<Point2D> orderedPoints = new ArrayList<>(pointList);
            orderedPoints.remove(kernelPoint);
            // search for the largest empty convex polygon with kernerlPoint as mostleft point
            Polygon2D triangle = findTriangleWithLongestEdge(kernelPoint, orderedPoints);
            if(triangle == null) continue;
            if(triangle.longestEdge>longestEdge){
                bestInitialSolution = triangle;
                longestEdge = triangle.longestEdge;
            }
        }

        return bestInitialSolution;
    }

    public Polygon2D getInitialSolution2(){
        Polygon2D bestInitialSolution = null;
        double largestArea = 0;
        for (Point2D kernelPoint : pointList) {
            List<Point2D> orderedPoints = new ArrayList<>(pointList);
            orderedPoints.remove(kernelPoint);
            // search for the largest empty convex polygon with kernerlPoint as mostleft point
            Polygon2D triangle = findTriangleWithLargestArea(kernelPoint, orderedPoints);
            if(triangle == null) continue;
            if(triangle.area>largestArea){
                bestInitialSolution = triangle;
                largestArea = triangle.area;
            }
        }

        return bestInitialSolution;
    }

    private Polygon2D findTriangleWithLongestEdge(Point2D kernelPoint, List<Point2D> orderedPoints) {
        removePointsOnLeft(orderedPoints,kernelPoint);
        if(orderedPoints.size()<2)
            return null;

        setupOrderedList(orderedPoints, kernelPoint);
        int maxIndex = 0;
        double maxEdge = 0;
        for(int i = 0; i<orderedPoints.size()-1; i++){
            Point2D p1 = orderedPoints.get(i);
            Point2D p2 = orderedPoints.get(i+1);
            double longestEdge = Math.max(p1.getSquaredDistanceTo(kernelPoint)
                    ,p2.getSquaredDistanceTo(kernelPoint));

            if(longestEdge>maxEdge){
                maxIndex = i;
                maxEdge = longestEdge;
            }
        }
        Polygon2D polygon = new Polygon2D();
        polygon.addPoint2DToEnd(kernelPoint);
        polygon.addPoint2DToEnd(orderedPoints.get(maxIndex));
        polygon.addPoint2DToEnd(orderedPoints.get(maxIndex+1));
        polygon.longestEdge = maxEdge;

        return polygon;

    }

    private Polygon2D findTriangleWithLargestArea(Point2D kernelPoint, List<Point2D> orderedPoints) {
        removePointsOnLeft(orderedPoints,kernelPoint);
        if(orderedPoints.size()<2)
            return null;

        setupOrderedList(orderedPoints, kernelPoint);
        double maxArea = 0;
        Polygon2D maxPolygon = null;
        for(int i = 0; i<orderedPoints.size()-1; i++){
            Point2D p1 = orderedPoints.get(i);
            Point2D p2 = orderedPoints.get(i+1);
            Polygon2D polygon = new Polygon2D();

            polygon.addPoint2DToEnd(kernelPoint);
            polygon.addPoint2DToEnd(orderedPoints.get(i));
            polygon.addPoint2DToEnd(orderedPoints.get(i+1));

            double area = polygon.calculateArea();

            if(area>maxArea){
                maxArea = area;
                maxPolygon = polygon;
            }
        }

        return maxPolygon;

    }
*/
/*
    public void localSearch(Polygon2D startingPolygon, int iterations) {
        PolygonFactory factory = new PolygonFactory(pointList, random, false);
        Polygon2D bestSolution = startingPolygon;
        bestSolution.calculateArea();
        foundPolygons.add(bestSolution);
        for (int i = 0; i < iterations; i++) {
            Polygon2D neighbour = factory.generateNeighbour(bestSolution);
            if (neighbour.isFeasible(pointList)) {
                neighbour.calculateArea();
                if (neighbour.calculateArea() > bestSolution.area) {
                    bestSolution = neighbour;
                    foundPolygons.add(neighbour);
                    System.out.println(bestSolution.area);
                }
            }
        }
    }

    // SIMULATED ANNEALING

    public void simulatedAnnealing(Polygon2D startingPolygon, int iterations) {
        PolygonFactory factory = new PolygonFactory(pointList, random, true);
        Polygon2D bestSolution = startingPolygon;
        bestSolution.calculateArea();
        foundPolygons.add(bestSolution);
        double startingTemperature = 0.5;
        for (int i = 0; i < iterations; i++) {
            double temperature = startingTemperature - startingTemperature / iterations * i;
            Polygon2D neighbour = factory.generateNeighbour(bestSolution);
            if (neighbour.isFeasible(pointList)) {
                neighbour.calculateArea();

                if (neighbour.area >= bestSolution.area) {
                    bestSolution = neighbour;
                    foundPolygons.add(neighbour);
                    System.out.println(bestSolution.area);
                } else {
                    double areaDifference = bestSolution.area - neighbour.area;
                    double probability = Math.exp(-areaDifference / temperature);
                    System.out.println("Prob:" + probability);
                    if (random.nextDouble() < probability) {
                        bestSolution = neighbour;
                        foundPolygons.add(neighbour);
                        System.out.println(bestSolution.area);
                    }
                }

            }
        }
    }
*/

}
