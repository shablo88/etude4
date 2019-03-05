import java.text.DecimalFormat;

final class Point {
    private static DecimalFormat df = new DecimalFormat("#.##");

    final double x;
    final double y;


    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Point subtract(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    double distance(Point p) {
        return Math.hypot(x - p.x, y - p.y);
    }

    // Signed area / determinant thing
    double cross(Point p) {
        return x * p.y - y * p.x;
    }

    @Override
    public String toString() {
        return String.format("Point(%g, %g)", x, y);
    }

    //round returns a point in 2 dp as the input file is in 2 dp
    Point round() {
        return new Point(Double.parseDouble(df.format(x)), Double.parseDouble(df.format(y)));
    }
}
