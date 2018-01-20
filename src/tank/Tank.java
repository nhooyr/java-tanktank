package tank;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

class Tank {
    protected final static int VELOCITY = 3; // exported for use in Bullet.
    private final static int TURNING_ANGLE = 5;
    private final static int WIDTH = 40;
    protected  final static int HEIGHT = 30; // exported for use in Maze.
    private final static int HEAD_WIDTH = WIDTH / 2;
    protected final static int HEAD_HEIGHT = HEIGHT / 4; // exported for use in Bullet.
    private final static Color COLOR = Color.BLUE;

    private Group group;
    private Rectangle head;
    private Rectangle body;
    private Rotate rotate;


    protected Tank(Group root) {
        group = new Group();

        body = new Rectangle(WIDTH, HEIGHT, COLOR);
        head = new Rectangle(HEAD_WIDTH, HEAD_HEIGHT, COLOR);
        head.setX(body.getX() + body.getWidth() - head.getWidth() / 2);
        head.setY(body.getY() + body.getHeight() / 2 - head.getHeight() / 2);

        rotate = new Rotate();
        rotate.setPivotX(body.getWidth() / 2);
        rotate.setPivotY(body.getHeight() / 2);

        // head needs to be added after so that it is in front.
        group.getChildren().addAll(body, head);
        group.getTransforms().add(rotate);

        group.setTranslateX(Maze.THICKNESS);
        group.setTranslateY(Maze.THICKNESS);

        root.getChildren().add(group);
    }

    protected void right() {
        // This is really wierd... Why do we add the angle to move it right? Shouldn't it be the other way around?
        // Vice versa for this.left()
        rotate.setAngle(rotate.getAngle() + TURNING_ANGLE);
    }

    protected void left() {
        rotate.setAngle(rotate.getAngle() - TURNING_ANGLE);
    }

    protected void forward() {
        move(VELOCITY);
    }

    protected void back() {
        move(-VELOCITY);
    }

    private void move(int d) {
        double theta = getTheta();

        double x = group.getTranslateX() + Physics.DisplaceX(d, theta);
        group.setTranslateX(x);

        double y = group.getTranslateY() + Physics.DisplaceY(d, theta);
        group.setTranslateY(y);
    }

    private double width() {
        return head.getX() + head.getWidth();
    }

    private double height() {
        return body.getHeight();
    }

    protected double getBulletLaunchX() {
        double x = group.getTranslateX();
        x += rotate.getTx();

        // Now we are the top right of the tank.
        // We need to move the center of the bullet to the width of the tank plus the radius of the bullet.
        // This will spawn the bullet right at the very tip of the tank. Furthermore, we move the center of the
        // bullet to half of the height of the tank. This will spawn it at the middle of the tip.
        // The exact same operations apply for the bottom function.
        double theta = getTheta();
        x += Physics.DisplaceX(width() + Bullet.RADIUS, theta);
        x += Physics.DisplaceX(height() / 2, theta + Math.PI / 2);
        return x;
    }

    protected double getBulletLaunchY() {
        double y = group.getTranslateY();
        y += rotate.getTy();

        // For a description of these transformations, see getBulletLaunchX().
        double theta = getTheta();
        y += Physics.DisplaceY(width() + Bullet.RADIUS, theta);
        y += Physics.DisplaceY(height() / 2, theta + Math.PI / 2);
        return y;
    }

    protected double getTheta() {
        return Math.toRadians(rotate.getAngle());
    }
}
