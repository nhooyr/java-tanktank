package tank;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

class Bullet {
    protected static final double RADIUS = Tank.HEAD_HEIGHT/2; // exported for use in Tank.
    private static final Paint COLOR = Color.RED;
    private static final double VELOCITY = Tank.VELOCITY * 1.5;
    private double dx;
    private double dy;
    private Circle circle;

    protected Bullet(Group group, double x, double y, double theta) {
        circle = new Circle(x, y, RADIUS, COLOR);
        setTheta(theta);
        group.getChildren().add(circle);
    }

    protected void setTheta(double theta) {
        this.dx = Math.cos(theta) * VELOCITY;
        this.dy = Math.sin(theta) * VELOCITY;
    }

    protected void update() {
        circle.setTranslateX(circle.getTranslateX() + dx);
        circle.setTranslateY(circle.getTranslateY() + dy);
    }
}