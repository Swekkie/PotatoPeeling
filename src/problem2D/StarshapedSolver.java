package problem2D;

import java.util.*;

public class StarshapedSolver {

    private List<Point2D> inputPoints; // input set points
    private List<Polygon2D> foundPolygons;

    public Polygon2D maxPolygon = null;
    private double maxArea = 0;

    public StarshapedSolver(List<Point2D> inputPoints){
        this.inputPoints = inputPoints;
        this.foundPolygons = new ArrayList<>();
        Collections.shuffle(inputPoints);
    }


    public List<Polygon2D> solve() {
        long startTime = System.currentTimeMillis();

        // for every point in the input set:
        // find the largest area polygon with that point as leftmost point
        for (Point2D kernelPoint : inputPoints) {
            List<Point2D> orderedPoints = new ArrayList<>(inputPoints);
            orderedPoints.remove(kernelPoint);
            // search for the largest empty convex polygon with kernelPoint as mostleft point
            findMaxAreaEmptyPolygonWithMostLeftPoint(kernelPoint, orderedPoints);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("STAR-SHAPED");
        //System.out.print("Max polygon: " + maxPolygon);
        System.out.println("Area: " + maxPolygon.area);
        System.out.println("Time (ms): " + (endTime-startTime));
        return foundPolygons;
    }

    private void findMaxAreaEmptyPolygonWithMostLeftPoint(Point2D kernelPoint, List<Point2D> orderedPoints) {

        removePointsOnLeft(orderedPoints, kernelPoint);

        // We cant build a polygon when we do not have 2 points on the right of the leftmost point
        if (orderedPoints.size() < 2)
            return;

        // sort orderedPoint by angle (counter clockwise)
        setupOrderedList(orderedPoints, kernelPoint);

        // orderedPoints are now the vertices of a star-shaped polygon
        // construct the visibilitygraph
        VisibilityGraph2D vg = new VisibilityGraph2D(orderedPoints);

        // construct dag by setting edges
        // (convex chains, which edges are valid for an edge)
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


        // add kernel point and construct polygon
        maximumEmptyPolygon.add(0, kernelPoint);
        Polygon2D polygon = new Polygon2D(maximumEmptyPolygon);

        foundPolygons.add(polygon);


        // track maxPolygon
        if (polygon.calculateArea() > maxArea) {
            maxPolygon = polygon;
            maxArea = polygon.area;
        }
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
                if (a == 0) {
                    System.out.println("Input set nog in general position");
                    System.exit(1);
                }
                return a;
            }
        });
    }

    private void constructDAG(VisibilityGraph2D vg) {
        // construct the dag by setting the childs of the edges
        // check if edge y is a valid child of edge x
        List<Point2D> starShapedPolygon = vg.starShapedPolygon;
        List<List<Edge>> edgeLists = vg.edges;

        for (int i = 0; i < edgeLists.size() - 1; i++) {
            Point2D fromX = starShapedPolygon.get(i);
            List<Edge> outgoingEdges = edgeLists.get(i);
            for (Edge x : outgoingEdges) {
                int indexToX = x.to;
                Point2D toX = starShapedPolygon.get(indexToX); // toX is same point as fromY

                // toX is last point from starshaped-polygon
                // no outgoing edges from that point so no potential children
                if (indexToX == starShapedPolygon.size() - 1)
                    continue;

                boolean firstChildCheck = false;
                for (Edge y : edgeLists.get(indexToX)) {
                    int indexToY = y.to;
                    Point2D toY = starShapedPolygon.get(indexToY);
                    // check if toY is on left of edge x, if so, y is a valid child of y
                    // because edges are sorted, if 1 is a valid child, all remaining are too
                    if (firstChildCheck || toY.isOnLeftOfVector(fromX, toX)) {
                        x.addChild(y);
                        firstChildCheck = true;
                    }
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

    private List<Point2D> searchLongestPath(List<LongestPathItem> items, HashMap<Edge,
            LongestPathItem> map, List<Point2D> orderedPoints) {
        List<Edge> maxAreaChain = null;

        initItems(items);

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

    private void initItems(List<LongestPathItem> items) {
        for(LongestPathItem i: items){
            i.initItem();
        }
    }
}
