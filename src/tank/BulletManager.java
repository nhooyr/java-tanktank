package tank;

import javafx.geometry.Point2D;
import javafx.scene.Group;

import java.util.ArrayList;

class BulletManager {
    private ArrayList<Bullet> bullets;
    private Group group;
    private Maze maze;
    private Tank tank;

    protected BulletManager(Group root, Maze maze, Tank tank) {
        bullets = new ArrayList<>(100);
        group = new Group();
        root.getChildren().add(group);
        this.maze = maze;
        this.tank = tank;
    }

    protected void addBullet(Point2D launchPoint, double theta) {
        Bullet bullet = new Bullet(group, launchPoint, theta);
        bullets.add(bullet);
    }

    protected void update() {
        bullets.forEach(bullet -> bullet.update());
    }

    protected void handleCollisions() {
        bullets.forEach(bullet -> maze.handleCollision(bullet));
    }
}