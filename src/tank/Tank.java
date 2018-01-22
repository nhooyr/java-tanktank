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
class Tank implements Maze.CollisionHandler {
    protected final static int VELOCITY = 3; // exported for use in Bullet.
    private final static double TURNING_ANGLE = Math.PI / 36;

    private final static double BODY_WIDTH = 40;
    protected final static double BODY_HEIGHT = 30; // exported for use in Maze.

    private final static double HEAD_WIDTH = BODY_WIDTH / 2;
    protected final static double HEAD_HEIGHT = BODY_HEIGHT / 4; // exported for use in Bullet.

    private final Color HEAD_COLOR;
    private final Color BODY_COLOR;
    private final Color OUT_OF_AMNO_HEAD_COLOR;
    private final static Color DEAD_COLOR = Color.BLACK;
    private final String COLOR_NAME;

    private Polygon headPolygon = new Polygon();
    private Polygon bodyPolygon = new Polygon();
    private Rectangle head = new Rectangle(HEAD_WIDTH, HEAD_HEIGHT);
    private Rectangle body = new Rectangle(BODY_WIDTH, BODY_HEIGHT);
    // Middle of body.
    private Point2D pivot = new Point2D(body.getWidth() / 2, body.getHeight() / 2);
    private double theta;
    private Point2D decomposedVelocity;
    private Point2D negativeDecomposedVelocity;
    private BulletManager bulletManager;
    private Maze maze;

    protected Tank(String colorName, Color bodyColor, Color headColor, Color outOfAmnoColor, Maze maze, HashMap<KeyCode, Op> keyCodeOpHashMap, double initialAngle) {
        this.maze = maze;
        this.keyCodeOpHashMap = keyCodeOpHashMap;
        this.COLOR_NAME = colorName;

        bulletManager = new BulletManager(maze);

        Point2D headPoint = new Point2D(
                body.getWidth() - head.getWidth() / 2, // half of head sticks out.
                body.getHeight() / 2 - head.getHeight() / 2 // head is in the vertical middle of tank.
        );
        head.moveTo(headPoint);

        this.BODY_COLOR = bodyColor;
        this.HEAD_COLOR = headColor;
        this.OUT_OF_AMNO_HEAD_COLOR = outOfAmnoColor;
        headPolygon.setFill(HEAD_COLOR);
        bodyPolygon.setFill(BODY_COLOR);

        rotate(initialAngle);

        // Move to the middle of some random cell.
        Random rand = new Random();
        int col = rand.nextInt(Maze.COLUMNS);
        int row = rand.nextInt(Maze.ROWS);
        moveBy(new Point2D(col * Cell.LENGTH, row * Cell.LENGTH));
        moveBy(new Point2D(Maze.THICKNESS, Maze.THICKNESS));
        moveBy(new Point2D((Cell.LENGTH - Maze.THICKNESS) / 2, (Cell.LENGTH - Maze.THICKNESS) / 2));
        moveBy(new Point2D(-body.getWidth() / 2, -body.getHeight() / 2));

        syncPolygons();
    }

    protected BulletManager getBulletManager() {
        return bulletManager;
    }

    protected Node getNode() {
        // head added after so that you can see it in front.
        return new Group(bodyPolygon, headPolygon);
    }

    protected Node getNodeFacingRight() {
        Polygon headPolygonCopy = new Polygon();
        Polygon bodyPolygonCopy = new Polygon();

        Rectangle headCopy = new Rectangle(head);
        Rectangle bodyCopy = new Rectangle(body);

        // TODO should the tank be pointing out or into the alert❓¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿ need more thought.
        headCopy.rotate(pivot, -theta+Math.PI);
        bodyCopy.rotate(pivot, -theta+Math.PI);

        headPolygonCopy.getPoints().setAll(headCopy.getDoubles());
        bodyPolygonCopy.getPoints().setAll(bodyCopy.getDoubles());

        headPolygonCopy.setFill(headPolygon.getFill());
        bodyPolygonCopy.setFill(bodyPolygon.getFill());

        return new Group(bodyPolygonCopy, headPolygonCopy);
    }

    // The direction of angles is reversed because the coordinate system is reversed.
    protected void right() {
        lastMovementOp = Op.RIGHT;
        rotate(TURNING_ANGLE);
    }

    protected void left() {
        lastMovementOp = Op.LEFT;
        rotate(-TURNING_ANGLE);
    }

