package tank;

import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.HashSet;

class Tank {
    Group group;
    Rectangle head;
    Rectangle body;

    Rotate rotate;
    Translate translate;

    HashSet<KeyCode> pressedCodes;

    protected Tank() {
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
    }

    protected void handle(KeyEvent e) {
        if (e.getEventType() == KeyEvent.KEY_PRESSED) {
            pressedCodes.add(e.getCode());
        } else if (e.getEventType() == KeyEvent.KEY_RELEASED) {
            pressedCodes.remove(e.getCode());
        }
    }

    protected void update() {
        if (pressedCodes.contains(KeyCode.RIGHT)) {
            rotate.setAngle(rotate.getAngle() + 5);
        }
        if (pressedCodes.contains(KeyCode.LEFT)) {
            rotate.setAngle(rotate.getAngle() - 5);
        }
        if (pressedCodes.contains(KeyCode.UP)) {
            move(5);
        }
        if (pressedCodes.contains(KeyCode.DOWN)) {
            move(-5);
        }
    }

    protected void move(int d) {
        double theta = Math.toRadians(rotate.getAngle());
        double dx = Math.cos(theta) * d;
        double dy = Math.sin(theta) * d;
        translate.setX(translate.getX() + dx);
        translate.setY(translate.getY() + dy);
    }
}
