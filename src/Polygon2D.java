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
    public double calculateArea() {
        // TODO
        return 0;
    }

    public boolean isConvex () {
        // TODO
        return true;
    }

    // a polygon is a skull if it doesnt enclose any points
    public boolean isSkull (List<Point2D> pointsToCheck) {
        // TODO
        return true;
    }

    public List<Point2D> getPoint2DList() {
        return point2DList;
    }

    public void setVector2DList(List<Vector2D> vector2DList) {
        this.point2DList = point2DList;
    }

    @Override
    public String toString() {
        String s = "";
        for (Point2D p : point2DList){
            s += (p.toString() + "\n");
        }
        return s;
    }
}
