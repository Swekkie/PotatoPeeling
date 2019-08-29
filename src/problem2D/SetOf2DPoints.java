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

    public SetOf2DPoints() {
        this.pointList = new ArrayList<>();
        this.foundPolygons = new ArrayList<>();
        Collections.shuffle(pointList); // randomization in order for repeated testing

    }

    public SetOf2DPoints(List<Point2D> points) {
        this.pointList = new ArrayList<>(points);
        this.foundPolygons = new ArrayList<>();

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
        if(foundPolygons.size()>500){
            writeFiftyLargestPolygonsToFile(pathName);
        }else {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathName)));
            for (Polygon2D p : foundPolygons) {
                bw.write(p.idsOfPoints());
                bw.newLine();
            }
            bw.close();
        }
    }

    public void writeFiftyLargestPolygonsToFile(String pathName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathName)));
        for (int i = 0; i < 50; i++) {
            bw.write(foundPolygons.get(i).idsOfPoints());
            bw.newLine();
        }
        bw.close();
    }

    // sorts found polygon list by decreasing area and prints the best found
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

    // uncomment for running algorithm
    // solve methods of algorithm return list of polygons
    // can be added to foundpolygons (which will be written to file)
    // sort and print foundpolygons with printInfoFoundPolygonsToConsole()
    // if list is big use writeFiftyLargestPolygonsToFile() in application class
    public void solve() {
//        BruteForceSolver bfs = new BruteForceSolver(pointList);
//        bfs.solve(); // prints the best polygon, its area, and time needed
//        System.out.println("-------------");

//        StarshapedSolver ss = new StarshapedSolver(pointList);
//        foundPolygons = ss.solve();
//        System.out.println("-------------");

//        RandomAddPointHeuristic ra = new RandomAddPointHeuristic(pointList);
//        foundPolygons = ra.solve(750);
//        System.out.println("-------------");

//        GreedyAddPointHeuristic ga = new GreedyAddPointHeuristic(pointList,false);
//        foundPolygons = ga.solve(750);
//        System.out.println("-------------");

//        long time = 4000;
//        GreedyAddPointHeuristic gaInit = new GreedyAddPointHeuristic(pointList,false);
//        Polygon2D initSolution = gaInit.solve(time*1/4).get(0);
//        System.out.println(initSolution.area);
//        System.out.println(initSolution);
//
//        SimulatedAnnealing sa = new SimulatedAnnealing(pointList,initSolution,3);
//        foundPolygons.addAll(sa.solve(time-gaInit.timeInit,2,0.005,0.95));
//        System.out.println(sa.maxPolygon);
//        System.out.println("-------------");

//        foundPolygons.addAll(findMultiplePolygonsStarshaped(8,0.6));

    }

    private List<Polygon2D> findMultiplePolygonsStarshaped(int number, double overlapValue) {
        long startAlgorithm = System.currentTimeMillis();
        StarshapedSolver ss = new StarshapedSolver(pointList);
        List<Polygon2D> allSolutions = ss.solve();
        Collections.sort(allSolutions, new Comparator<Polygon2D>() {
            public int compare(Polygon2D p1, Polygon2D p2) {
                return -Double.compare(p1.calculateArea(), p2.calculateArea());
            }
        });

        // update extrema for estimating overlap
        for (Polygon2D p : allSolutions)
            p.updateBoundaries();
        System.out.println(allSolutions.size());
        ListIterator<Polygon2D> iterator = allSolutions.listIterator();
        List<Polygon2D> chosenSolutions = new ArrayList<>();
        Polygon2D first = iterator.next();
        chosenSolutions.add(first);
        System.out.println(first.area);
        while (chosenSolutions.size() < number && iterator.hasNext()) {
            Polygon2D polygon = iterator.next();
            if (isLongNarrow(polygon)) {
                continue;
            }
            // estimate overlap with every polygon in chosenInit
            // if big overlap dont add to the chosenInit
            List<Polygon2D> toAdd = new ArrayList<>();
            boolean overlapDetected = false;
            for (Polygon2D init : chosenSolutions) {
                double overlapFactor = checkOverlap(polygon, init);
                if (overlapFactor > overlapValue) {
                    overlapDetected = true;
                    break;
                }
            }
            if (!overlapDetected) {
                chosenSolutions.add(polygon);
                System.out.println(polygon.area);
            }

        }
        System.out.println(chosenSolutions.size());
        long solutionsFinished = System.currentTimeMillis() - startAlgorithm;
        System.out.println("Time multiple polygons exact before annealing: " + solutionsFinished);

        List<Polygon2D> afterAnnealing = new ArrayList<>();
        long timeForOnePolygon = Math.max(solutionsFinished/10/chosenSolutions.size(), 10);
        for(Polygon2D p: chosenSolutions){
            SimulatedAnnealing sa = new SimulatedAnnealing(pointList,p,1);
            sa.solve(timeForOnePolygon,0.1,0.005,0.95);
            System.out.println(sa.maxPolygon);
            afterAnnealing.add(sa.maxPolygon);
        }
        System.out.println("Time multiple polygons exact after annealing: " + (System.currentTimeMillis()-startAlgorithm));
        chosenSolutions.addAll(afterAnnealing);
        return chosenSolutions;

    }

    private boolean isLongNarrow(Polygon2D polygon) {
        double x = polygon.xMax - polygon.xMin;
        double y = polygon.yMax - polygon.yMin;

        double factor = x / y;
        if (factor < 0.2 || factor > 5) {
            return true;
        }

        if (polygon.area / (x * y) < 0.2)
            return true;

        return false;

    }

    private double checkOverlap(Polygon2D p, Polygon2D init) {
        double xOverlap, yOverlap;
        if (init.xMin > p.xMax || p.xMin > init.xMax)
            xOverlap = 0;
        else {
            double xOverlapMin = Math.max(init.xMin, p.xMin);
            double xOverlapMax = Math.min(init.xMax, p.xMax);
            xOverlap = xOverlapMax - xOverlapMin;
        }
        if (init.yMin > p.yMax || p.yMin > init.yMax)
            yOverlap = 0;
        else {
            double yOverlapMin = Math.max(init.yMin, p.yMin);
            double yOverlapMax = Math.min(init.yMax, p.yMax);
            yOverlap = yOverlapMax - yOverlapMin;
        }

        double areaOverlap = xOverlap * yOverlap;

        double smallestArea = Math.min((init.xMax-init.xMin)*(init.yMax-init.yMin),(p.xMax-p.xMin)*(p.yMax-p.yMin));

        //System.out.println(areaOverlap/smallestArea);
        return areaOverlap / smallestArea; // 0 if no overlap, 1 if one contains another
    }


}
