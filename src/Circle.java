import java.math.RoundingMode;
import java.text.DecimalFormat;

final class Circle {
    private DecimalFormat df = new DecimalFormat("#.##");

    final Point c;   // Center
    final double r;  // Radius

    Circle(Point c, double r) {
        this.c = c;
        this.r = r;
    }

    boolean contains(Point p) {
        return c.distance(p) <= r * (1 + 1e-14);
    }

    @Override
    public String toString() {
        return String.format("Circle(x=%g, y=%g, r=%g)", c.x, c.y, r);
    }

    //shrink and grow are used to change the size of the smallest enclosing circle to find the largest excluding circle
    Circle shrink() {
        df.setRoundingMode(RoundingMode.CEILING);
        return new Circle(c.round(), Double.parseDouble(df.format(r)) - 0.01);
    }

    Circle grow() {
        return new Circle(c, r + 0.01);
    }
}
