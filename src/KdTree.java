import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.LinkedBag;

/**
 * The {@code KdTree} class constructs a generalization of a BST to two-dimensional keys. The idea is to build
 * a BST with points in the nodes, using the x- and y-coordinates of the points as keys
 * in strictly alternating sequence.
 *
 * @author Jorge Frisancho
 */
public class KdTree {

    private int size;
    private Node root;
    private LinkedBag<Point2D> list;
    private double minDistance;
    private Point2D nearestPoint = null;
    private Point2D target = null;

    public KdTree() {
        root = null;
        size = 0;
        list = null;
    }

    /**
     * Returns true if this symbol table is empty.
     *
     * @return {@code true} if this symbol table is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of points in this set.
     *
     * @return the number of points in this set
     */
    public int size() {
        return size;
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
        root = insert(root, p, 0.0, 0.0, 1.0, 1.0, true);
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
        return find(root, p, true);
    }

    /**
     * Draws all points in a set using the standard draw
     */
    public void draw() {
        drawhelper(root);
    }

    /**
     * Returns all points that are inside a rectangle as an {@code Iterable}.
     *
     * @param rect a {@code RectHV}
     * @return all points that are inside a {@code rect}
     * @throws NullPointerException if {@code rect} is {@code null}
     */
    public Iterable<Point2D> range(RectHV rect) {
        list = new LinkedBag<>();
        searchRange(root, rect);
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
        minDistance = Double.MAX_VALUE;
        target = p;
        searchNearest(root);
        return nearestPoint;
    }



    // helper function for insert
    private Node insert(Node node, Point2D p, double xmin, double ymin, double xmax, double ymax, boolean isVer) {
        if (node == null) {
            size++;
            RectHV r = new RectHV(xmin, ymin, xmax, ymax);
            return new Node(p, r, isVer);
        }

        if (node.point().equals(p))
            return node;

        if (isVer) {
            if (p.x() < node.point().x()) {
                node.lb = insert(node.lb, p, xmin, ymin, node.point().x(), ymax, false);
            } else {
                node.rt = insert(node.rt, p, node.point().x(), ymin, xmax, ymax, false);
            }
        } else {
            if (p.y() < node.point().y()) {
                node.lb = insert(node.lb, p, xmin, ymin, xmax, node.point().y(), true);
            } else {
                node.rt = insert(node.rt, p, xmin, node.point().y(), xmax, ymax, true);
            }
        }

        return node;
    }


    // helper function for contains
    private boolean find(Node node, Point2D p, boolean isVer) {
        if (node == null)
            return false;
        if (node.point().equals(p))
            return true;
        if (isVer) {
            if (p.x() < node.point().x()) {
                return find(node.lb, p, false);
            } else {
                return find(node.rt, p, false);
            }
        } else {
            if (p.y() < node.point().y()) {
                return find(node.lb, p, true);
            } else {
                return find(node.rt, p, true);
            }
        }
    }

