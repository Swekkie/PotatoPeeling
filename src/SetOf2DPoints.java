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
        // search multiple times for different convex skulls
        for (int i = 0; i<1; i++){
            List<Point2D> usablePoints = new ArrayList<>(pointList); //
            Polygon2D polygon = new Polygon2D();

            // pick random beginpair
            Collections.shuffle(usablePoints);
            Point2D start = usablePoints.remove(0);
            Point2D end = usablePoints.remove(0);
            polygon.addPoint2DToEnd(start);
            polygon.addPoint2DToEnd(end);

            System.out.println("Start: " + start + "\n" + "End: " + end);
            double angle = start.getAngleOfVectorTo(end);
            double temp = angle;
            // remove points that are eliminated because of the fact that we want convex polygons
            // we choose counter clockwise
            ListIterator<Point2D> iter = usablePoints.listIterator();
            while(iter.hasNext()){
                if(!pointIsUsableCounterClockwise(end, iter.next(), temp)){
                    iter.remove();
                }
            }

            while(!usablePoints.isEmpty()) {
                // pick next point
                // TODO check if point makes polygon enclosing other points
                Point2D lastAdded = usablePoints.remove(0);
                polygon.addPoint2DToEnd(lastAdded);

                Point2D secondLast = polygon.getPoint2DList().get(polygon.numberOfPoints()-2);
                angle = secondLast.getAngleOfVectorTo(lastAdded);
                // calculate new angle from last vector
                iter = usablePoints.listIterator();
                while(iter.hasNext()){
                    if(!pointIsUsableCounterClockwise(end, iter.next(), angle)){
                        iter.remove();
                    }
                }

            }
            System.out.println("FOUND POLYGON");
            System.out.println(polygon);
        }
    }

    private boolean pointIsUsableCounterClockwise(Point2D refPoint, Point2D pointToCheck, double angle) {
        double angleToCheck = refPoint.getAngleOfVectorTo(pointToCheck);
        if(angle<Math.PI){
            if(angle<=angleToCheck && angleToCheck<angle+Math.PI)
                return true;
        }
        else{
            if(angleToCheck>angle || angleToCheck<angle)
                return true;
        }
        return false;
    }

}
