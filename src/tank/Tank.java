package tank;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

// TODO when spawning always make tanks face away from each other.
class Tank {
    static final int VELOCITY = 3; // exported for use in Bullet.
    static final double BODY_HEIGHT = 30; // exported for use in Cell.
    static final double HEAD_HEIGHT = BODY_HEIGHT / 4; // exported for use in Bullet.

    static final HashMap<KeyCode, Op> KEY_CODES_1 = new HashMap<>();
    static final HashMap<KeyCode, Op> KEY_CODES_2 = new HashMap<>();

    private static final double TURNING_ANGLE = Math.PI / 36;
    private static final double BODY_WIDTH = 40;
    private static final double HEAD_WIDTH = BODY_WIDTH / 2;
    private static final Color DEAD_COLOR = Color.BLACK;

    static {
        KEY_CODES_1.put(KeyCode.UP, Op.FORWARD);
        KEY_CODES_1.put(KeyCode.RIGHT, Op.RIGHT);
        KEY_CODES_1.put(KeyCode.DOWN, Op.REVERSE);
        KEY_CODES_1.put(KeyCode.LEFT, Op.LEFT);
        KEY_CODES_1.put(KeyCode.PERIOD, Op.FIRE);
    }

    static {
        KEY_CODES_2.put(KeyCode.W, Op.FORWARD);
        KEY_CODES_2.put(KeyCode.D, Op.RIGHT);
        KEY_CODES_2.put(KeyCode.S, Op.REVERSE);
        KEY_CODES_2.put(KeyCode.A, Op.LEFT);
        KEY_CODES_2.put(KeyCode.V, Op.FIRE);
    }

    private final Color headColor;
    private final Color outOfAmmoHeadColor;
    private final String mainColorName;
    private final Rectangle head = new Rectangle(HEAD_WIDTH, HEAD_HEIGHT);
    private final Rectangle body = new Rectangle(BODY_WIDTH, BODY_HEIGHT);
    private final BulletManager bulletManager;
    private final Maze maze;
    private final HashMap<KeyCode, Op> keycodes;
    // Keys pressed since the last frame.
    private final HashSet<Op> pressedOps = new HashSet<>();
    private Shape shape;
    // Middle of body.
    private Point2D pivot = new Point2D(body.getWidth() / 2, body.getHeight() / 2);
    private double theta;
    private Point2D decomposedVelocity;
    private Point2D negativeDecomposedVelocity;
    private Op lastMovementOp;
    private boolean dead;

    Tank(final String mainColorName, final Color bodyColor, final Color headColor, final Color outOfAmmoColor, final Maze maze, final HashMap<KeyCode, Op> keycodes, final double initialAngle) {
        this.maze = maze;
        this.keycodes = keycodes;
        this.mainColorName = mainColorName;

        bulletManager = new BulletManager(maze);

        final Point2D headPoint = new Point2D(
                body.getWidth() - head.getWidth() / 2, // half of head sticks out.
                body.getHeight() / 2 - head.getHeight() / 2 // head is in the vertical middle of tank.
        );
        head.moveTo(headPoint);

        this.headColor = headColor;
        this.outOfAmmoHeadColor = outOfAmmoColor;
        head.getPolygon().setFill(this.headColor);
        body.getPolygon().setFill(bodyColor);

        rotate(initialAngle);

        // Move to the middle of some random cell.
        final Random rand = new Random();
        final int col = rand.nextInt(Maze.COLUMNS);
        final int row = rand.nextInt(Maze.ROWS);
        moveBy(new Point2D(col * Cell.LENGTH, row * Cell.LENGTH));
        moveBy(new Point2D(Maze.THICKNESS, Maze.THICKNESS));
        moveBy(new Point2D((Cell.LENGTH - Maze.THICKNESS) / 2, (Cell.LENGTH - Maze.THICKNESS) / 2));
        moveBy(new Point2D(-body.getWidth() / 2, -body.getHeight() / 2));

        syncPolygons();
    }

    BulletManager getBulletManager() {
        return bulletManager;
    }

    Node getNode() {
        // head added after so that you can see it in front.
        return new Group(body.getPolygon(), head.getPolygon());
    }

    Node getNodeFacingRight() {
        final Polygon headPolygonCopy = new Polygon();
        final Polygon bodyPolygonCopy = new Polygon();

        final Rectangle headCopy = new Rectangle(head);
        final Rectangle bodyCopy = new Rectangle(body);

        // TODO should the tank be pointing out or into the alert ¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿ need more thought.
        headCopy.rotate(pivot, -theta + Math.PI);
        bodyCopy.rotate(pivot, -theta + Math.PI);

        return new Group(bodyPolygonCopy, headPolygonCopy);
    }

