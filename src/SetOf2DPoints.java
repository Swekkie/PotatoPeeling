import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SetOf2DPoints {

    private List<Point2D> pointList;
    private List<Polygon2D> foundPolygons;
    private boolean[][] pairChecked;
    public SetOf2DPoints() {
        this.pointList = new ArrayList<>();
        this.foundPolygons = new ArrayList<>();

    }

    public SetOf2DPoints(List<Point2D> points) {
        this.pointList = new ArrayList<>(points);
        this.foundPolygons = new ArrayList<>();

    }
    public void addPoint (Point2D p){
        pointList.add(p);
    }


    @Override
    public String toString() {
        String s = "";
        for (Point2D p : pointList){
            s += (p + "\n");
        }
        return s;
    }

    public void writePointsToFile(String pathName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathName)));
        for(Point2D p: pointList) {
            bw.write(p.toStringFile());
            bw.newLine();
        }
        bw.close();
    }

    public void writePolygonsToFile(String pathName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathName)));
        for(Polygon2D p: foundPolygons) {
            bw.write(p.idsOfPoints());
            bw.newLine();
        }
        bw.close();
    }

    public void writeFiftyLargestPolygonsToFile(String pathName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathName)));
        for(int i = 0; i<50; i++) {
            bw.write(foundPolygons.get(i).idsOfPoints());
            bw.newLine();
        }
        bw.close();
    }

    public void solveAlgorithm(){
        findConvexSkulls();
        foundPolygons.clear();
        recursiveSolution();
    }

    public void recursiveSolution(){
        // search for all convex, empty polygons

        // if a search from a certain pair (starting edge) is finished, the largest polygon containing that edge (order is important)
        // is already found, so if we add a point and make that edge in another search, we can stop the search for that polygon
        pairChecked = new boolean[pointList.size()][pointList.size()];
        // generate all possible starting pairs (0-1 is not the same as 1-0, from point 0 to point 1)
        for(int i = 0; i<pointList.size();i++){
            for(int j = 0; j<pointList.size();j++){
                if(i != j){
                    startSearchFromPair(pointList.get(i),pointList.get(j));
                    pairChecked[i][j] = true;
                }
            }
        }

        // check results
        System.out.println("Number of found polygons: " + foundPolygons.size());

        Collections.sort(foundPolygons, new Comparator<Polygon2D>(){
            public int compare(Polygon2D p1, Polygon2D p2){
                return -Double.compare(p1.calculateArea(),p2.calculateArea());
            }
        });

        System.out.println("Largest area: " + foundPolygons.get(0).calculateArea());


    }

    public void startSearchFromPair(Point2D p1, Point2D p2){
        // make a new polygon with the starting pair
        Polygon2D polygon = new Polygon2D();
        polygon.addPoint2DToEnd(p1);
        polygon.addPoint2DToEnd(p2);
        // recursively add points to the polygon
        addPointRecursive(polygon);
    }

    public void addPointRecursive(Polygon2D polygon){
        // if the polygon is valid (at least 3 points) it is a solution
        // add to the found polygons
        if(polygon.isValid())
            foundPolygons.add(polygon);

        // the next point cant be a point that already is a point of the polygon
        List<Point2D> pointsLeft = new ArrayList<>(pointList);
        pointsLeft.removeAll(polygon.getPoint2DList());

        for(Point2D newPoint : pointsLeft){
            // test for all remaining points: add to the polygon
            // if it is convex and empty, recursive search for new point
            // if not, test for the next point
            Polygon2D temp = new Polygon2D(polygon);
            Point2D beforeLast = temp.getPoint2DList().get(temp.numberOfPoints()-1);
            temp.addPoint2DToEnd(newPoint);

            int indexInListBeforeLast = pointList.indexOf(beforeLast);
            int indexInListLast = pointList.indexOf(newPoint);

            if(!pairChecked[indexInListBeforeLast][indexInListLast]) {
                if (temp.isConvex()) {
                    if (temp.isEmpty(pointsLeft)) {
                        addPointRecursive(temp);
                    }
                }
            }

        }

    }

    public void findConvexSkulls () {
        System.out.println(this);
        // search multiple times for different convex skulls
        for (int i = 0; i<500000; i++){
            Polygon2D polygon = searchForPolygonAttempt();
            if(polygon.isValid())
                foundPolygons.add(polygon);
        }
        System.out.println("Number of found polygons: " + foundPolygons.size());


        Collections.sort(foundPolygons, new Comparator<Polygon2D>(){
            public int compare(Polygon2D p1, Polygon2D p2){
                return -Double.compare(p1.calculateArea(),p2.calculateArea());
            }
        });

        System.out.println("Largest area: " + foundPolygons.get(0).calculateArea());

    }

    public Polygon2D searchForPolygonAttempt(){
        List<Point2D> usablePoints = new ArrayList<>(pointList); //
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
        while(iter.hasNext()){
            if(!iter.next().isOnLeftOfVector(start,end)){
                iter.remove();
            }
        }


        while(!usablePoints.isEmpty()) {
            // pick next point
            Point2D first = polygon.getFirstPoint();
            Point2D secondLast = polygon.getPoint2DList().get(polygon.numberOfPoints()-1); // second last after adding a new point
            Point2D lastAdded = usablePoints.remove(0);
            polygon.addPoint2DToEnd(lastAdded);
            List<Point2D> tempCopy = new ArrayList<>(usablePoints);

            iter = usablePoints.listIterator();
            while(iter.hasNext()){
                Point2D p = iter.next();
                if(!p.isOnLeftOfVector(secondLast,lastAdded)){
                    // points on "clockwise" side of the vector
                    iter.remove();
                }
                else{
                    if(!p.isOnLeftOfVector(first,lastAdded)){
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



}
