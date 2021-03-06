package problem2D;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Point2D {
    private double x, y;
    public int id;
    public double areaForGreedyAdd;

    public Point2D() {
        this.x = 0;
        this.y = 0;

    }

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x){
        this.x=x;
    }

    public void setY(double y){
        this.y=y;
    }

    public int getId() {
        return id;
    }


    public double calculateAngleOfVectorFrom(Point2D from){
        // doesn't do the tan-1, not necessary
        return (this.y-from.y)/(this.x-from.x);
    }


    // on left of vector = on the counter clockwise half of the vector (positive cross product)
    // false if the point is on the vector
    public boolean isOnLeftOfVector(Point2D pointA, Point2D pointB){
        double xA = pointA.getX();
        double yA = pointA.getY();
        double xB = pointB.getX();
        double yB = pointB.getY();
        double crossProduct = (xB-xA)*(this.y-yA)-(yB-yA)*(this.x-xA);
        if(crossProduct>0)
            return true;
        return false;
    }

    public double isOnLeftOfVectorReturnCrossproduct(Point2D pointA, Point2D pointB){
        double xA = pointA.getX();
        double yA = pointA.getY();
        double xB = pointB.getX();
        double yB = pointB.getY();
        return (xB-xA)*(this.y-yA)-(yB-yA)*(this.x-xA);
    }

    public double getSquaredDistanceTo(Point2D p){
        return Math.pow(this.getX()-p.getX(),2) + Math.pow(this.getY()-p.getY(),2);
    }
    public double getDistanceTo(Point2D p){
        return Math.sqrt(Math.pow(this.getX()-p.getX(),2) + Math.pow(this.getY()-p.getY(),2));
    }

    public double calculateAreaGreedyAdd(Point2D start, Point2D end){
        areaForGreedyAdd = start.getX() * (end.getY()-this.getY())+end.getX() *
                (this.getY()-start.getY())+ this.getX() * (start.getY()-end.getY());
        return areaForGreedyAdd;
    }

    @Override
    public String toString() {
        return x + ";" + y + " id: " + id;
    }

    public String toStringFile() {

        DecimalFormat df = new DecimalFormat("#.000000",
                DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(x) + ";" + df.format(y);

    }


}
