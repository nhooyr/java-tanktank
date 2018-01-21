package tank;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class Bullet implements Maze.CollisionHandler {
    private static final double RADIUS = Tank.HEAD_HEIGHT / 2;
    private static final Paint COLOR = Color.RED;
    protected static final double VELOCITY = Tank.VELOCITY * 1.5; // exported for use in Maze.
    private static final long DURATION = TimeUnit.SECONDS.toNanos(5);

    private Point2D velocity;
    private Circle circle;
    private long expiry;

    protected Bullet(Point2D launchPoint, double theta, long nanos) {
        // We add velocity so the Tank does not instantly die from its own bullet.
        Point2D radiusForward = Physics.decomposeVector(RADIUS + VELOCITY, theta);
        launchPoint = launchPoint.add(radiusForward);

        circle = new Circle(launchPoint.getX(), launchPoint.getY(), RADIUS, COLOR);
        velocity = Physics.decomposeVector(VELOCITY, theta);

        expiry = nanos + DURATION;
    }

    private void horizontalBounce() {
        velocity = new Point2D(velocity.getX(), -velocity.getY());
    }

    private void verticalBounce() {
        velocity = new Point2D(-velocity.getX(), velocity.getY());
    }

    private void moveBy(Point2D velocity) {
        circle.setCenterX(circle.getCenterX() + velocity.getX());
        circle.setCenterY(circle.getCenterY() + velocity.getY());
    }

    protected void update() {
        moveBy(velocity);
    }

    protected long getExpiry() {
        return expiry;
    }

    protected Shape getShape() {
        return circle;
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
        Point2D smallVelocity = velocity.multiply(-1.0 / 8.0);
        do {
            moveBy(smallVelocity);

            for (int i = 0; i < sides.size(); i++) {
                if (!Physics.checkCollision(circle, sides.get(i))) {
                    side = sides.remove(i);
                    i--;
                }
            }
        } while (sides.size() > 0);

        double x = circle.getCenterX();
        double y = circle.getCenterY();

        if (x >= side.getX() && x <= side.getX() + side.getWidth()) {
            horizontalBounce();
            return;
        } else if (y >= side.getY() && y <= side.getY() + side.getHeight()) {
            verticalBounce();
            return;
        }

        // This means the bullet collided with a corner.

        Point2D corner;

        // TODO this could be cleaned up if we used the Rectangle class only.
        if (x < side.getX() && y < side.getY()) {
            // topLeft.
            corner = new Point2D(side.getX(), side.getY());
        } else if (x < side.getX() && y > side.getY() + side.getHeight()) {
            // bottomLeft.
            corner = new Point2D(side.getX(), side.getY() + side.getHeight());
        } else if (x > side.getX() + side.getWidth() && y < side.getY()) {
            // topRight.
            corner = new Point2D(side.getX() + side.getWidth(), side.getY());
        } else {
            // bottomRight
            corner = new Point2D(side.getX() + side.getWidth(), side.getY() + side.getHeight());
        }

        // Magic. Researched to figure this out. Mostly obtained from https://gamedev.stackexchange.com/questions/10911/a-ball-hits-the-corner-where-will-it-deflect.
        Point2D center = getCenter();
        Point2D diff = center.subtract(corner);
        double c = -2 * (velocity.getX() * diff.getX() + velocity.getY() * diff.getY()) / (diff.getX() * diff.getX() + diff.getY() * diff.getY());
        Point2D vectorDelta = new Point2D(c * diff.getX(), c * diff.getY());
        velocity = velocity.add(vectorDelta);
    }

    // Used for detecting which cells to check collisions with.
    public Point2D getCenter() {
        return new Point2D(circle.getCenterX(), circle.getCenterY());
    }

}