package tank;

import javafx.geometry.Point2D;

class Physics {
    protected static Point2D decomposeVector(double v, double theta) {
        return new Point2D(Math.cos(theta) * v, Math.sin(theta) * v);
    }

    // Obtained from research. See https://stackoverflow.com/a/2259502/4283659
    // and https://academo.org/demos/rotation-about-point/.
    protected static Point2D rotate(Point2D point, Point2D pivot, double theta) {
        point = point.subtract(pivot);

        double s = Math.sin(theta);
        double c = Math.cos(theta);
        double x = point.getX() * c - point.getY() * s;
        double y = point.getX() * s + point.getY() * c;

        point = new Point2D(x, y);
        return point.add(pivot);
    }

    protected static Double[] pointsToDoubles(Point2D[] points) {
        Double[] doubles = new Double[points.length*2];
        for (int i = 0; i < points.length; i++) {
            int j = i * 2;
            doubles[j] = points[i].getX();
            doubles[j + 1] = points[i].getY();
        }
        return doubles;
    }
}
