/*
 * References: https://www.nayuki.io/res/smallest-enclosing-circle/SmallestEnclosingCircleTest.java
 * https://en.wikipedia.org/wiki/Circumscribed_circle
 */
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LargestExcludingCircle {
    private static List<Point> points;

    public LargestExcludingCircle(List<Phones> phones) {
        points = new ArrayList<>();
        for(Phones phone : phones) {
            points.add(new Point(phone.x, phone.y));
        }
        if (points.size() < 12) { //if there are less than 12 points, return
            System.out.println("Not enough points");
            return;
        }
        //Find the 12 closest together points
        List<Circle> iterations = new ArrayList<>();
        for(int i = 0; i < points.size(); i++) {
            iterations.add(cluster(points, i));
        }
        int smallest = 0;
        double radius = iterations.get(0).radius;
        for(int i = 0; i < iterations.size(); i++) {
            if(iterations.get(i).radius < radius) {
                smallest = i;
                radius = iterations.get(i).radius;
            }
        }
        Circle smallestEnclosing = iterations.get(smallest);
        Circle largestExcluding = largest(smallestEnclosing, false, smallestEnclosing.shrink().grow());
        System.out.println(largestExcluding.radius);
    }

//    public static void main(String[] args) {
//            //find the largest excluding circle
//            if (points.size() < 12) {
//                System.out.println("There is no upper limit on the range, as there are less than 12 points!");
//            } else {
//                Circle largestExcluding = largest(smallestEnclosing, false, smallestEnclosing.shrink().grow());
//                //print a summary
//                if (largestExcluding.radius > 0) {
//                    System.out.println(String.format("The largest circle that can be drawn such that no more than 11 points are" +
//                                    " within its area is of radius %.2fm.\nThe ideal circle is centred on %.2fm east and %.2fm" +
//                                    " north of the central point", largestExcluding.radius, largestExcluding.center.x,
//                            largestExcluding.center.y));
//                } else if (largestExcluding.radius == 0) {
//                    System.out.println("The largest circle that can be drawn such that no more than 11 points are" +
//                            " within its area is of radius 0m.\nThis means that all except one phone are at the exact same location," +
//                            " or that the points are too close to each other to have a feasible maximum range");
//                } else {
//                    System.out.println("There is no possible circle that includes at most eleven points, as they are all at the same location!");
//                }
//            }
////            debug
////            //finds the excluded points
////            List<Point> excluded = new ArrayList<>();
////            for(Point individual : points) {
////                if(largestExcluding.contains(individual)) {
////                    excluded.add(individual);
////                }
////            }
////            //prints the excluded points - testing purposes
////            for (Point a : excluded){
////                System.out.println(a + " is not within this circle");
////            }
//    }

    private static Circle largest(Circle old, boolean grow, Circle original) {
        Circle newCircle;
        //boolean input determines whether to grow or shrink the circle
        if (grow) {
            newCircle = old.grow();
        } else {
            newCircle = old.shrink();
        }
        //checks to see how many points are included
        int i = 0;
        for (Point individual : points) {
            if (!newCircle.doesNotContain(individual)) {
                i++;
            }
        }
        //if the radius is the original, return a circle of radius 0.01 smaller
        if (newCircle.radius == original.radius) {
            return newCircle;
        }
        //else if there are less than eleven points included, then see if you can enlarge the circle and still include at most eleven
        else if (i <= 11) {
            return largest(newCircle, true, original);
        }
        //if there are more than 11 points included, then shrink the circle
        else {
            return largest(newCircle, false, original);
        }
    }

    private static Circle cluster(List<Point> localPoints, int closest) {
        List<Double> distances = new ArrayList<>();
        //get the 12th largest distance between each point and all points around it
//        List<Double> distance = new ArrayList<>();
//        for (Point i : localPoints) {
//            distance.add(localPoints.get(closest).distance(i));
//            Collections.sort(distance);
//        }
//        distances.add(distance.get(11));
        //get the point which has the smallest circle centred on itself containing 11 other points
//        double size = distances.get(0);
//        for (int i = 0; i < distances.size(); i++) {
//            if (distances.get(i) < size) {
//                closest = i;
//                size = distances.get(i);
//            }
//        }
        //get the distances from this point to all others
        for (Point k : localPoints) {
            distances.add(localPoints.get(closest).distance(k));
        }
        List<Double> dist = new ArrayList<>();
        //get the 11 closest points' distances, then add their Points to cluster
        for (Double distance : distances) {
            if (dist.size() < 12) {
                dist.add(distance);
                Collections.sort(dist);
            } else if (distance < dist.get(11)) {
                dist.remove(11);
                dist.add(distance);
                Collections.sort(dist);
            }
        }
        List<Point> pointCluster = new ArrayList<>();
        for (double i : dist) {
            pointCluster.add(localPoints.get(distances.indexOf(i)));
        }
        return makeCircle(pointCluster);
    }

    /*
     * Returns the smallest circle that encloses all the given points. Expected run time of O(n)
     * Initially: No boundary points known
     */
    private static Circle makeCircle(List<Point> points) {
        // Copy list to preserve the data field
        List<Point> localPoints = new ArrayList<>(points);
        // Progressively check if points are within circle, recalculate circle if necessary
        Circle localCircle = null;
        for (int i = 0; i < localPoints.size(); i++) {
            Point p = localPoints.get(i);
            if (localCircle == null || localCircle.doesNotContain(p)) {
                localCircle = makeCircle(localPoints.subList(0, i + 1), p);
            }
        }
        return localCircle;
    }

    /*
     * If one edge point is known, check to see if the circle contains the remaining points. If not, then resize the circle
     */
    private static Circle makeCircle(List<Point> points, Point p1) {
        Circle localCircle = new Circle(p1, 0);
        for (int i = 0; i < points.size(); i++) {
            Point p2 = points.get(i);
            if (localCircle.doesNotContain(p2)) {
                if (localCircle.radius == 0)
                    localCircle = makeDiameter(p1, p2);
                else
                    localCircle = makeCircle(points.subList(0, i + 1), p1, p2);
            }
        }
        return localCircle;
    }


    /*
     * If two edge points are known, check to see if the circle contains the remaining points. If not, then resize the circle
     */
    private static Circle makeCircle(List<Point> points, Point p1, Point p2) {
        Circle localCircle = makeDiameter(p1, p2), left = null, right = null;

        Point p3 = p2.subtract(p1);
        for (Point r : points) {
            // For each point not in the circle
            if (localCircle.doesNotContain(r)) {
                // Form a circumcircle and classify it being on the left or right of the origin
                double determinant = p3.cross(r.subtract(p1));
                Circle c = makeCircumcircle(p1, p2, r);
                if (c != null) {
                    if (determinant > 0 && (left == null || p3.cross(c.center.subtract(p1)) > p3.cross(left.center.subtract(p1))))
                        left = c;
                    else if (determinant < 0 && (right == null || p3.cross(c.center.subtract(p1)) < p3.cross(right.center.subtract(p1))))
                        right = c;
                }
            }
        }
        // Select which circle to return
        if (left == null && right == null)
            return localCircle;
        else if (left == null)
            return right;
        else if (right == null)
            return left;
        else
            return left.radius <= right.radius ? left : right;
    }

    private static Circle makeDiameter(Point a, Point b) {
        Point c = new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
        return new Circle(c, Math.max(c.distance(a), c.distance(b)));
    }

    private static Circle makeCircumcircle(Point a, Point b, Point c) {
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
        if (d == 0) return null;
        //finds the intersection of the bisectors of the three sides of the triangle - the center of a circumscribed circle
        double x = ((ax * ax + ay * ay) * (by - cy) + (bx * bx + by * by) * (cy - ay) + (cx * cx + cy * cy) * (ay - by)) / d,
                y = ((ax * ax + ay * ay) * (cx - bx) + (bx * bx + by * by) * (ax - cx) + (cx * cx + cy * cy) * (bx - ax)) / d;
        //makes a new point based on the calculated values and from the 'origin'
        Point p = new Point(originX + x, originY + y);
        //makes the radius the distance from the center to the furthest of the three points of the triangle
        double r = Math.max(Math.max(p.distance(a), p.distance(b)), p.distance(c));
        return new Circle(p, r);
    }

    static final class Circle {
        private DecimalFormat df = new DecimalFormat("#.##");
        final Point center;
        final double radius;
        //setter
        Circle(Point center, double radius) {
            this.center = center;
            this.radius = radius;
        }
        //checks if the point is within the circle
        boolean doesNotContain(Point p) {
            return !(center.distance(p) <= radius * (1 + 1e-14));
        }
        @Override
        public String toString() {
            return String.format("LargestExcludingCircle.Circle(x=%g, y=%g, radius=%g)", center.x, center.y, radius);
        }
        //shrink and grow are used to change the size of the smallest enclosing circle to find the largest excluding circle
        Circle shrink() {
            df.setRoundingMode(RoundingMode.CEILING);
            return new Circle(center.round(), Double.parseDouble(df.format(radius)) - 0.01);
        }
        Circle grow() {
            return new Circle(center, radius + 0.01);
        }
    }
}

final class Point {
    private static DecimalFormat df = new DecimalFormat("#.##");
    final double x;
    final double y;
    //setter
    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    //subtract a point from another
    Point subtract(Point p) {
        return new Point(x - p.x, y - p.y);
    }
    //find the distance between two points
    double distance(Point p) {
        return Math.hypot(x - p.x, y - p.y);
    }
    //determinant
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