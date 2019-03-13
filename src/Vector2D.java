public class Vector2D {
    
    private Point2D start;
    private Point2D end;
    private double angle; // in rad, between 0 and 2pi
    
    public Vector2D(){
        this.start = null;
        this.end = null;
    }

    public Vector2D(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
        calculateAngle();
    }

    public Point2D getStart() {
        return start;
    }

    public void setStart(Point2D start) {
        this.start = start;
        // calculate slope if End is not null
        if(end!=null)
            calculateAngle();
    }

    public Point2D getEnd() {
        return end;
    }

    public void setEnd(Point2D end) {
        this.end = end;
        if(end!=null)
            calculateAngle();
    }

    public double getAngle(){
        return angle;
    }
    public void calculateAngle() {
        double deltaX = end.getX()-start.getX();
        double deltaY = end.getY()-start.getY();
        angle = Math.atan(deltaY / deltaX);
        if(deltaX <0){
            if(deltaY>=0)
                angle += Math.PI;
            else
                angle -= Math.PI;
        }
        if(angle<0)
            angle += (2 * Math.PI);
    }

    @Override
    public String toString() {
        return "Vector met start: " + start + " en eind " + end;
    }
}
