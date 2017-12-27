package tank;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.HashSet;

class Tank {
    private Group group;
    private Rectangle head;
    private Rectangle body;

    private final static int VELOCITY = 4;
    private final static int TURNING_ANGLE = 6;

    private Rotate rotate;
    private Translate translate;

    private HashSet<KeyCode> pressedCodes;

    Tank(Group root, Scene scene) {
        pressedCodes = new HashSet<>();
        group = new Group();

        body = new Rectangle(30, 20, Color.RED);
        head = new Rectangle(15, 5, Color.BLUE);
        head.setX(body.getX() + body.getWidth() - head.getWidth() / 2);
        head.setY(body.getY() + body.getHeight() / 2 - head.getHeight() / 2);

        rotate = new Rotate();
        rotate.setPivotX(body.getX() + body.getWidth() / 2);
        rotate.setPivotY(body.getY() + body.getHeight() / 2);
        translate = new Translate();

        // head needs to be added after so that it is in front.
        group.getChildren().addAll(body, head);
        // rotate needs to go last so that the pivot point is not affected by translate.
        group.getTransforms().addAll(translate, rotate);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::handlePressed);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, this::handleReleased);
        root.getChildren().add(group);
    }

    void update() {
        if (pressedCodes.contains(KeyCode.RIGHT)) {
            rotate.setAngle(rotate.getAngle() + TURNING_ANGLE);
        }
        if (pressedCodes.contains(KeyCode.LEFT)) {
            rotate.setAngle(rotate.getAngle() - TURNING_ANGLE);
        }
        if (pressedCodes.contains(KeyCode.UP)) {
            move(VELOCITY);
        }
        if (pressedCodes.contains(KeyCode.DOWN)) {
            move(-VELOCITY);
        }
    }

    private void move(int d) {
        double theta = Math.toRadians(rotate.getAngle());
        double dx = Math.cos(theta) * d;
        double dy = Math.sin(theta) * d;
        translate.setX(translate.getX() + dx);
        translate.setY(translate.getY() + dy);
    }

    private void handlePressed(KeyEvent e) {
        pressedCodes.add(e.getCode());
    }

    private void handleReleased(KeyEvent e) {
        pressedCodes.remove(e.getCode());
    }
}
