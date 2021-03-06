package problem2D;

import java.io.*;
import java.util.*;

public class Application {

    public static void main(String[] args) {
        Random random = new Random(0);
        SetOf2DPoints setOfPoints = new SetOf2DPoints(generatePoints1(500, random));
//        SetOf2DPoints setOfPoints = new SetOf2DPoints(generatePoints2());
//        SetOf2DPoints setOfPoints = new SetOf2DPoints(generatePoints3(10,random));
//        SetOf2DPoints setOfPoints = new SetOf2DPoints(generatePoints4(5000,3,3,random));
//        SetOf2DPoints setOfPoints = new SetOf2DPoints(generatePoints5("5_912.txt",random));
//        SetOf2DPoints setOfPoints = new SetOf2DPoints(generatePoints5("2_629.txt",random));
//        SetOf2DPoints setOfPoints = new SetOf2DPoints(generatePoints5("8_1428.txt",random));
//        SetOf2DPoints setOfPoints = new SetOf2DPoints(generatePoints5("2_36.txt",random));
//        SetOf2DPoints setOfPoints = new SetOf2DPoints(generatePoints6(1000,random));

        try {
            setOfPoints.writePointsToFile("D:/Masterproef/PotatoPeeling/pointset.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // solve for chosen input set
        // algorithm choesen in solve method
        setOfPoints.solve();

        // write output
        try {
            setOfPoints.writePolygonsToFile("D:/Masterproef/PotatoPeeling/foundpolygons.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Point2D> generatePoints1(int numberOfPoints, Random random) {

        double xRange = 10;
        double yRange = 10;

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

    public static List<Point2D> generatePoints2() {


        List<Point2D> tempList = new ArrayList<>();


        Point2D p1 = new Point2D(1, 9);
        Point2D p2 = new Point2D(1.5, 6.5);
        Point2D p3 = new Point2D(1.5, 2);
        Point2D p4 = new Point2D(0, 5);
        Point2D p5 = new Point2D(5, 2.5);
        Point2D p6 = new Point2D(7, 2);
        Point2D p7 = new Point2D(10, 4);
        Point2D p8 = new Point2D(5, 5);
        Point2D p9 = new Point2D(9, 7);
        Point2D p10 = new Point2D(7, 9.5);

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

    public static List<Point2D> generatePoints3(int numberOfPoints, Random random) {
        int xRange = 10;
        int yRange = 10;

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

    public static List<Point2D> generatePoints4(int numberOfPoints, int numberOfRegions, int maxRadius, Random random) {
        int xRange = 10;
        int yRange = 10;

        List<Point2D> tempList = new ArrayList<>();
        List<Point2D> regionPoints = new ArrayList<>();

        if (numberOfPoints < numberOfRegions)
            System.exit(1);

        double[] radius = new double[numberOfRegions];

        for (int i = 0; i < numberOfRegions; i++) {
            double randomX = random.nextDouble();
            double randomY = random.nextDouble();
            randomX = randomX * xRange;
            randomY = randomY * yRange;

            radius[i] = Math.max(random.nextInt(maxRadius) + random.nextDouble(), 1);

            Point2D regionPoint = new Point2D(randomX, randomY);

            System.out.println(regionPoint + "  Straal: " + radius[i]);

            regionPoints.add(regionPoint);

        }

        while (tempList.size() != numberOfPoints) {
            double randomX = random.nextDouble();
            double randomY = random.nextDouble();
            randomX = randomX * xRange;
            randomY = randomY * yRange;

            Point2D p = new Point2D(randomX, randomY);

            boolean badPoint = false;
            for (int i = 0; i < radius.length; i++) {
                if (regionPoints.get(i).getDistanceTo(p) < radius[i]) {
                    badPoint = true;
                    break;
                }
            }

            if (!badPoint)
                tempList.add(p);
        }
        return tempList;
    }

    public static List<Point2D> generatePoints5(String filename, Random random) {
        List<Point2D> pointSet = new ArrayList<>();
        BufferedReader br = null;
        double maxX = 0;
        double maxY = 0;
        try {

            br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                double[] intArray = Arrays.stream(line.trim().split("\\s+"))
                        .mapToDouble(Double::parseDouble)
                        .toArray();
                double x = intArray[0];
                double y = intArray[1];
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                Point2D p = new Point2D(x, y);
                pointSet.add(p);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double factorX = maxX / 10;
        double factorY = maxY / 10;

        for (Point2D p : pointSet) {
            p.setX(p.getX() / factorX + random.nextDouble() / 100);
            p.setY(p.getY() / factorY + random.nextDouble() / 100);
        }
        System.out.println(pointSet.size());
        return pointSet;

    }

    public static List<Point2D> generatePoints6(int numberOfPoints, Random random) {
        int xRange = 10;
        int yRange = 10;

        List<Point2D> tempList = new ArrayList<>();
        List<Point2D> regionPoints = new ArrayList<>();


        double[] radius = new double[4];
        double r1 = 3;
        double r2 = 2;
        double r3 = 3;
        double r4 = 2;
        radius[0] = r1;
        radius[1] = r2;
        radius[2] = r3;
        radius[3] = r4;
        regionPoints.add(new Point2D(3, 5));
        regionPoints.add(new Point2D(6, 3));
        regionPoints.add(new Point2D(6, 6));
        regionPoints.add(new Point2D(7, 7));

        while (tempList.size() != numberOfPoints) {
            double randomX = random.nextDouble();
            double randomY = random.nextDouble();
            randomX = randomX * xRange;
            randomY = randomY * yRange;

            Point2D p = new Point2D(randomX, randomY);

            boolean badPoint = false;
            for (int i = 0; i < radius.length; i++) {
                if (regionPoints.get(i).getDistanceTo(p) < radius[i]) {
                    badPoint = true;
                    break;
                }
            }

            if (!badPoint)
                tempList.add(p);
        }

        return tempList;
    }


}