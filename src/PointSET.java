import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.LinkedBag;

/**
 * The {@code PointSET} class constructs a data type that represents a set of points in the unit square.
 * This implementation uses a red-black BST.
 *
 * @author Jorge Frisancho
 */
public class PointSET {
    private SET<Point2D> set;

    /**
     * Initializes an empty set of points
     */
    public PointSET() {
        set = new SET<Point2D>();
    }

    /**
     * Returns true if this symbol table is empty.
     *
     * @return {@code true} if this symbol table is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * Returns the number of points in this set.
     *
     * @return the number of points in this set
     */
    public int size() {
        return set.size();
    }

    /**
     * Adds the point to the set (if it is not already in the set)
     *
     * @param p a point
     * @throws NullPointerException if {@code p} is {@code null}
     */
    public void insert(Point2D p) {
        if (p == null)
            throw new NullPointerException();
        set.add(p);
    }

    /**
     * Does this set contain the given point?
     *
     * @param p a point
     * @return {@code true} if this set contains {@code p} and
     * {@code false} otherwise
     * @throws NullPointerException if {@code p} is {@code null}
     */
    public boolean contains(Point2D p) {
        if (p == null)
            throw new NullPointerException();
        return set.contains(p);
    }

    /**
     * Draws all points in a set using the standard draw
     */
    public void draw() {
        for (Point2D point : set) {
            point.draw();
        }
    }

    /**
     * Returns all points that are inside a rect as an {@code Iterable}.
     *
     * @param rect a {@code RectHV}
     * @return all points that are inside a {@code rect}
     * @throws NullPointerException if {@code rect} is {@code null}
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new NullPointerException();
        LinkedBag<Point2D> list = new LinkedBag<>();
        for (Point2D point : set) {
            if (rect.distanceSquaredTo(point) == 0)
                list.add(point);
        }
        return list;
    }

    /**
     * Returns a nearest neighbor in the set to a point {@code p}.
     *
     * @param  p a point
     * @return  a nearest neighbor in the set to point {@code p}; {@code null} if the set is empty
     * @throws NullPointerException if {@code rectangle} is {@code null}
     */
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new NullPointerException();
        double min = Double.MAX_VALUE;
        Point2D nearestPoint = null;
        for (Point2D point : set) {
            if (p.distanceSquaredTo(point) < min) {
                min = p.distanceSquaredTo(point);
                nearestPoint = point;
            }
        }
        return nearestPoint;
    }

    /**
     * Unit tests the {@code PointSET} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        String filename = args[0];
        In in = new In(filename);

        StdDraw.enableDoubleBuffering();

        // initialize the data structures with N points from standard input
        PointSET brute = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }

        // draw the points
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        brute.draw();

        // print the point in rectangle
        RectHV rect = new RectHV(0.6, 0, 1, 0.5);
        // draw the range search results for brute-force data structure in red
        StdDraw.setPenRadius(0.03);
        StdDraw.setPenColor(StdDraw.RED);
        for (Point2D point : brute.range(rect))
            point.draw();

        // draw the rectangle
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius();
        rect.draw();

        // print the nearest point
        Point2D point = new Point2D(0.05, 0.95);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setPenRadius(0.01);
        point.draw();
        StdDraw.setPenRadius(0.02);
        StdDraw.setPenColor(StdDraw.CYAN);
        brute.nearest(point).draw();

        StdDraw.show();
        StdDraw.pause(40);
    }
}
