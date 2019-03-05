/*
 * Smallest enclosing circle - Library (Java)
 *
 * Copyright (c) 2018 Project Nayuki
 * https://www.nayuki.io/page/smallest-enclosing-circle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (see COPYING.txt and COPYING.LESSER.txt).
 * If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public final class LargestExcludingCircle {
    private static List<String> inputs;

    public static void main(String[] args) {
        try {
            //reads the input file
            readFile();
            //assigns the input file to a list of points
            List<Point> points = new ArrayList<>();
            for (String line : inputs) {
                if (!line.equals("Telephone sites")) {
                    String[] s = line.split(" ");
                    points.add(new Point(Double.parseDouble(s[0]), Double.parseDouble(s[1])));
                }
            }

            //makes the smallest enclosing circle possible
            Circle smallestEnclosing = makeCircle(points);

            //find the largest excluding circle
            Circle largestExcluding = largest(smallestEnclosing, points, false, smallestEnclosing.shrink().grow());
            //print a summary
            System.out.println(String.format("The largest circle that can drawn such that no more than eleven points are" +
                            " within its area is of radius %.2fm.\nThe ideal circle is centred on %.2fm north and %.2fm" +
                            " east of the central point", largestExcluding.r, largestExcluding.c.x, largestExcluding.c.y));

            //lists the excluded points
            List<Point> excluded = new ArrayList<>();
            for(Point individual : points) {
                if(!largestExcluding.contains(individual)) {
                    excluded.add(individual);
                }
            }
            //prints the excluded points
            for (Point a : excluded){
                System.out.println(a + " is not within this circle");
            }

        } catch (IOException e) {
            System.out.println("File not found");
        }
    }

    private static Circle largest(Circle old, List<Point> points, boolean grow, Circle original) {
        Circle newCircle;

        //boolean input determines whether to grow or shrink the circle
        if(grow) newCircle = old.grow();
        else newCircle = old.shrink();

        //checks to see how many points are excluded
        int j = 0;
        for(Point individual : points) {
            if(!newCircle.contains(individual)) {
                j++;
            }
        }
        //if the radius is the original, return a circle of radius 0.01 smaller
        if(newCircle.r == original.r) {
            return newCircle.shrink();
        }
        //else if there are more than one points excluded, then see if you can enlarge the circle and still exclude at least one
        else if(j > 1) {
            return largest(newCircle, points, true, original);
        }
        //if there are no points excluded, then shrink the circle
        else if(j == 0) {
            return largest(newCircle,points, false, original);
        }
        //else the new circle is right
        else return newCircle;
    }

    /*
     * Reads the file coords.txt
     */
    private static void readFile() throws IOException {
        inputs = Files.readAllLines(Paths.get("coords.txt"));
    }

    /*
     * Returns the smallest circle that encloses all the given points. Runs in expected O(n) time, randomized.
     */
    // Initially: No boundary points known
    private static Circle makeCircle(List<Point> points) {
        // Clone list to preserve the input data
        List<Point> localPoints = new ArrayList<>(points);

        // Progressively add points to circle or recompute circle
        Circle c = null;
        for (int i = 0; i < localPoints.size(); i++) {
            Point p = localPoints.get(i);
            if (c == null || !c.contains(p))
                c = makeCircleOnePoint(localPoints.subList(0, i + 1), p);
        }
        return c;
    }


    // One boundary point known
    private static Circle makeCircleOnePoint(List<Point> points, Point p) {
        Circle c = new Circle(p, 0);
        for (int i = 0; i < points.size(); i++) {
            Point q = points.get(i);
            if (!c.contains(q)) {
                if (c.r == 0)
                    c = makeDiameter(p, q);
                else
                    c = makeCircleTwoPoints(points.subList(0, i + 1), p, q);
            }
        }
        return c;
    }


    // Two boundary points known
    private static Circle makeCircleTwoPoints(List<Point> points, Point p, Point q) {
        Circle circ = makeDiameter(p, q);
        Circle left  = null;
        Circle right = null;

        // For each point not in the two-point circle
        Point pq = q.subtract(p);
        for (Point r : points) {
            if (circ.contains(r))
                continue;

            // Form a circumcircle and classify it on left or right side
            double cross = pq.cross(r.subtract(p));
            Circle c = makeCircumcircle(p, q, r);
            if (c != null) {
                if (cross > 0 && (left == null || pq.cross(c.c.subtract(p)) > pq.cross(left.c.subtract(p))))
                    left = c;
                else if (cross < 0 && (right == null || pq.cross(c.c.subtract(p)) < pq.cross(right.c.subtract(p))))
                    right = c;
            }
        }

        // Select which circle to return
        if (left == null && right == null)
            return circ;
        else if (left == null)
            return right;
        else if (right == null)
            return left;
        else
            return left.r <= right.r ? left : right;
    }


    private static Circle makeDiameter(Point a, Point b) {
        Point c = new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
        return new Circle(c, Math.max(c.distance(a), c.distance(b)));
    }


    private static Circle makeCircumcircle(Point a, Point b, Point c) {
        // Mathematical algorithm from Wikipedia: Circumscribed circle
        //"recenter" the three points on the minimum x and y values
        double originx = (Math.min(Math.min(a.x, b.x), c.x) + Math.max(Math.min(a.x, b.x), c.x)) / 2;
        double originy = (Math.min(Math.min(a.y, b.y), c.y) + Math.max(Math.min(a.y, b.y), c.y)) / 2;

        //get the new co-ordinates in relation to this new origin
        double ax = a.x - originx,  ay = a.y - originy;
        double bx = b.x - originx,  by = b.y - originy;
        double cx = c.x - originx,  cy = c.y - originy;

        //find the scale of the triangle
        double d = (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by)) * 2;
        //if the scale is 0, then all three points lie on a line such that ay = by = cy, or ax = bx = cx = 0
        if (d == 0) return null;

        //finds the intersection of the bisectors of the three sides of the triangle - the center of a circumscribed circle
        double x = ((ax*ax + ay*ay) * (by - cy) + (bx*bx + by*by) * (cy - ay) + (cx*cx + cy*cy) * (ay - by)) / d;
        double y = ((ax*ax + ay*ay) * (cx - bx) + (bx*bx + by*by) * (ax - cx) + (cx*cx + cy*cy) * (bx - ax)) / d;

        //adds the origin points back on
        Point p = new Point(originx + x, originy + y);
        //makes the radius the distance from the center to the three points of the triangle
        double r = Math.max(Math.max(p.distance(a), p.distance(b)), p.distance(c));
        return new Circle(p, r);
    }

}



