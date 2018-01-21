package tank;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

// TODO when spawning always make tanks face away from each other.
class Tank {
    protected final static int VELOCITY = 3; // exported for use in Bullet.
    private final static double TURNING_ANGLE = Math.PI / 36;
    private final static Color COLOR = Color.BLUE;

    private final static int BODY_WIDTH = 40;
    protected final static int BODY_HEIGHT = 30; // exported for use in Maze.

    private final static int HEAD_WIDTH = BODY_WIDTH / 2;
    protected final static int HEAD_HEIGHT = BODY_HEIGHT / 4; // exported for use in Bullet.

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
        moveBy(new Point2D(Maze.THICKNESS, Maze.THICKNESS));

        // head needs to be added after so that it is in front. In case we want to change colors later.
        root.getChildren().addAll(bodyPolygon, headPolygon);
        syncPolygons();
    }

    // The direction of angles is reversed because the coordinate system is reversed.
    protected void right() {
        rotate(TURNING_ANGLE);
    }

    protected void left() {
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
        moveBy(decomposedVelocity);
    }

    protected void back() {
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
}
