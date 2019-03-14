import java.util.ArrayList;
import java.util.List;

public class Polygon2D {

    private List<Point2D> point2DList;

    public Polygon2D(){
        point2DList = new ArrayList<>();
    }

    public Polygon2D(Polygon2D polygon){
        point2DList = new ArrayList<>(polygon.getPoint2DList());
    }

    public void addPoint2DToEnd(Point2D point){
        point2DList.add(point);
    }

    public int numberOfPoints(){
        return point2DList.size();
    }

    public Point2D getFirstPoint(){
        return point2DList.get(0);
    }
    public void removeLastPoint(){
        point2DList.remove(point2DList.size()-1);
    }
    public double calculateArea() {
        // TODO
        return 0;
    }

    public boolean isConvex () {
        // TODO
        return true;
    }


    // a polygon is a skull if it does not enclose any points
    public boolean isSkull (List<Point2D> pointsToCheck) {
        // DONT CHECK POINTS THAT ARE PART OF THE POLYGON
    


        return true;
    }


    // a polygon is valid when it has at least 3 points
    public boolean isValid(){
        if(point2DList.size()>2)
            return true;
        else
            return false;
    }

    public List<Point2D> getPoint2DList() {
        return point2DList;
    }

    public void setVector2DList(List<Vector2D> vector2DList) {
        this.point2DList = point2DList;
    }

    @Override
    public String toString() {
        if(isValid()) {
            String s = "";
            for (Point2D p : point2DList) {
                s += (p.toString() + "\n");
            }
            return s;
        }
        else return "Polygon is invalid";
    }
}