    // The direction of angles is reversed because the coordinate system is reversed.
    private void right() {
        lastMovementOp = Op.RIGHT;
        rotate(TURNING_ANGLE);
    }

    private void left() {
        lastMovementOp = Op.LEFT;
        rotate(-TURNING_ANGLE);
    }

    private void rotate(final double theta) {
        this.theta += theta;
        body.rotate(pivot, theta);
        head.rotate(pivot, theta);
        decomposedVelocity = Physics.decomposeVector(VELOCITY, this.theta);
        negativeDecomposedVelocity = Physics.decomposeVector(-VELOCITY, this.theta);
        syncPolygons();
    }

    private void syncPolygons() {
        shape = Shape.union(head.getPolygon(), body.getPolygon());
    }

    private void forward() {
        lastMovementOp = Op.FORWARD;
        moveBy(decomposedVelocity);
    }

    private void back() {
        lastMovementOp = Op.REVERSE;
        moveBy(negativeDecomposedVelocity);
    }

    private void moveBy(final Point2D point) {
        head.moveBy(point);
        body.moveBy(point);
        pivot = pivot.add(point);
        syncPolygons();
    }

    private Point2D getBulletLaunchPoint() {
        final Point2D topRight = head.getTopRight();
        final Point2D bottomRight = head.getBotRight();
        return topRight.midpoint(bottomRight);
    }

    private double getTheta() {
        return theta;
    }

    private Point2D getCenter() {
        return pivot;
    }

    Shape getShape() {
        return shape;
    }

    private boolean checkCollision(final Shape shape) {
        return Physics.checkCollision(getShape(), shape);
    }

    // TODO add edge mechanics, e.g. instead of just stopping the tank, we lower velocity/slide.
    private void handleMazeCollisions() {
        final ArrayList<Rectangle> segs = maze.getCollisionCandidates(getCenter());

        for (int i = 0; i < segs.size(); i++) {
            if (!checkCollision(segs.get(i).getPolygon())) {
                // The tank does not intersect the seg.
                segs.remove(i);
                i--;
            }
        }

        if (segs.size() == 0) {
            // The tank does not intersect any of the segs.
            return;
        }

        Runnable reverseOp = null;
        // Backtrack.
        final Tank tank = this;
        final Point2D decomposedVelocity;
        switch (lastMovementOp) {
            case FORWARD:
                decomposedVelocity = Physics.decomposeVector(-1, theta);
                reverseOp = () -> tank.moveBy(decomposedVelocity);
                break;
            case REVERSE:
                decomposedVelocity = Physics.decomposeVector(1, theta);
                reverseOp = () -> tank.moveBy(decomposedVelocity);
                break;
            case RIGHT:
                reverseOp = () -> tank.rotate(-TURNING_ANGLE / 12);
                break;
            case LEFT:
                reverseOp = () -> tank.rotate(TURNING_ANGLE / 12);
                break;
        }
        do {
            assert reverseOp != null;
            reverseOp.run();
            syncPolygons();

            for (int i = 0; i < segs.size(); i++) {
                if (!checkCollision(segs.get(i).getPolygon())) {
                    segs.remove(i);
                    i--;
                }
            }
        } while (segs.size() > 0);
    }

    void handlePressed(final KeyCode keyCode) {
        pressedOps.add(keycodes.get(keyCode));
    }

    void handleReleased(final KeyCode keyCode) {
        final Op op = keycodes.get(keyCode);
        if (op == Op.FIRE) {
            bulletManager.lock = false;
        }
        pressedOps.remove(op);
    }

    void handle(final long nanos) {
        bulletManager.update(nanos);
        if (pressedOps.contains(Op.FIRE)) {
            bulletManager.addBullet(
                    getBulletLaunchPoint(),
                    getTheta(),
                    nanos
            );
            bulletManager.lock = true;
        }
        bulletManager.handleMazeCollisions();

        if (bulletManager.isReloading()) {
            head.getPolygon().setFill(outOfAmmoHeadColor);
        } else {
            head.getPolygon().setFill(headColor);
        }

        if (pressedOps.contains(Op.RIGHT)) {
            right();
        }
        if (pressedOps.contains(Op.LEFT)) {
            left();
        }
        handleMazeCollisions();

        if (pressedOps.contains(Op.FORWARD)) {
            forward();
        }
        if (pressedOps.contains(Op.REVERSE)) {
            back();
        }
        handleMazeCollisions();
    }

    void kill() {
        dead = true;
        head.getPolygon().setFill(DEAD_COLOR);
        body.getPolygon().setFill(DEAD_COLOR);
    }

    boolean isDead() {
        return dead;
    }

    String getMainColorName() {
        return mainColorName;
    }

    private enum Op {
        FORWARD,
        RIGHT,
        LEFT,
        REVERSE,
        FIRE,
    }
}
