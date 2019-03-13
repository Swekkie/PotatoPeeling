import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    }

}
