package tank;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

class Bullet {
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

    protected void horizontalBounce() {
        double theta = -3 * Math.PI / 2 + (Math.PI / 2 + this.theta);
        setTheta(theta);
    }

    protected void verticalBounce() {
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
    protected CollisionStatus checkCollision(Rectangle rect) {
        if (!rect.getBoundsInParent().intersects(circle.getBoundsInParent())) {
            // The bullet does not intersect the rect.
            return CollisionStatus.OK;
        }

        // Maybe increase the velocity if this is too computationally expensive. Does not seem like that so far though.
        // So whatever.
        Point2D decomposedVelocity = Physics.decomposeVector(-0.1, theta);

        do {
            moveBy(decomposedVelocity);
        } while (rect.getBoundsInParent().intersects(circle.getBoundsInParent()));


        double x = circle.getCenterX();
        double y = circle.getCenterY();
        double radius = circle.getRadius();

        double h1 = Math.abs(rect.getX() - (x + radius));
        double h2 = Math.abs((x - radius) - (rect.getX() + rect.getWidth()));
        double h = h1 < h2 ? h1 : h2;

        double v1 = Math.abs((rect.getY() - (y + radius)));
        double v2 = Math.abs((y - radius) - (rect.getY() + rect.getHeight()));
        double v = v1 < v2 ? v1 : v2;

        if (v < h) {
            return CollisionStatus.HORIZONTAL;
        }
        return CollisionStatus.VERTICAL;
    }
}