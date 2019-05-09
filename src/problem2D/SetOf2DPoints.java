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
    private List<Point2D> checkedStartpoints;
    private Polygon2D bestSolution;
    private Random random;
    public int number = 0;

    public SetOf2DPoints() {
        this.pointList = new ArrayList<>();
        this.foundPolygons = new ArrayList<>();

    }

    public SetOf2DPoints(List<Point2D> points, Random random) {
        this.pointList = new ArrayList<>(points);
        this.foundPolygons = new ArrayList<>();
        this.random = random;
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

    public void printInfoFoundPolygonsToConsole(){
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

    public void solveAlgorithm() {
        //recursiveSolution();
        //findConvexSkullsGrowingClockwise(5000);
        findConvexSkullsUsingStarshapedPolygons();
        printInfoFoundPolygonsToConsole();
        foundPolygons.clear();
        findConvexSkullsMaxArea(100);
        printInfoFoundPolygonsToConsole();
        Polygon2D initialSolution = foundPolygons.get(0);
        foundPolygons.clear();
        simulatedAnnealing(initialSolution,5000);

    }


    // RECURSIVE (EXACT) ALGORITHM

    public void recursiveSolution() {
        // search for all convex, empty polygons

        checkedStartpoints = new ArrayList<>();
        // generate all possible starting pairs (0-1 is not the same as 1-0, from point 0 to point 1)
        for (int i = 0; i < pointList.size() - 1; i++) { // -1 because all possible polygons with last point have already been searched
            for (int j = i + 1; j < pointList.size(); j++) {
                // i is index of start point, j is index of second point (still no polygon formed with 2 points)
                startSearchFromPair(pointList.get(i), pointList.get(j));
            }

            // all polygons containing point i have been searched
            checkedStartpoints.add(pointList.get(i));

        }

        // check results
        System.out.println("Number of found polygons: " + foundPolygons.size());

        Polygon2D maxPolygon = foundPolygons.get(0);
        for (Polygon2D p : foundPolygons) {
            if (p.calculateArea() > maxPolygon.area) {
                maxPolygon = p;
            }
        }
/*
        Collections.sort(foundPolygons, new Comparator<problem2D.Polygon2D>(){
            public int compare(problem2D.Polygon2D p1, problem2D.Polygon2D p2){
                return -Double.compare(p1.calculateArea(),p2.calculateArea());
            }
        });*/
        System.out.println(foundPolygons.get(0));
        System.out.println("Largest area: " + foundPolygons.get(0).calculateArea() + "  andere: " + maxPolygon.area);
    }

    public void startSearchFromPair(Point2D p1, Point2D p2) {
        // make a new polygon with the starting pair
        Polygon2D polygon = new Polygon2D();
        polygon.addPoint2DToEnd(p1);
        polygon.addPoint2DToEnd(p2);

        // recursively add points to the polygon
        addPointRecursive(polygon);
    }

    public void addPointRecursive(Polygon2D polygon) {
        // if the polygon is valid (at least 3 points) it is a solution
        // add to the found polygons
        if (polygon.isValid())
            foundPolygons.add(polygon);

        // the next point cant be a point that already is a point of the polygon
        List<Point2D> pointsLeft = new ArrayList<>(pointList);
        pointsLeft.removeAll(polygon.getPoint2DList());

        for (Point2D newPoint : pointsLeft) {
            // test for all remaining points: add to the polygon
            // if it is convex and empty, recursive search for new point
            // if not, test for the next point
            Polygon2D temp = new Polygon2D(polygon);
            temp.addPoint2DToEnd(newPoint);

            number++;

            if (!checkedStartpoints.contains(newPoint)) {
                if (temp.isConvex()) {
                    if (temp.isEmpty(pointsLeft)) {
                        addPointRecursive(temp);
                    }
                }
            }

        }

    }


    // GREEDY HEURISTIC

    public void findConvexSkullsMaxArea(int attempts) {
        // search multiple times for different convex skulls
        for (int i = 0; i < attempts; i++) {
            Polygon2D polygon = searchForPolygonAttemptMaxArea();
            if (polygon.isValid())
                foundPolygons.add(polygon);
        }
    }

    public Polygon2D searchForPolygonAttemptMaxArea() {
        // constructive heuristic
        List<Point2D> usablePoints = new ArrayList<>(pointList); //
        Polygon2D polygon = new Polygon2D();

        // pick random beginpair
        Random random = new Random();
        int indexStart, indexEnd;
        indexStart = random.nextInt(usablePoints.size());
        indexEnd = random.nextInt(usablePoints.size()-1);

        Point2D start = usablePoints.remove(indexStart);
        Point2D end =  usablePoints.remove(indexEnd);
        polygon.addPoint2DToEnd(start);
        polygon.addPoint2DToEnd(end);

        // 3th point
        Point2D thirdPoint = null;
        double maxArea = 0;
        Polygon2D maxPolygon = null;
        for (Point2D point : usablePoints) {
            Polygon2D temp = new Polygon2D(polygon);
            if (point.isOnLeftOfVector(start, end))
                temp.addPoint2DToEnd(point);
            else
                temp.getPoint2DList().add(1, point);

            if (temp.calculateArea() > maxArea && temp.isEmpty(pointList)) {
                maxArea = temp.calculateArea();
                maxPolygon = temp;
            }
        }
        polygon = new Polygon2D(maxPolygon);
/*
        // adding point that maximizes added area
        boolean noPointFound = false;
        do {

            Polygon2D newPolygon = addPointToMaximizeArea(polygon, usablePoints);
            if (newPolygon == null)
                noPointFound = true;
            else {
                polygon = new Polygon2D(newPolygon);
            }


        } while (!noPointFound&&polygon.point2DList.size()<4);
*/
        return polygon;

    }

    public Polygon2D addPointToMaximizeArea(Polygon2D polygon, List<Point2D> usablePoints) {
        usablePoints.removeAll(polygon.getPoint2DList());

        List<List<Point2D>> pointsPossibleForEdgeList = new ArrayList<>();
        for(int i = 0; i<polygon.point2DList.size();i++){
            pointsPossibleForEdgeList.add(new ArrayList<>());
        }

        for (Point2D p : usablePoints) {
            for (int j = 0, i = polygon.point2DList.size() - 1; j < polygon.point2DList.size(); i = j++) {
                // from i to j, h = point before i, k = point after j, in triangle h = k
                // traverse edges in order
                int h = i - 1;
                int k = j + 1;
                if (h < 0)
                    h = polygon.numberOfPoints() - 1;
                if (k == polygon.numberOfPoints())
                    k = 0;

                if (p.isOnLeftOfVector(polygon.point2DList.get(h), polygon.point2DList.get(i))
                        && p.isOnLeftOfVector(polygon.point2DList.get(j), polygon.point2DList.get(k))
                        && !p.isOnLeftOfVector(polygon.point2DList.get(i), polygon.point2DList.get(j))) {
                    pointsPossibleForEdgeList.get(j).add(p); // points can be added to the edge i j (edge ij on index j in list)
                    break;
                }
            }
        }

        Polygon2D maxPolygon = null;
        double maxArea = 0;
        boolean pointAdded = false;
        // search for point that adds most area
        for(int listIndex = 0; listIndex<pointsPossibleForEdgeList.size();listIndex++){
            List<Point2D> pointsPossibleForEdge = pointsPossibleForEdgeList.get(listIndex);
            for(Point2D p: pointsPossibleForEdge){

                Polygon2D temp = new Polygon2D(polygon);
                temp.point2DList.add(listIndex, p);
                if (temp.isEmpty(pointsPossibleForEdge)) {
                    pointAdded = true;
                    double tempArea = temp.calculateArea();
                    if (maxArea < tempArea) {
                        maxPolygon = temp;
                        maxArea = tempArea;
                    }
                }
            }
        }

        // maxPolygon is previous polygon with point added that adds largest area
        if (!pointAdded)
            return null;
        return maxPolygon;
    }


    // RANDOM HEURISTIC (BAD)

    public void findConvexSkullsGrowingClockwise(int attempts) {
        // search multiple times for different convex skulls
        for (int i = 0; i < attempts; i++) {
            Polygon2D polygon = searchForPolygonAttempt();
            if (polygon.isValid())
                foundPolygons.add(polygon);
        }
        System.out.println("Number of found polygons: " + foundPolygons.size());


        Collections.sort(foundPolygons, new Comparator<Polygon2D>() {
            public int compare(Polygon2D p1, Polygon2D p2) {
                return -Double.compare(p1.calculateArea(), p2.calculateArea());
            }
        });
        System.out.println(foundPolygons.get(0));
        System.out.println("Largest area: " + foundPolygons.get(0).calculateArea());

    }

    public Polygon2D searchForPolygonAttempt() {
        List<Point2D> usablePoints = new ArrayList<>(pointList);
        Polygon2D polygon = new Polygon2D();

        // pick random beginpair
        Collections.shuffle(usablePoints, random);
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
                        // remove new added point and STOP CURRENT POLYGON SEARCH
                        polygon.removeLastPoint();
                        usablePoints = tempCopy;
                        break;
                    }
                }
            }

        }
        return polygon;
    }


    // EXACT ALGORITHM USING STARSHAPED POLYGONS

    public void findConvexSkullsUsingStarshapedPolygons() {
        for (Point2D kernelPoint : pointList) {
            List<Point2D> orderedPoints = new ArrayList<>(pointList);
            orderedPoints.remove(kernelPoint);
            // search for the largest empty convex polygon with kernerlPoint as mostleft point
            findMaxAreaEmptyPolygonWithMostLeftPoint(kernelPoint, orderedPoints);
        }
    }

    private void findMaxAreaEmptyPolygonWithMostLeftPoint(Point2D kernelPoint, List<Point2D> orderedPoints) {
        removePointsOnLeft(orderedPoints, kernelPoint);
        if(orderedPoints.size()<2)
            return;

        setupOrderedList(orderedPoints, kernelPoint);

        // orderedPoints are now the vertices of a star-shaped polygon
        // construct the visibilitygraph
        VisibilityGraph2D vg = new VisibilityGraph2D(orderedPoints);

        // construct dag (convex chains, which edges are valid for an edge)
        constructDAG(vg);

        // topological sort of edges
        List<Edge> sortedEdges = topologicalSort(vg);

        // setup for longest path search
        List<LongestPathItem> longestPathList = new ArrayList<>();
        HashMap<Edge, LongestPathItem> map = new HashMap<>();
        for (Edge e : sortedEdges) {
            LongestPathItem item = new LongestPathItem(e);
            map.put(e, item);
            longestPathList.add(item);
        }
        calculateAreas(longestPathList, kernelPoint, orderedPoints);

        // search for path with heighest area
        List<Point2D> maximumEmptyPolygon = searchLongestPath(longestPathList, map, orderedPoints);
        maximumEmptyPolygon.add(0, kernelPoint);
        Polygon2D polygon = new Polygon2D(maximumEmptyPolygon);

        foundPolygons.add(polygon);
    }

    private void removePointsOnLeft(List<Point2D> orderedPoints, Point2D kernelPoint) {
        double kernelPointX = kernelPoint.getX();
        Iterator<Point2D> it = orderedPoints.iterator();
        while (it.hasNext()) {
            Point2D p = it.next();
            if (p.getX() < kernelPointX) {
                it.remove();
            }
        }
    }

    private void setupOrderedList(List<Point2D> orderedPoints, Point2D kernelPoint) {
        // sorts the points by angle (anti-clockwise)
        Collections.sort(orderedPoints, new Comparator<Point2D>() {
            @Override
            public int compare(Point2D p1, Point2D p2) {
                int a = Double.compare(p1.calculateAngleOfVectorFrom(kernelPoint), p2.calculateAngleOfVectorFrom(kernelPoint));
                if(a==0)
                    System.out.println("oeps");
                return a;
            }
        });

    }

    private void constructDAG(VisibilityGraph2D vg) {
        // construct the dag by setting the childs of the edges
        // chech if edge y is a valid child of edge x
        List<Point2D> starShapedPolygon = vg.starShapedPolygon;
        List<List<Edge>> edgeLists = vg.edges;

        for (int i = 0; i < edgeLists.size() - 1; i++) {
            Point2D fromX = starShapedPolygon.get(i);
            List<Edge> outgoingEdges = edgeLists.get(i);
            for (Edge x : outgoingEdges) {
                int indexToX = x.to;
                Point2D toX = starShapedPolygon.get(indexToX);
                // toX is same point as fromY
                if (indexToX == starShapedPolygon.size() - 1)
                    continue;
                for (Edge y : edgeLists.get(indexToX)) {
                    int indexToY = y.to;
                    Point2D toY = starShapedPolygon.get(indexToY);
                    // check if toY is on left of edge x, if so, y is a valid child of y
                    // TODO edges are sorted by angle, if one edge becomes a child, others will be to
                    // so check is not necessary
                    if (toY.isOnLeftOfVector(fromX, toX))
                        x.addChild(y);
                }
            }
        }
    }

    private List<Edge> topologicalSort(VisibilityGraph2D vg) {
        List<Edge> edges = new ArrayList<>();
        LinkedList<Edge> sortedList = new LinkedList<>();
        for (List<Edge> list : vg.edges)
            edges.addAll(list);

        Set<Edge> visited = new HashSet<>();

        for (Edge e : edges) {
            if (!visited.contains(e))
                topologicalSortHelp(e, visited, sortedList);
        }

        return sortedList;
    }

    private void topologicalSortHelp(Edge e, Set<Edge> visited, LinkedList<Edge> sortedList) {
        visited.add(e);
        for (Edge child : e.validChildEdges) {
            if (!visited.contains(child)) {
                topologicalSortHelp(child, visited, sortedList);
            }
        }
        sortedList.push(e);
    }

    private void calculateAreas(List<LongestPathItem> items, Point2D kernelPoint, List<Point2D> orderedPoints) {
        double Ax = kernelPoint.getX();
        double Ay = kernelPoint.getY();
        for (LongestPathItem item : items) {
            Edge e = item.edge;
            double Bx = orderedPoints.get(e.from).getX();
            double By = orderedPoints.get(e.from).getY();
            double Cx = orderedPoints.get(e.to).getX();
            double Cy = orderedPoints.get(e.to).getY();
            double area = Math.abs((Ax * (By - Cy) + Bx * (Cy - Ay) + Cx * (Ay - By)) / 2);
            item.area = area;
        }
    }

    private List<Point2D> searchLongestPath(List<LongestPathItem> items, HashMap<Edge, LongestPathItem> map, List<Point2D> orderedPoints) {
        List<Edge> maxAreaChain = null;
        double maxArea = 0;
        for (int i = 0; i < items.size(); i++) {
            LongestPathItem item = items.get(i);

            if (!item.visited) {
                item.firstVisit();
                if (item.areaChain > maxArea) {
                    maxArea = item.areaChain;
                    maxAreaChain = item.chain;
                }
            }
            for (Edge ch : item.edge.validChildEdges) {
                LongestPathItem child = map.get(ch);
                if (item.areaChain + child.area > child.areaChain) {
                    child.visited = true;
                    child.updateChain(item);
                    if (child.areaChain > maxArea) {
                        maxArea = child.areaChain;
                        maxAreaChain = child.chain;
                    }
                }
            }

        }

        List<Point2D> polygonPoints = new ArrayList<>();
        for (Edge e : maxAreaChain) {
            polygonPoints.add(orderedPoints.get(e.from));
        }

        polygonPoints.add(orderedPoints.get(maxAreaChain.get(maxAreaChain.size() - 1).to));

        return polygonPoints;

    }


    // LOCALSEARCH
    public void localSearch(Polygon2D startingPolygon,int iterations){
        PolygonFactory factory = new PolygonFactory(pointList,random);
        bestSolution = startingPolygon;
        bestSolution.calculateArea();
        foundPolygons.add(bestSolution);

        for(int i = 0; i<iterations;i++){
            Polygon2D neighbour = factory.generateNeighbour(bestSolution);
            if(neighbour.isFeasible(pointList)){
                neighbour.calculateArea();
                //System.out.println(neighbour.area);
                if(neighbour.calculateArea()>bestSolution.area){
                    bestSolution = neighbour;
                    foundPolygons.add(neighbour);
                    System.out.println(bestSolution.area);
                }
            }
        }
    }

    // SIMULATED ANNEALING

    public void simulatedAnnealing(Polygon2D startingPolygon,int iterations){
        PolygonFactory factory = new PolygonFactory(pointList,random);
        bestSolution = startingPolygon;
        bestSolution.calculateArea();
        foundPolygons.add(bestSolution);
        double startingTemperature = 0.5;
        for(int i = 0; i<iterations;i++){
            double temperature = startingTemperature-startingTemperature/iterations*i;
            Polygon2D neighbour = factory.generateNeighbour(bestSolution);
            if(neighbour.isFeasible(pointList)){
                neighbour.calculateArea();

                if(neighbour.area>=bestSolution.area){
                    bestSolution = neighbour;
                    foundPolygons.add(neighbour);
                    System.out.println(bestSolution.area);
                }
                else{
                    double areaDifference = bestSolution.area - neighbour.area;
                    double probability = Math.exp(-areaDifference/temperature);
                    System.out.println("Prob:" + probability);
                    if(random.nextDouble()<probability){
                        bestSolution = neighbour;
                        foundPolygons.add(neighbour);
                        System.out.println(bestSolution.area);
                    }
                }

            }
        }
    }
}
