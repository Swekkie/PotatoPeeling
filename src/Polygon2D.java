import java.util.ArrayList;
import java.util.List;

public class Polygon2D {

    private List<Vector2D> vector2DList;

    public Polygon2D(){
        vector2DList = new ArrayList<>();
    }

    public Polygon2D(Polygon2D polygon){
        vector2DList = new ArrayList<>(polygon.getVector2DList());
    }

    public void addPoint2DToEnd(Point2D point){
        Vector2D last = vector2DList.get(vector2DList.size()-1);
        Point2D beginPointPolygon = last.getEnd();
        last.setEnd(point);
        vector2DList.add(new Vector2D(point,beginPointPolygon));

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

    public List<Vector2D> getVector2DList() {
        return vector2DList;
    }

    public void setVector2DList(List<Vector2D> vector2DList) {
        this.vector2DList = vector2DList;
    }
}
