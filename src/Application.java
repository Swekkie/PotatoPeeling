import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Application {

    public static void main(String[] args) {


/*
        // generate set of points
        List<Point2D> temp = new ArrayList<>();
        temp.add(new Point2D(1,9));
        temp.add(new Point2D(1.5,6.5));
        temp.add(new Point2D(0,5));
        temp.add(new Point2D(1.5,2));
        temp.add(new Point2D(5,2.5));
        temp.add(new Point2D(7,2));
        temp.add(new Point2D(10,4));
        temp.add(new Point2D(9,7));
        temp.add(new Point2D(7,9.5));
        temp.add(new Point2D(4.5,8.5));
*/

        SetOf2DPoints setOfPoints = new SetOf2DPoints(generatePoints());

        // solve for the given set
        setOfPoints.solveAlgorithm();


        // write output
        try {
            setOfPoints.writePointsToFile("D:/Masterproef/PotatoPeeling/pointset.txt");
            setOfPoints.writeFiftyLargestPolygonsToFile("D:/Masterproef/PotatoPeeling/foundpolygons.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static List<Point2D> generatePoints(){
        Random random = new Random(6);
        int numberOfPoints = 200;

        int xRange = 40;
        int yRange = 40;

        List<Point2D> tempList = new ArrayList<>();

        for (int i = 0; i < numberOfPoints; i++) {
            double randomX = random.nextDouble();
            double randomY = random.nextDouble();

            if (i % 4 == 0) {
                randomX = randomX * xRange;
                randomY = randomY * yRange / 8;

            } else if (i % 4 == 1) {
                randomX = randomX * xRange / 8;
                randomY *= yRange;
            } else if (i % 4 == 2) {
                randomX = randomX * xRange;
                randomY = randomY * yRange / 8 + 7 * yRange / 8;
            } else {
                randomX = randomX * xRange / 8 + 7 * xRange / 8;
                randomY = randomY * yRange;
            }

            Point2D p = new Point2D(randomX, randomY);
            tempList.add(p);
        }
        return tempList;
    }

}
