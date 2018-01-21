package tank;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Iterator;

class BulletManager {
    private static final int MAX_BULLETS = 10;

    private ArrayList<Bullet> bullets = new ArrayList<>(MAX_BULLETS);
    private Group group = new Group();
    private Maze maze;

    protected boolean lock;

    protected BulletManager(Maze maze) {
        this.maze = maze;
    }

    protected Node getNode() {
        return group;
    }

    protected void addBullet(Point2D launchPoint, double theta, long nanos) {
        if (lock || bullets.size() >= MAX_BULLETS) {
            return;
        }
        Bullet bullet = new Bullet(launchPoint, theta, nanos);
        group.getChildren().add(bullet.getShape());
        bullets.add(bullet);
    }

    protected void update(long nanos) {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            if (nanos > bullet.getExpiry()) {
                it.remove();
                group.getChildren().remove(bullet.getShape());
            } else {
                bullet.update();
            }
        }
    }

    protected void handleCollisions() {
        bullets.forEach(bullet -> maze.handleCollision(bullet));
    }

    protected boolean isDeadTank(Tank tank) {
        for (Bullet bullet : bullets) {
            if (tank.checkCollision(bullet.getShape())) {
                return true;
            }
        }
        return false;
    }
}