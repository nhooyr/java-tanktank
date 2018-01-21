package tank;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;

// TODO when spawning always make tanks face away from each other.
class Tank implements Maze.CollisionHandler {
    protected final static int VELOCITY = 3; // exported for use in Bullet.
    private final static double TURNING_ANGLE = Math.PI / 36;
    private final static Color COLOR = Color.BLUE;

    private final static double BODY_WIDTH = 40;
    protected final static double BODY_HEIGHT = 30; // exported for use in Maze.

    private final static double HEAD_WIDTH = BODY_WIDTH / 2;
    protected final static double HEAD_HEIGHT = BODY_HEIGHT / 4; // exported for use in Bullet.

    private Polygon headPolygon;
    private Polygon bodyPolygon;
    private Rectangle head;
    private Rectangle body;
    private Point2D pivot;
    private double theta;
    private Point2D decomposedVelocity;
    private Point2D negativeDecomposedVelocity;

    protected Tank(Group root) {
        body = new Rectangle(BODY_WIDTH, BODY_HEIGHT);
        head = new Rectangle(HEAD_WIDTH, HEAD_HEIGHT);
        Point2D headPoint = new Point2D(
                body.getWidth() - head.getWidth() / 2, // half of head sticks out.
                body.getHeight() / 2 - head.getHeight() / 2 // head is in the vertical middle of tank.
        );
        head.moveTo(headPoint);

        bodyPolygon = new Polygon();
        headPolygon = new Polygon();
        headPolygon.setFill(COLOR);
        bodyPolygon.setFill(COLOR);

        // Middle of body.
        pivot = new Point2D(body.getWidth() / 2, body.getHeight() / 2);

        // Will set [negative]decomposedVelocity and initial angle.
        // We can expose the initial angle in the constructor later for creating two tanks that do not face each other.
        rotate(0);
        moveBy(new Point2D(Maze.THICKNESS+50, Maze.THICKNESS+50));

        // head needs to be added after so that it is in front. In case we want to change colors later.
        root.getChildren().addAll(bodyPolygon, headPolygon);
        syncPolygons();
    }

    // The direction of angles is reversed because the coordinate system is reversed.
    protected void right() {
        lastOp = Op.RIGHT;
        rotate(TURNING_ANGLE);
    }

    protected void left() {
        lastOp = Op.LEFT;
        rotate(-TURNING_ANGLE);
    }

    protected void rotate(double theta) {
        this.theta += theta;
        body.rotate(pivot, theta);
        head.rotate(pivot, theta);
        decomposedVelocity = Physics.decomposeVector(VELOCITY, this.theta);
        negativeDecomposedVelocity = Physics.decomposeVector(-VELOCITY, this.theta);
    }

    protected void syncPolygons() {
        headPolygon.getPoints().setAll(head.getDoubles());
        bodyPolygon.getPoints().setAll(body.getDoubles());
    }

    protected void forward() {
        lastOp = Op.FORWARD;
        moveBy(decomposedVelocity);
    }

    protected void back() {
        lastOp = Op.REVERSE;
        moveBy(negativeDecomposedVelocity);
    }

    private void moveBy(Point2D point) {
        head.moveBy(point);
        body.moveBy(point);
        pivot = pivot.add(point);
    }

    protected Point2D getBulletLaunchPoint() {
        return head.getMidRight();
    }

    protected double getTheta() {
        return theta;
    }

    public Point2D getCenter() {
        return pivot;
    }

    private boolean checkCollision(javafx.scene.shape.Rectangle side) {
        return Physics.checkCollision(headPolygon, side) || Physics.checkCollision(bodyPolygon, side);
    }

    public void handleCollision(ArrayList<javafx.scene.shape.Rectangle> sides) {
        for (int i = 0; i < sides.size(); i++) {
            if (!checkCollision(sides.get(i))) {
                // The tank does not intersect the side.
                sides.remove(i);
                i--;
            }
        }

        if (sides.size() == 0) {
            // The tank does not intersect any of the sides.
            return;
        }

        Runnable reverseOp = null;
        // Backtrack.
        final Tank tank = this;
        final Point2D decomposedVelocity;
        switch (lastOp) {
            case FORWARD:
                decomposedVelocity = Physics.decomposeVector(-1, theta);
                reverseOp = () -> {
                    tank.moveBy(decomposedVelocity);
                };
                break;
            case REVERSE:
                decomposedVelocity = Physics.decomposeVector(1, theta);
                reverseOp = () -> {
                    tank.moveBy(decomposedVelocity);
                };
                break;
            case RIGHT:
                reverseOp = () -> {
                    tank.rotate(-TURNING_ANGLE/12);
                };
                break;
            case LEFT:
                reverseOp = () -> {
                    tank.rotate(TURNING_ANGLE/12);
                };
                break;
        }
        do {
            reverseOp.run();
            syncPolygons();

            for (int i = 0; i < sides.size(); i++) {
                if (!checkCollision(sides.get(i))) {
                    sides.remove(i);
                    i--;
                }
            }
        } while (sides.size() > 0);
    }

    private Op lastOp;

    private enum Op {
        FORWARD,
        RIGHT,
        LEFT,
        REVERSE,
    }
}