    private void rotate(double theta) {
        this.theta += theta;
        body.rotate(pivot, theta);
        head.rotate(pivot, theta);
        decomposedVelocity = Physics.decomposeVector(VELOCITY, this.theta);
        negativeDecomposedVelocity = Physics.decomposeVector(-VELOCITY, this.theta);
        syncPolygons();
    }

    private void syncPolygons() {
        headPolygon.getPoints().setAll(head.getDoubles());
        bodyPolygon.getPoints().setAll(body.getDoubles());
    }

    protected void forward() {
        lastMovementOp = Op.FORWARD;
        moveBy(decomposedVelocity);
    }

    protected void back() {
        lastMovementOp = Op.REVERSE;
        moveBy(negativeDecomposedVelocity);
    }

    private void moveBy(Point2D point) {
        head.moveBy(point);
        body.moveBy(point);
        pivot = pivot.add(point);
        syncPolygons();
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

    protected boolean checkCollision(Shape shape) {
        return Physics.checkCollision(headPolygon, shape) || Physics.checkCollision(bodyPolygon, shape);
    }

    // TODO better edge mechanics, e.g. instead of stopping, we lower velocity/slide.
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
        switch (lastMovementOp) {
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
                    tank.rotate(-TURNING_ANGLE / 12);
                };
                break;
            case LEFT:
                reverseOp = () -> {
                    tank.rotate(TURNING_ANGLE / 12);
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

    private Op lastMovementOp;

    private enum Op {
        FORWARD,
        RIGHT,
        LEFT,
        REVERSE,
        FIRE,
    }

    private HashMap<KeyCode, Op> keyCodeOpHashMap;
    // keys pressed since the last frame.
    private HashSet<Op> pressedOps = new HashSet<>();

    protected void handlePressed(KeyCode keyCode) {
        pressedOps.add(keyCodeOpHashMap.get(keyCode));
    }

    protected void handleReleased(KeyCode keyCode) {
        Op op = keyCodeOpHashMap.get(keyCode);
        if (op == Op.FIRE) {
            bulletManager.lock = false;
        }
        pressedOps.remove(op);
    }

    protected void handle(long nanos) {
        bulletManager.update(nanos);
        if (pressedOps.contains(Op.FIRE)) {
            bulletManager.addBullet(
                    getBulletLaunchPoint(),
                    getTheta(),
                    nanos
            );
            bulletManager.lock = true;
        }
        bulletManager.handleCollisions();

        if (bulletManager.isReloading()) {
            headPolygon.setFill(OUT_OF_AMNO_HEAD_COLOR);
        } else {
            headPolygon.setFill(HEAD_COLOR);
        }

        if (pressedOps.contains(Op.RIGHT)) {
            right();
        }
        if (pressedOps.contains(Op.LEFT)) {
            left();
        }
        maze.handleCollision(this);

        if (pressedOps.contains(Op.FORWARD)) {
            forward();
        }
        if (pressedOps.contains(Op.REVERSE)) {
            back();
        }
        maze.handleCollision(this);
    }

    protected static final HashMap<KeyCode, Op> keyCodeOpHashMap1 = new HashMap<>();

    static {
        keyCodeOpHashMap1.put(KeyCode.UP, Op.FORWARD);
        keyCodeOpHashMap1.put(KeyCode.RIGHT, Op.RIGHT);
        keyCodeOpHashMap1.put(KeyCode.DOWN, Op.REVERSE);
        keyCodeOpHashMap1.put(KeyCode.LEFT, Op.LEFT);
        keyCodeOpHashMap1.put(KeyCode.PERIOD, Op.FIRE);
    }


    protected static final HashMap<KeyCode, Op> keyCodeOpHashMap2 = new HashMap<>();

    static {
        keyCodeOpHashMap2.put(KeyCode.W, Op.FORWARD);
        keyCodeOpHashMap2.put(KeyCode.D, Op.RIGHT);
        keyCodeOpHashMap2.put(KeyCode.S, Op.REVERSE);
        keyCodeOpHashMap2.put(KeyCode.A, Op.LEFT);
        keyCodeOpHashMap2.put(KeyCode.V, Op.FIRE);
    }


    private boolean dead;

    protected void kill() {
        dead = true;
        headPolygon.setFill(DEAD_COLOR);
        bodyPolygon.setFill(DEAD_COLOR);
    }


    protected boolean isDead() {
        return dead;
    }

    protected String getColorName() {
        return COLOR_NAME;
    }
}
