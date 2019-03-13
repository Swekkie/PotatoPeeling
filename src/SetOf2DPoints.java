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


    @Override
    public String toString() {
        String s = "";
        for (Point2D p : pointList){
            s += (p.toString() + "\n");
        }
        return s;
    }

    public void writeOuput(String pathName, String data) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathName)));
        for(Point2D p: pointList) {
            bw.write(p.toString());
            bw.newLine();
        }
        bw.close();
    }
}
