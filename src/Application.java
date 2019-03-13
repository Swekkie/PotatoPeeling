import java.io.IOException;
import java.util.Random;

public class Application {

    public static void main(String [] args){

        Random random = new Random();

        int numberOfPoints = 20;

        // data input
        SetOf2DPoints setOfPoints = new SetOf2DPoints();

        // generate set of points between (0,0) and (20,20)
        int xRange = 20;
        int yRange = 20;

        for (int i = 0; i < numberOfPoints; i++){
            double randomX = random.nextDouble() * xRange;
            double randomY = random.nextDouble() * yRange;
            Point2D p = new Point2D(randomX,randomY);
            setOfPoints.addPoint(p);
        }



        // solve for the given set
        setOfPoints.solveAlgorithm();


        // write output
        try {
            setOfPoints.writePointsToFile("D:/Masterproef/data.txt",setOfPoints.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
