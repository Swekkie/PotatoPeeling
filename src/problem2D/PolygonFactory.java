package problem2D;

import java.util.*;

public class PolygonFactory {

    private List<Point2D> setOfPoints;
    private Random random;

    public PolygonFactory(List<Point2D> setOfPoints, Random random){
        this.setOfPoints = setOfPoints;
        this.random = random;
    }

    public Polygon2D generateNeighbour(Polygon2D polygon){
        Polygon2D neighbour = new Polygon2D(polygon);
        List<Point2D> pointListPolygon = neighbour.point2DList;
        int index = deletePoints(pointListPolygon);
        addPoints(index,pointListPolygon);
        return neighbour;
    }

    private int deletePoints(List<Point2D> pointListPolygon){
        // generate random index from where to remove points
        int index = random.nextInt(pointListPolygon.size());
        // generate number of random number of points to remove (0-1-2-3)
        // or less (after deletion we still want 3 points left)
        int toDelete = random.nextInt(Math.min(2,pointListPolygon.size()-2));
        // remove points
        for(int i = 0; i<toDelete; i++){
            if(index>pointListPolygon.size()-1)
                pointListPolygon.remove(0);
            else
                pointListPolygon.remove(index);
        }
        // return index (needed for adding points)
        return index;
    }

    private void addPoints(int index, List<Point2D> pointListPolygon){
        // deleted points form new edge E
        // 4 possibilities of adding new points
        // 1) dont add points 2) add in edge E-1 3) add in edge E 4) add in edge E+1

        int [] indexing = new int[4];
        indexing[0] = index-2;
        indexing[1] = index-1;
        indexing[2] = index;
        indexing[3] = index+1;
        int listSize = pointListPolygon.size();
        for(int i =0; i<4; i++){
            if(indexing[i] > listSize-1)
                indexing[i]=indexing[i] % listSize;
            else if(indexing[i]<0)
                indexing[i]+=listSize;
        }
        // generate random number for percentages
        double randomNumber = random.nextDouble(); //0 -->1

        int indexFrom, indexTo;
        if(randomNumber<0.25){
            return;
        }
        else if(randomNumber<0.5){
            indexFrom = indexing[0];
            indexTo = indexing[1];
        }
        else if(randomNumber<0.75){
            indexFrom = indexing[1];
            indexTo = indexing[2];
        }
        else{
            indexFrom = indexing[2];
            indexTo = indexing[3];
        }

        List<Point2D> pointsToAdd = addPointsToEdge(indexFrom,indexTo,pointListPolygon);

        for(Point2D p: pointsToAdd){
            pointListPolygon.add(indexTo,p);
        }

    }

    private List<Point2D> addPointsToEdge(int b, int c, List<Point2D> pointListPolygon) {
        List<Point2D> usablePoints = new ArrayList<>(setOfPoints);
        usablePoints.removeAll(pointListPolygon);
        int a = b - 1;
        if(a<0)
            a+= pointListPolygon.size();
        int d = c + 1;
        if(d>pointListPolygon.size()-1)
            d-= pointListPolygon.size();
        // a is d if 3 points in polygon

        Point2D pointA = pointListPolygon.get(a);
        Point2D pointB = pointListPolygon.get(b);
        Point2D pointC = pointListPolygon.get(c);
        Point2D pointD = pointListPolygon.get(d);

        ListIterator<Point2D> iter = usablePoints.listIterator();
        while (iter.hasNext()) {
            Point2D p = iter.next();
            if (p.isOnLeftOfVector(pointB, pointA)
                    || p.isOnLeftOfVector(pointB, pointC)
                    || p.isOnLeftOfVector(pointD, pointC)) {
                iter.remove();
            }
        }

        // sort usable points by angle
        Collections.sort(usablePoints, new Comparator<Point2D>() {
            @Override
            public int compare(Point2D p1, Point2D p2) {
                int x = Double.compare(calculateAngle(p1,pointA,pointB),
                        calculateAngle(p2,pointA,pointB));
                if(x==0)
                    System.out.println("oeps");
                return x;
            }
        });

        // delete points that will make the polygon not empty
        iter = usablePoints.listIterator();
        Polygon2D polygon = new Polygon2D();
        polygon.addPoint2DToEnd(pointC);
        polygon.addPoint2DToEnd(pointB);
        while (iter.hasNext()) {
            Point2D p = iter.next();
            polygon.addPoint2DToEnd(p);
            if (!polygon.isEmpty(usablePoints)){
                iter.remove();
            }
            polygon.removeLastPoint();
        }

        if(usablePoints.size()<2)
            return usablePoints;


        ////// geen of 1 punt toevoegen
        int chance = random.nextInt(2);
        if(chance ==0 ) return new ArrayList<>();
        else{
            List<Point2D> temp = new ArrayList<>();
            temp.add(usablePoints.get(random.nextInt(usablePoints.size())));
            return temp;
        }
        ////// deze blok of blok hieronder

        /*


        // generate random sequence by deleting elements from list
        // int toDelete = random.nextInt(usablePoints.size());
        int toDelete = random.nextInt(usablePoints.size());

        // if 4 items in list, we can remove 0, 1, 2 or 3 items. If we remove 4 items then we will not add points

        for(int i = 0; i<toDelete; i++){
            int indexToRemove = random.nextInt(usablePoints.size());
            usablePoints.remove(indexToRemove);
        }

        return usablePoints;
        */
    }

    public double calculateAngle(Point2D p, Point2D edgeFrom, Point2D edgeTo){
        double xA = edgeFrom.getX();
        double yA = edgeFrom.getY();
        double xB = edgeTo.getX();
        double yB = edgeTo.getY();
        double crossProduct = (xB-xA)*(p.getY()-yA)-(yB-yA)*(p.getX()-xA);
        return crossProduct;
    }

}

