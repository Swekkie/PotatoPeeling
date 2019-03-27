import java.util.*;

public class SetOf3DPoints {

    private List<Point3D> pointList;
    private List<Polygon3D> foundPolygonMeshes;

    public SetOf3DPoints() {
        this.pointList = new ArrayList<>();
        this.foundPolygonMeshes = new ArrayList<>();

    }

    public SetOf3DPoints(List<Point3D> points) {
        this.pointList = new ArrayList<>(points);
        this.foundPolygonMeshes = new ArrayList<>();

    }

    public void addPoint(Point3D p) {
        pointList.add(p);
    }

    public List<Point3D> getPointList() {
        return pointList;
    }

    @Override
    public String toString() {
        String s = "";
        for (Point3D p : pointList) {
            s += (p.toStringWithId() + "\n");
        }
        return s;
    }

    public void solveAlgorithm() {

    }
}