    // helper function for draw
    private void drawhelper(Node node) {
        if (node == null)
            return;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.point().draw();

        StdDraw.setPenRadius();
        if (node.isVertical()) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.point().x(), node.rect.ymin(), node.point().x(), node.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect.xmin(), node.point().y(), node.rect.xmax(), node.point().y());
        }
        drawhelper(node.lb);
        drawhelper(node.rt);
    }

    // helper function for range
    private void searchRange(Node node, RectHV rect) {
        if (node == null)
            return;
        if (rect.contains(node.point()))
            list.add(node.point());
        if (node.isVertical()) {
            if (node.point().x() >= rect.xmin()) {
                searchRange(node.lb, rect);
            }
            if (node.point().x() <= rect.xmax()) {
                searchRange(node.rt, rect);
            }
        } else {
            if (node.point().y() >= rect.ymin()) {
                searchRange(node.lb, rect);
            }
            if (node.point().y() <= rect.ymax()) {
                searchRange(node.rt, rect);
            }
        }
    }

    // helper function for nearest
    private void searchNearest(Node node) {
        if (node == null)
            return;
        if (node.point().distanceSquaredTo(target) < minDistance) {
            minDistance = node.point().distanceSquaredTo(target);
            nearestPoint = node.point();
        }

        if (node.isVertical()) {
            if (target.x() < node.point().x()) {
                searchNearest(node.lb);
                if (distanceSquared(node) < minDistance) {
                    searchNearest(node.rt);
                }
            } else {
                searchNearest(node.rt);
                if (distanceSquared(node) < minDistance) {
                    searchNearest(node.lb);
                }
            }
        } else {
            if (target.y() < node.point().y()) {
                searchNearest(node.lb);
                if (distanceSquared(node) < minDistance) {
                    searchNearest(node.rt);
                }
            } else {
                searchNearest(node.rt);
                if (distanceSquared(node) < minDistance) {
                    searchNearest(node.lb);
                }
            }
        }
    }

    private double distanceSquared(Node node) {
        double dx = 0.0, dy = 0.0;
        if (node.isVertical()) {
            if      (target.y() < node.rect.ymin()) dy = target.y() - node.rect.ymin();
            else if (target.y() > node.rect.ymax()) dy = target.y() - node.rect.ymax();
            dx = target.x() - node.point().x();
        } else {
            if      (target.x() < node.rect.xmin()) dx = target.x() - node.rect.xmin();
            else if (target.x() > node.rect.xmax()) dx = target.x() - node.rect.xmax();
            dy = target.y() - node.point().y();
        }

        return dx*dx + dy*dy;
    }


    /**
     * The {@code Node} class constructs a node that includes the point, a link to the left/bottom subtree,
     * a link to the right/top subtree, and an axis-aligned rectangle corresponding to the node.
     *
     * @author Jorge Frisancho
     */
    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the lb/bottom subtree
        private Node rt;        // the rt/top subtree
        private boolean isVertical;

        private Node(Point2D point, RectHV rect, boolean isVertical) {
            this.p = point;
            this.rect = rect;
            this.isVertical = isVertical;
        }

        private Point2D point() {
            return p;
        }

        private boolean isVertical() {
            return isVertical;
        }
    }

    /**
     * Unit tests the {@code KdTree} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        KdTree tree = new KdTree();

        // test for insert and contains
        Point2D p1 = new Point2D(0.7, 0.2);
        tree.insert(p1);
        StdOut.println(tree.contains(p1));
        StdOut.println(tree.root.point());
        StdOut.println(tree.root.isVertical());
        StdOut.println(tree.size());
        StdOut.println("------------------");

        Point2D p2 = new Point2D(0.5, 0.4);
        tree.insert(p2);
        StdOut.println(tree.contains(p2));
        StdOut.println(tree.root.lb.point());
        StdOut.println(tree.root.lb.isVertical());
        StdOut.println(tree.size());
        StdOut.println("------------------");

        Point2D p3 = new Point2D(0.2, 0.3);
        tree.insert(p3);
        StdOut.println(tree.contains(p3));
        StdOut.println(tree.root.lb.lb.point());
        StdOut.println(tree.root.lb.lb.isVertical());
        StdOut.println(tree.size());
        StdOut.println("------------------");

        Point2D p4 = new Point2D(0.4, 0.7);
        tree.insert(p4);
        StdOut.println(tree.contains(p4));
        StdOut.println(tree.root.lb.rt.point());
        StdOut.println(tree.root.lb.rt.isVertical());
        StdOut.println("------------------");

        Point2D p5 = new Point2D(0.9, 0.6);
        tree.insert(p5);
        StdOut.println(tree.contains(p5));
        StdOut.println(tree.root.rt.point());
        StdOut.println(tree.root.rt.isVertical());
        StdOut.println("------------------");

        Point2D p6 = new Point2D(0.9, 0.7);
        StdOut.println(tree.contains(p6));
        StdOut.println("------------------");

        // test for range search
        StdOut.println("*************************");
        for (Point2D point : tree.range(new RectHV(0.69, 0, 1, 0.21))) {
            StdOut.println(point);  // (0.7, 0.2)
        }

        StdOut.println("*************************");
        for (Point2D point : tree.range(new RectHV(0, 0, 1.0, 1.0))) {
            StdOut.println(point);  // all
        }

        StdOut.println("*************************");
        for (Point2D point : tree.range(new RectHV(0.39, 0.39, 0.51, 0.71))) {
            StdOut.println(point);  // (0.4, 0.7), (0.5, 0.4)
        }
    }
}
