package tank;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Iterator;

class BulletManager {
    private static final int MAX_BULLETS = 5;

    private final ArrayList<Bullet> bullets = new ArrayList<>(MAX_BULLETS);
    private final Group group = new Group();
    private final Maze maze;

    boolean lock;

    BulletManager(final Maze maze) {
        this.maze = maze;
    }

    Node getNode() {
        return group;
    }

    void addBullet(final Point2D launchPoint, final double theta, final long nanos) {
        if (lock || bullets.size() >= MAX_BULLETS) {
            return;
        }
        final Bullet bullet = new Bullet(launchPoint, theta, nanos);
        group.getChildren().add(bullet.getShape());
        bullets.add(bullet);
    }

    void update(final long nanos) {
        final Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            final Bullet bullet = it.next();
            if (nanos > bullet.getExpiry()) {
                it.remove();
                group.getChildren().remove(bullet.getShape());
            } else {
                bullet.update();
            }
        }
    }

    void handleCollisions() {
        bullets.forEach(maze::handleCollision);
    }

    boolean isDeadTank(final Tank tank) {
        for (final Bullet bullet : bullets) {
            if (tank.checkCollision(bullet.getShape())) {
                return true;
            }
        }
        return false;
    }

    boolean isReloading() {
        return bullets.size() == MAX_BULLETS;
    }
}