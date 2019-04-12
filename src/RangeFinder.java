import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RangeFinder {
    private static List<Phones> phones;
    private static String fileName = "coords.txt";
    private static final double tolerance = 0.0001;
    private static final int maximumInRange = 11;

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
        if(phones.size() < maximumInRange + 1) {
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
                    Phones temp1 = new Phones(localPhones.get(i).x, localPhones.get(i).y);
                    Phones temp2 = new Phones(localPhones.get(j).x, localPhones.get(j).y);
                    Phones temp3 = new Phones(localPhones.get(k).x, localPhones.get(k).y);
                    boolean bypass = false;
                    Range range = makeCircumcircle(temp1, temp2, temp3);
                    if(range == null) bypass = true;
                    if(!bypass && range.radius <= 0) bypass = true;

                    if(!bypass) {
                        int count = 0;
                        int border = 0;
                        for (Phones m : localPhones) {
                            if (range.contains(m)) {
                                count++;
                                if (count > maximumInRange) break;
                            }
                            if (Math.abs(range.center.distance(m) - range.radius) <= tolerance) border++;
                        }
                        if (count <= maximumInRange && border + count >= maximumInRange + 1 && (range.radius < largest.radius || largest.radius == -1))
                            largest = range;
                    }

                    bypass = false;
                    if(temp1.x == temp2.x || temp1.x == temp3.x || temp2.x == temp3.x) {
                        if(temp1.x == temp2.x) temp3.x = temp1.x;
                        else if(temp1.x == temp3.x) temp2.x = temp1.x;
                        else if(temp3.x == temp2.x) temp1.x = temp2.x;
                        Range range2 = makeCircumcircle(temp1, temp2, temp3);
                        if(range2 == null) bypass = true;
                        if(!bypass && range2.radius <= 0) bypass = true;
                        if(!bypass) {
                            int count = 0;
                            int border = 0;
                            for (Phones m : localPhones) {
                                if (range2.contains(m)) {
                                    count++;
                                    if (count > maximumInRange) break;
                                }
                                if (Math.abs(range2.center.distance(m) - range2.radius) <= tolerance) border++;
                            }
                            if (count <= maximumInRange && border + count >= maximumInRange + 1 && (range2.radius < largest.radius || largest.radius == -1))
                                largest = range2;
                        }
                    }

                    temp1 = new Phones(localPhones.get(i).x, localPhones.get(i).y);
                    temp2 = new Phones(localPhones.get(j).x, localPhones.get(j).y);
                    temp3 = new Phones(localPhones.get(k).x, localPhones.get(k).y);
                    bypass = false;
                    if(temp1.y == temp2.y || temp1.y == temp3.y || temp2.y == temp3.y) {
                        if(temp1.y == temp2.y) temp3.y = temp1.y;
                        else if(temp1.y == temp3.y) temp2.y = temp1.y;
                        else if(temp3.y == temp2.y) temp1.y = temp2.y;
                        Range range3 = makeCircumcircle(temp1, temp2, temp3);
                        if(range3 == null) bypass = true;
                        if(!bypass && range3.radius <= 0) bypass = true;
                        if(!bypass) {
                            int count = 0;
                            int border = 0;
                            for (Phones m : localPhones) {
                                if (range3.contains(m)) {
                                    count++;
                                    if (count > maximumInRange) break;
                                }
                                if (Math.abs(range3.center.distance(m) - range3.radius) <= tolerance) border++;
                            }
                            if (count <= maximumInRange && border + count >= maximumInRange + 1 && (range3.radius < largest.radius || largest.radius == -1))
                                largest = range3;
                        }
                    }

                    temp1 = new Phones(localPhones.get(i).x, localPhones.get(i).y);
                    temp2 = new Phones(localPhones.get(j).x, localPhones.get(j).y);
                    temp3 = new Phones(localPhones.get(k).x, localPhones.get(k).y);

                    Range range4 = midRange(temp1, temp2);
                    Range range5 = midRange(temp1, temp3);
                    Range range6 = midRange(temp2, temp3);
                    if (range4.radius > 0) {
                        int count = 0;
                        int border = 0;
                        for (Phones m : localPhones) {
                            if (range4.contains(m)) {
                                count++;
                                if (count > maximumInRange) break;
                            }
                            if (Math.abs(range4.center.distance(m) - range4.radius) <= tolerance) border++;
                        }
                        if (count <= maximumInRange && border + count >= maximumInRange + 1 && (range4.radius < largest.radius || largest.radius == -1))
                            largest = range4;
                    }

                    if (range5.radius > 0) {
                        int count = 0;
                        int border = 0;
                        for (Phones m : localPhones) {
                            if (range5.contains(m)) {
                                count++;
                                if (count > maximumInRange) break;
                            }
                            if (Math.abs(range5.center.distance(m) - range5.radius) <= tolerance) border++;
                        }
                        if (count <= maximumInRange && border + count >= maximumInRange + 1 && (range5.radius < largest.radius || largest.radius == -1))
                            largest = range5;
                    }

                    if (range6.radius > 0) {
                        int count = 0;
                        int border = 0;
                        for (Phones m : localPhones) {
                            if (range6.contains(m)) {
                                count++;
                                if (count > maximumInRange) break;
                            }
                            if (Math.abs(range6.center.distance(m) - range6.radius) <= tolerance) border++;
                        }
                        if (count <= maximumInRange && border + count >= maximumInRange + 1 && (range6.radius < largest.radius || largest.radius == -1))
                            largest = range6;
                    }
                }
            }
        }
        List<Range> iterations = new ArrayList<>();
        for(int i = 0; i < phones.size(); i++) iterations.add(clusterPhones(phones, i));

        for (Range iteration : iterations) {
            if (iteration.radius > 0) {
                int count = 0;
                int border = 0;
                for (Phones m : localPhones) {
                    if (iteration.contains(m)) {
                        count++;
                        if (count > maximumInRange) break;
                    }
                    if (Math.abs(iteration.center.distance(m) - iteration.radius) <= 0) border++;
                }
                if (count <= maximumInRange && border + count >= maximumInRange + 1 && (iteration.radius < largest.radius || largest.radius == -1))
                    largest = iteration;
            }
        }

        if(largest.radius == -1) System.out.println("Failed to bound points");
        else System.out.println(String.format("%f radius, centered at %f, %f",largest.radius, largest.center.x, largest.center.y));
    }

    private static Range clusterPhones(List<Phones> listPhones, int i) {
        List<Double> distances = new ArrayList<>();
        //get the distances from this point to all others
        for (Phones k : listPhones) {
            distances.add(listPhones.get(i).distance(k));
        }
        List<Double> dist = new ArrayList<>();
        //get the 11 closest points' distances, then add their Points to cluster
        for (Double distance : distances) {
            if (dist.size() < maximumInRange + 1) {
                dist.add(distance);
                Collections.sort(dist);
            } else if (distance < dist.get(maximumInRange)) {
                dist.remove(maximumInRange);
                dist.add(distance);
                Collections.sort(dist);
            }
        }
        List<Phones> pointCluster = new ArrayList<>();
        for (double j : dist) {
            pointCluster.add(listPhones.get(distances.indexOf(j)));
        }
        return smallestIncluding(pointCluster);

    }

    private static Range midRange(Phones a, Phones b) {
        double radius = a.distance(b)/2;
        double x = a.x-(a.x-b.x)/2;
        double y = a.y-(a.y-b.y)/2;
        return new Range(new Phones(x,y),radius);

    }

    private static Range smallestIncluding(List<Phones> set) {
        List<Phones> localPhones = new ArrayList<>(set);
        // Progressively check if points are within circle, recalculate circle if necessary
        Range localRange = null;
        for (int i = 0; i < localPhones.size(); i++) {
            Phones p = localPhones.get(i);
            if (localRange == null || !localRange.contains(p)) {
                localRange = makeRange(localPhones.subList(0, i + 1), p);
            }
        }
        return localRange;
    }

    private static Range makeRange(List<Phones> phones, Phones p1) {
        Range localRange = new Range(p1, 0);
        for (int i = 0; i < phones.size(); i++) {
            Phones p2 = phones.get(i);
            if (!localRange.contains(p2)) {
                if (localRange.radius == 0)
                    localRange = makeDiameter(p1, p2);
                else
                    localRange = makeRange(phones.subList(0, i + 1), p1, p2);
            }
        }
        return localRange;
    }

    private static Range makeRange(List<Phones> subList, Phones p1, Phones p2) {
        Range localRange = makeDiameter(p1, p2), left = null, right = null;

        Phones p3 = p2.subtract(p1);
        for (Phones r : subList) {
            // For each point not in the circle
            if (!localRange.contains(r)) {
                // Form a circumcircle and classify it being on the left or right of the origin
                double determinant = p3.cross(r.subtract(p1));
                Range c = makeCircumcircle(p1, p2, r);
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
            return localRange;
        else if (left == null)
            return right;
        else if (right == null)
            return left;
        else
            return left.radius <= right.radius ? left : right;
    }

    private static Range makeDiameter(Phones a, Phones b) {
        Phones c = new Phones((a.x + b.x) / 2, (a.y + b.y) / 2);
        return new Range(c, Math.max(c.distance(a), c.distance(b)));
    }

    private static Range makeCircumcircle(Phones a, Phones b, Phones c) {
        //"recenter" the three points on the minimum x and y values
        double originX = (Math.min(Math.min(a.x, b.x), c.x) + Math.max(Math.max(a.x, b.x), c.x)) / 2,
                originY = (Math.min(Math.min(a.y, b.y), c.y) + Math.max(Math.max(a.y, b.y), c.y)) / 2;
        //get the new co-ordinates in relation to this new origin
        double ax = a.x - originX, ay = a.y - originY,
                bx = b.x - originX, by = b.y - originY,
                cx = c.x - originX, cy = c.y - originY;
        //find the scale of the triangle
        double d = (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by)) * 2;
        //if the scale is 0, then all three points lie on a line such that ay = by = cy, or ax = bx = cx = 0
        if(ax == bx && bx == cx) {
            double radius = (Math.max(Math.max(ay,by),cy) - Math.min(Math.min(ay, by), cy))/2;
            return new Range(new Phones(originX, originY), radius);
        } else if(ay == by && by == cy) {
            double radius = (Math.max(Math.max(ax, bx), cx) - Math.min(Math.min(ax, bx), cx)) / 2;
            return new Range(new Phones(originX, originY), radius);
        } else if(d == 0){
            return null;
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
            return (center.distance(p) < radius * (1-1e-14));
        }
        @Override
        public String toString() {
            return String.format("Range(x=%g, y=%g, radius=%g)", center.x, center.y, radius);
        }
    }


}

final class Phones {
    double x;
    double y;
    //setter
    Phones(double x, double y) {
        this.x = x;
        this.y = y;
    }
    //find the distance between two Phones
    double distance(Phones p) {
        return Math.hypot(x - p.x, y - p.y);
    }
    Phones subtract(Phones p) {
        return new Phones(x - p.x, y - p.y);
    }
    //determinant
    double cross(Phones p) {
        return x * p.y - y * p.x;
    }
    @Override
    public String toString() {
        return String.format("Phones(%g, %g)", x, y);
    }
}
