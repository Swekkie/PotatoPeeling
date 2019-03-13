import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Point2D {
    private double x, y;

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

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.00",
                DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(x) + ";" + df.format(y);
    }


}
