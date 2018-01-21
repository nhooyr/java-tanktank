package tank;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

class Bullet implements Maze.CollisionHandler {
    private static final double RADIUS = Tank.HEAD_HEIGHT / 2;
    private static final Paint COLOR = Color.RED;
    protected static final double VELOCITY = Tank.VELOCITY * 1.5; // exported for use in Maze.
    private Point2D decomposedVelocity;
    private Circle circle;
    private double theta;

    protected Bullet(Group group, Point2D launchPoint, double theta) {
        Point2D radiusForward = Physics.decomposeVector(Bullet.RADIUS, theta);
        launchPoint = launchPoint.add(radiusForward);
        circle = new Circle(launchPoint.getX(), launchPoint.getY(), RADIUS, COLOR);
        setTheta(theta);
        group.getChildren().add(circle);
    }

    private void setTheta(double theta) {
        this.theta = theta;
        decomposedVelocity = Physics.decomposeVector(VELOCITY, theta);
    }

    private void horizontalBounce() {
        double theta = -this.theta;
        setTheta(theta);
    }

    private void verticalBounce() {
        double theta = -Math.PI - this.theta;
        setTheta(theta);
    }

    private void moveBy(Point2D velocity) {
        circle.setCenterX(circle.getCenterX() + velocity.getX());
        circle.setCenterY(circle.getCenterY() + velocity.getY());
    }

    protected void update() {
        moveBy(decomposedVelocity);
    }

    // The way this works is that first we check if the rectangle is intersecting with the bullet. If so,
    // then we need to figure out which side the bullet is on. So we move the bullet back until there is no
    // collision. Then we check which side is closest to the bullet and based on that return the appropriate
    // collision status.
    public void handleCollision(ArrayList<Rectangle> sides) {
        for (int i = 0; i < sides.size(); i++) {
            if (!Physics.checkCollision(circle, sides.get(i))) {
                // The bullet does not intersect the side.
                sides.remove(i);
                i--;
            }
        }

        if (sides.size() == 0) {
            // The bullet does not intersect any of the sides.
            return;
        }

        // side will hold the final side the object ended up colliding with, aka the first collision.
        Rectangle side = null;
        // Backtrack.
        Point2D decomposedVelocity = Physics.decomposeVector(-1, theta);
        do {
            moveBy(decomposedVelocity);

            for (int i = 0; i < sides.size(); i++) {
                if (!Physics.checkCollision(circle, sides.get(i))) {
                    side = sides.remove(i);
                    i--;
                }
            }
        } while (sides.size() > 0);

        double x = circle.getCenterX();
        double y = circle.getCenterY();
        double radius = circle.getRadius();

        double h1 = Math.abs(side.getX() - (x + radius));
        double h2 = Math.abs((x - radius) - (side.getX() + side.getWidth()));
        double h = h1 < h2 ? h1 : h2;

        double v1 = Math.abs((side.getY() - (y + radius)));
        double v2 = Math.abs((y - radius) - (side.getY() + side.getHeight()));
        double v = v1 < v2 ? v1 : v2;

        if (v < h) {
            horizontalBounce();
            return;
        }
        verticalBounce();
    }

    // Used for detecting which cells to check collisions with.
    public Point2D getCenter() {
        return new Point2D(circle.getCenterX(), circle.getCenterY());
    }
}