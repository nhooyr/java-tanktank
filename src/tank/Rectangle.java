package tank;

import javafx.geometry.Point2D;

class Rectangle {
    private final double width;
    private final double height;
    private Point2D[] points = new Point2D[4];
    private Point2D origin = Point2D.ZERO;

    Rectangle(final Rectangle rect) {
        this.points = rect.points.clone();
        this.origin = rect.origin.add(Point2D.ZERO);
        this.width = rect.width;
        this.height = rect.height;
    }

    Rectangle(final double width, final double height) {
        points.clone();
        this.width = width;
        this.height = height;
        points[0] = new Point2D(0, 0);
        points[1] = new Point2D(width, 0);
        points[2] = new Point2D(width, height);
        points[3] = new Point2D(0, height);
    }

    double getWidth() {
        return width;
    }

    double getHeight() {
        return height;
    }

    void moveBy(final Point2D p) {
        for (int i = 0; i < points.length; i++) {
            points[i] = points[i].add(p);
        }
        origin.add(p);
    }

    void moveTo(final Point2D p) {
        final Point2D dif = p.subtract(origin);
        moveBy(dif);
    }

    void rotate(final Point2D pivot, final double theta) {
        for (int i = 0; i < points.length; i++) {
            points[i] = Physics.rotate(points[i], pivot, theta);
        }
    }

    Double[] getDoubles() {
        final Double[] doubles = new Double[points.length * 2];
        for (int i = 0; i < points.length; i++) {
            final int j = i * 2;
            doubles[j] = points[i].getX();
            doubles[j + 1] = points[i].getY();
        }
        return doubles;
    }

    // Needed for figuring out the position in which to launch the bullet.
    Point2D getMidRight() {
        final Point2D topRight = points[1];
        final Point2D bottomRight = points[2];
        return topRight.midpoint(bottomRight);
    }
}