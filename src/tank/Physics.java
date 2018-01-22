package tank;

import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

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

    protected static boolean checkCollision(Shape shape1, Shape shape2) {
        // This is incredibly inefficient but doing it more efficiently involves a lot of math and for this game
        // it is not worth the effort. Just do not run this on a complete toaster.
        // We do not use javafx's bounds as described at https://docs.oracle.com/javase/8/javafx/api/javafx/geometry/Bounds.html
        // because when a shape is rotated, the bounding box does not rotate, instead it is formed the min/max x/y values which
        // means collision detection becomes highly inaccurate.
        //
        // If in the future, this becomes a bottleneck, use the method described in
        // https://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection.
        Shape intersection = Shape.intersect(shape1, shape2);
        return intersection.getBoundsInLocal().getWidth() > 0;
        // This is the inaccurate method that javafx provides.
//        return shape1.getBoundsInParent().intersects(shape2.getBoundsInParent());
    }
}
