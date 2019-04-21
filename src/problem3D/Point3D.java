package problem3D;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Point3D {
    private double x, y, z;
    private int id;

    private static int idTracker = 1;
    public Point3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.id = idTracker;
        idTracker++;
    }

    public Point3D(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = idTracker;
        idTracker++;
    }

    public double getX() {
        return x;
    }


    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.00",
                DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(x) + ";" + df.format(y) + ";" + df.format(z);
    }

    public String toStringWithId() {
        DecimalFormat df = new DecimalFormat("#.00",
                DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(x) + ";" + df.format(y) + ";" + df.format(z) + " id: " + id;
    }

}
