package problem2D;

import java.util.*;

public class LocalSearch {

    private List<Point2D> inputPoints; // input set points
    private List<Polygon2D> foundPolygons;
    private Random random;
    private Polygon2D startingPolygon;
    private Polygon2D maxPolygon = null;

    public LocalSearch(List<Point2D> inputPoints, Polygon2D startingPolygon){
        this.inputPoints = inputPoints;
        this.random = new Random();
        this.startingPolygon = startingPolygon;
        this.foundPolygons = new ArrayList<>();
        Collections.shuffle(inputPoints);
    }

    public List<Polygon2D> solve(long timeInMillis) {
        long startTime = System.currentTimeMillis();
        boolean timeReached = false;
        startingPolygon.calculateArea();
        maxPolygon = startingPolygon;
        do {
            Polygon2D bestSolutionCycle = startingPolygon;
            foundPolygons.add(bestSolutionCycle);
            int iterations = 5 * inputPoints.size();

            for (int i = 0; i < iterations; i++) {
                if (timeInMillis < System.currentTimeMillis() - startTime) {
                    timeReached = true;
                    break;
                }
                Polygon2D neighbour = generateNeighbour(bestSolutionCycle);
                neighbour.calculateArea();
                if (neighbour.calculateArea() > bestSolutionCycle.area) {
                    bestSolutionCycle = neighbour;
                    foundPolygons.add(neighbour);
                    if(bestSolutionCycle.area>maxPolygon.area){
                        maxPolygon = bestSolutionCycle;
                    }
                }

            }

        }while(!timeReached);

        long endTime = System.currentTimeMillis();
        System.out.println("LOCAL SEARCH HEURISTIC");
        //System.out.print("Max polygon: " + maxPolygon);
        System.out.println("Area: " + maxPolygon.area);
        System.out.println("Time (ms): " + (endTime - startTime));
        return foundPolygons;
    }

    public Polygon2D generateNeighbour(Polygon2D polygon){
        Polygon2D neighbour = new Polygon2D(polygon);
        List<Point2D> pointListPolygon = neighbour.point2DList;
        int index = deletePoints(pointListPolygon);
        addPoints(index,pointListPolygon);
        return neighbour;
    }

    // removes consecutive points and returns index from where the points were removed
    private int deletePoints(List<Point2D> pointListPolygon){
        // generate random index from where to remove points
        int index = random.nextInt(pointListPolygon.size());
        // generate number of random number of points to remove
        // or less (after deletion we still want 3 points left)
        int maxToDelete = 3; // 0-1-2-3
        int toDelete = random.nextInt(Math.min(maxToDelete+1,pointListPolygon.size()-2));
        // remove points
        for(int i = 0; i<toDelete; i++){
            if(index>pointListPolygon.size()-1) {
                pointListPolygon.remove(0);
                index = 0;
            }
            else
                pointListPolygon.remove(index);
        }
        // return index (needed for adding points)
        return index;
    }

    // this function gives us the index where we will add the points
    // it calls the function that will actually add the points
    private void addPoints(int index, List<Point2D> pointListPolygon){
        // deleted points form new edge E
        // 4 possibilities of adding new points
        // 1) add in edge E-1 2) add in edge E 3) add in edge E+1
        int [] indexing = new int[4];
        indexing[0] = index-2;
        indexing[1] = index-1;
        indexing[2] = index;
        indexing[3] = index+1;
        int listSize = pointListPolygon.size();
        for(int i =0; i<4; i++){
            indexing[i]=(indexing[i]+listSize)%listSize;
        }
        // generate random number for percentages
        double randomNumber = random.nextDouble(); //0 -->1

        int indexFrom, indexTo;

        if(randomNumber<0.33){
            indexFrom = indexing[0];
            indexTo = indexing[1];
        }
        else if(randomNumber<0.66){
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
        List<Point2D> usablePoints = new ArrayList<>(inputPoints);
        usablePoints.removeAll(pointListPolygon);
        int a = b - 1;
        if(a<0)
            a+= pointListPolygon.size();
        int d = c + 1;
        if(d>pointListPolygon.size()-1)
            d-= pointListPolygon.size();
        // a is d if 3 points in polygon

        // check which points can be added to edge b-c
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

        // makes changes in usablePointsList
        findPointsForEdge(usablePoints,pointB,pointC,false);

        // sort usable points by angle
        Collections.sort(usablePoints, new Comparator<Point2D>() {
            @Override
            public int compare(Point2D p1, Point2D p2) {
                if(p1.isOnLeftOfVectorReturnCrossproduct(pointB,p2)>0)
                    return 1;
                else if(p1.isOnLeftOfVectorReturnCrossproduct(pointB,p2)<0)
                    return -1;
                else {
                    return 0;
                }
            }
        });

        // usableList contains points we can add (in good order)

        // if there is only 1 point that can be added, return the list
        // same for no points
        if(usablePoints.size()<2)
            return usablePoints;

        // if there are two or more points that can be added, 2 possibilities:
        // 1) pick 1 point and add that
        // 2) pick 2 consecutive points, if we pick more points its not guaranteed that we add a convex chain
        boolean coinFlip = random.nextBoolean();
        if(coinFlip){
            // option 1
            Point2D p =  usablePoints.remove(random.nextInt(usablePoints.size()));
            usablePoints = new ArrayList<>();
            usablePoints.add(p);
        }
        else {
            // option 2
            int index = random.nextInt((usablePoints.size() - 1));
            Point2D p1 = usablePoints.get(index);
            Point2D p2 = usablePoints.get(index + 1);
            usablePoints = new ArrayList<>();
            usablePoints.add(p2);
            usablePoints.add(p1);

        }
        return usablePoints;

    }


    private void findPointsForEdge(List<Point2D> points, Point2D start, Point2D end, boolean positive) {
        int sign;
        if (positive)
            sign = 1;
        else
            sign = -1;

        Iterator<Point2D> it = points.iterator();
        while (it.hasNext()) {
            Point2D p = it.next();
            if (p.calculateAreaGreedyAdd(start,end)==0) {
                it.remove();
            }
        }

        Collections.sort(points, new Comparator<Point2D>() {
            // sign is -1 if areas are negative
            @Override
            public int compare(Point2D p1, Point2D p2) {
                return sign * Double.compare(p1.areaForGreedyAdd, p2.areaForGreedyAdd);
            }
        });

        ListIterator<Point2D> iter = points.listIterator();
        Set<Point2D> toRemove = new HashSet<>();
        while (iter.hasNext()) {
            Point2D p = iter.next();
            if (toRemove.contains(p)) {
                iter.remove();
            } else {
                toRemove.addAll(removeForbiddenPoints(points, iter.nextIndex(), start, end, p, positive));
            }
        }
    }

    private Set<Point2D> removeForbiddenPoints(List<Point2D> points, int index, Point2D start, Point2D end,
                                               Point2D checkedPoint, boolean positive) {
        Set<Point2D> toRemove = new HashSet<>();

        // switch start and end for correct vectors if right list (negative areas)
        if (!positive) {
            Point2D temp = end;
            end = start;
            start = temp;
        }

        ListIterator<Point2D> iter = points.listIterator(index);
        while (iter.hasNext()) {
            Point2D point = iter.next();

            if (!point.isOnLeftOfVector(checkedPoint, start) && !point.isOnLeftOfVector(end, checkedPoint)) {
                toRemove.add(point);
            }

        }

        return toRemove;

    }
}
