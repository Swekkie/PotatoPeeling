import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class SetOf2DPoints {

    private List<Point2D> pointList;

    public SetOf2DPoints() {
        this.pointList = new ArrayList<>();
    }

    public SetOf2DPoints(List<Point2D> points) {
        this.pointList = new ArrayList<>(points);
    }
    public void addPoint (Point2D p){
        pointList.add(p);
    }

    public List<Point2D> getPointList() {
        return pointList;
    }

    @Override
    public String toString() {
        String s = "";
        for (Point2D p : pointList){
            s += (p.toString() + "\n");
        }
        return s;
    }

    public void writePointsToFile(String pathName, String data) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathName)));
        for(Point2D p: pointList) {
            bw.write(p.toString());
            bw.newLine();
        }
        bw.close();
    }

    public void solveAlgorithm(){
        System.out.println(this);
        // search multiple times for different convex skulls
        List<Polygon2D> foundPolygons = new ArrayList<>();
        for (int i = 0; i<25; i++){
            Polygon2D polygon = searchForPolygonAttempt();
            System.out.println("-----NEXTAAAAAA-----");
            System.out.println(polygon);
            if(polygon.isValid())
                foundPolygons.add(polygon);
        }

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

        System.out.println("Start: " + start + "\n" + "End: " + end);

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
            Point2D secondLast = polygon.getPoint2DList().get(polygon.numberOfPoints()-1);
            Point2D lastAdded = usablePoints.remove(0);
            polygon.addPoint2DToEnd(lastAdded);

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
                        return polygon;
                    }
                }
            }

        }
        return polygon;
    }



}
