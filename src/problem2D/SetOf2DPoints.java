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
        Polygon2D svp = foundPolygons.get(0);
        System.out.println("done");
        GreedyAddPointHeuristic gh = new GreedyAddPointHeuristic(pointList, random, true);
        foundPolygons = gh.solve(2000);
        printInfoFoundPolygonsToConsole();
        System.out.println("done");

        Polygon2D initSolution = foundPolygons.get(0);
        System.out.println("init:" + initSolution);

        LocalSearch ls = new LocalSearch(pointList, random, initSolution);
        foundPolygons = ls.solve(5000);
        Polygon2D lsp = foundPolygons.get(foundPolygons.size()-1);
        SimulatedAnnealing sa = new SimulatedAnnealing(pointList, random, initSolution);
        foundPolygons = sa.solve(5000,2,0.05,0.95);
        Polygon2D sap = foundPolygons.get(foundPolygons.size()-1);
        System.out.println("Exact best:" + svp.area);
        System.out.println("Localsearch best:" + lsp.area);
        System.out.println("Simulated annealing best:" + sap.area);
        foundPolygons.clear();
        foundPolygons.add(svp);
        foundPolygons.add(lsp);
        foundPolygons.add(sap);

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


}
