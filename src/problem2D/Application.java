package problem2D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Application {

    public static void main(String[] args) {

        SetOf2DPoints setOfPoints = new SetOf2DPoints(generatePoints1());

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

    public static List<Point2D> generatePoints1(){
        Random random = new Random(6);
        int numberOfPoints = 3000;

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

    public static List<Point2D> generatePoints2(){


        List<Point2D> tempList = new ArrayList<>();


        Point2D p1 = new Point2D(1,9);
        Point2D p2 = new Point2D(1.5,6.5);
        Point2D p3 = new Point2D(1.5,2);
        Point2D p4 = new Point2D(0,5);
        Point2D p5 = new Point2D(5,2.5);
        Point2D p6 = new Point2D(7,2);
        Point2D p7 = new Point2D(10,4);
        Point2D p8 = new Point2D(5,5);
        Point2D p9 = new Point2D(9,7);
        Point2D p10 = new Point2D(7,9.5);

        tempList.add(p1);
        tempList.add(p2);
        tempList.add(p3);
        tempList.add(p4);
        tempList.add(p5);
        tempList.add(p6);
        tempList.add(p7);
        tempList.add(p8);
        tempList.add(p9);
        tempList.add(p10);
        return tempList;
    }

    public static List<Point2D> generatePoints3(){
        Random random = new Random(6);
        int numberOfPoints = 5000;

        int xRange = 40;
        int yRange = 40;

        List<Point2D> tempList = new ArrayList<>();

        for (int i = 0; i < numberOfPoints; i++) {
            double randomX = random.nextDouble();
            double randomY = random.nextDouble();
            randomX = randomX * xRange;
            randomY = randomY * yRange;

            Point2D p = new Point2D(randomX, randomY);
            tempList.add(p);
        }
        return tempList;
    }
}
