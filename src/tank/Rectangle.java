package tank;

import javafx.geometry.Point2D;

class Rectangle {
    private Point2D[] points = new Point2D[4];
    private Point2D origin = Point2D.ZERO;
    private double width;
    private double height;

    protected double getWidth() {
        return width;
    }

    protected double getHeight() {
        return height;
    }

    protected Rectangle(Rectangle rect) {
        this.points = rect.points.clone();
        this.origin = rect.origin.add(Point2D.ZERO);
        this.width = width;
        this.height = height;
    }

    protected Rectangle(double width, double height) {
        points.clone();
        this.width = width;
        this.height = height;
        points[0] = new Point2D(0, 0);
        points[1] = new Point2D(width, 0);
        points[2] = new Point2D(width, height);
        points[3] = new Point2D(0, height);
    }

    protected void moveBy(Point2D p) {
        for (int i = 0; i < points.length; i++) {
            points[i] = points[i].add(p);
        }
        origin.add(p);
    }

    protected void moveTo(Point2D p) {
        Point2D dif = p.subtract(origin);
        moveBy(dif);
    }

    protected void rotate(Point2D pivot, double theta) {
        for (int i = 0; i < points.length; i++) {
            points[i] = Physics.rotate(points[i], pivot, theta);
        }
    }

    protected Double[] getDoubles() {
        Double[] doubles = new Double[points.length * 2];
        for (int i = 0; i < points.length; i++) {
            int j = i * 2;
            doubles[j] = points[i].getX();
            doubles[j + 1] = points[i].getY();
        }
        return doubles;
    }

    // Needed for figuring out the position in which to launch the bullet.
    protected Point2D getMidRight() {
        Point2D topRight = points[1];
        Point2D bottomRight = points[2];
        return topRight.midpoint(bottomRight);
    }
}