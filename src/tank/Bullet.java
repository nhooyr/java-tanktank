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
    protected static final Paint COLOR = Color.RED; // Used by Game.
    protected static final double VELOCITY = Tank.VELOCITY * 1.5; // exported for use in Maze.
    private static final long DURATION = TimeUnit.SECONDS.toNanos(15);

    private Point2D velocity;
    private final Circle circle;
    private final long expiry;

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
        // Very fine because it has a big impact when it comes to hitting corners. See source below. Also, you can test
        // this by editing BulletManager to allow for a stream of bullets and then move forward and back as you hit a corner.
        // This should not affect the trajectory of reflected bullets but it does, and it does more if smallVelocity is larger.
        // There are improvements that can be made to this but whatever.
        Point2D smallVelocity = velocity.multiply(-1.0 / 64.0);
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
        // The handling of this was obtained from research.
        // The most important and useful resources I found were:
        // See https://gamedev.stackexchange.com/q/112299
        // and https://gamedev.stackexchange.com/a/10917.
        // The approaches described in both are entirely equivalent but I went with using resource 1's vectors
        // because the incantation is significantly more clear.
        // I am still not sure how exactly the solutions are equivalent but I tested the velocity vectors produced
        // by both and they were in fact always equivalent if not negligently different (like difference of e-15).

        Point2D corner;

        // TODO this could be cleaned up if we used our custom Rectangle class for the Maze.
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

        Point2D center = getCenter();
        // Normal points from the corner to the center of the circle.
        Point2D normal = center.subtract(corner).normalize();
        velocity = velocity.subtract(normal.multiply(velocity.dotProduct(normal)).multiply(2));
    }

    // Used for detecting which cells to check collisions with.
    public Point2D getCenter() {
        return new Point2D(circle.getCenterX(), circle.getCenterY());
    }

}