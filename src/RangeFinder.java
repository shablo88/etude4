import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RangeFinder {
    private static List<Phones> phones;
    private static String fileName = "coords.txt";
    private static DecimalFormat df = new DecimalFormat("#.##");

    public static void main(String[] args) {
        try{
            readFile();
        } catch (IOException e) {
            System.out.println(fileName + " not found");
        }
    }

    private static void readFile() throws IOException {
        List<String> inputs = Files.readAllLines(Paths.get(fileName));
        List<Phones> localPhones = new ArrayList<>();
        for (String line : inputs) {
            if (!line.equals("Telephone sites")) {
                String[] s = line.split(" ");
                localPhones.add(new Phones(Double.parseDouble(s[0]), Double.parseDouble(s[1])));
            }
        }
        RangeFinder.phones = localPhones;
        if(phones.size() < 12) {
            System.out.println("Not enough points!");
        } else {
            cluster(phones);
        }
    }
    private static void cluster(List<Phones> localPhones) {
        Range largest = new Range(new Phones(0.0,0.0),-1);
        for (int i = 0; i < localPhones.size(); i++) {
            for (int j = i + 1; j < localPhones.size(); j++) {
                for (int k = j + 1; k < localPhones.size(); k++) {
                    Range range = makeCircumcircle(localPhones.get(i), localPhones.get(j), localPhones.get(k));
                    if(range == null) {
                        break;
                    }
                    int count = 0;
                    int border = 0;
                    for(Phones m : localPhones) {
                        if(range.contains(m)) {
                            count++;
                            if(count > 11) {
                                break;
                            }
                        }
                        if(range.center.distance(m) == range.radius) {
                            border++;
                        }
                    }
                    if(count <= 11 && border + count >= 12) {

                        if(range.radius < largest.radius || largest.radius == -1) {
                            largest = range;
                        }
                    }
                }
            }
        }
        if(largest.radius == -1) {
            System.out.println("Failed to bound points");
        } else {
            System.out.println(df.format(largest.radius));
        }
    }

    private static Range makeCircumcircle(Phones a, Phones b, Phones c) {
        //"recenter" the three points on the minimum x and y values
        double originX = (Math.min(Math.min(a.x, b.x), c.x) + Math.max(Math.min(a.x, b.x), c.x)) / 2,
                originY = (Math.min(Math.min(a.y, b.y), c.y) + Math.max(Math.min(a.y, b.y), c.y)) / 2;
        //get the new co-ordinates in relation to this new origin
        double ax = a.x - originX, ay = a.y - originY,
                bx = b.x - originX, by = b.y - originY,
                cx = c.x - originX, cy = c.y - originY;
        //find the scale of the triangle
        double d = (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by)) * 2;
        //if the scale is 0, then all three points lie on a line such that ay = by = cy, or ax = bx = cx = 0
        if(ax == bx && bx == cx) {
            double radius = (Math.max(Math.max(ay,by),cy) - Math.min(Math.min(ay, by), cy))/2;
            return new Range(new Phones(ax + originX, radius + originY), radius);
        } else if(ay == by && by == cy) {
            double radius = (Math.max(Math.max(ax, bx), cx) - Math.min(Math.min(ax, bx), cx)) / 2;
            return new Range(new Phones(radius + originX, ay + originY), radius);
        }
        //finds the intersection of the bisectors of the three sides of the triangle - the center of a circumscribed circle
        double x = ((ax * ax + ay * ay) * (by - cy) + (bx * bx + by * by) * (cy - ay) + (cx * cx + cy * cy) * (ay - by)) / d,
                y = ((ax * ax + ay * ay) * (cx - bx) + (bx * bx + by * by) * (ax - cx) + (cx * cx + cy * cy) * (bx - ax)) / d;
        //makes a new point based on the calculated values and from the 'origin'
        Phones p = new Phones(originX + x, originY + y);
        //makes the radius the distance from the center to the furthest of the three points of the triangle
        double r = Math.max(Math.max(p.distance(a), p.distance(b)), p.distance(c));
        return new Range(p, r);
    }

    static final class Range {
        final Phones center;
        final double radius;
        //setter
        Range(Phones center, double radius) {
            this.center = center;
            this.radius = radius;
        }
        //checks if the point is within the circle
        boolean contains(Phones p) {
            return (center.distance(p) < radius);
        }
        @Override
        public String toString() {
            return String.format("Range(x=%g, y=%g, radius=%g)", center.x, center.y, radius);
        }
    }


}

final class Phones {
    final double x;
    final double y;
    //setter
    Phones(double x, double y) {
        this.x = x;
        this.y = y;
    }
    //find the distance between two Phones
    double distance(Phones p) {
        return Math.hypot(x - p.x, y - p.y);
    }
    @Override
    public String toString() {
        return String.format("Phones(%g, %g)", x, y);
    }
}
