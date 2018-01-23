package tank;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Iterator;

// BulletManager manages the creation and removal of the bullets of a tank.
class BulletManager {
    private static final int MAX_BULLETS = 5;

    private final ArrayList<Bullet> bullets = new ArrayList<>(MAX_BULLETS);
    private final Group group = new Group();
    private final Maze maze;

    // lock prevents the manager from firing any more bullet. Used to wait for the bullet firing key to release before
    // allowing another bullet to fire in Game.
    boolean lock;

    BulletManager(final Maze maze) {
        this.maze = maze;
    }

    // Used for adding the bullets to the scene.
    Node getNode() {
        return group;
    }

    // addBullet creates a bullet at the launchPoint moving in the direction theta. nanos is the current time and used
    // for removing the bullet when it is too old.
    void addBullet(final Point2D launchPoint, final double theta, final long nanos) {
        if (lock || bullets.size() >= MAX_BULLETS) {
            return;
        }
        final Bullet bullet = new Bullet(launchPoint, theta, nanos);
        group.getChildren().add(bullet.getShape());
        bullets.add(bullet);
    }

    // update updates the position of the bullets and removes expired ones.
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

    // handleMazeCollisions handles collisions between all of the manager's bullets and the maze.
    void handleMazeCollisions() {
        bullets.forEach(bullet -> {
            final ArrayList<Rectangle> segs = maze.getCollisionCandidates(bullet.getCenter());
            bullet.handleMazeCollision(segs);
        });
    }

    // isDeadTank returns true if at least one bullet intersects with the tank.
    boolean isDeadTank(final Tank tank) {
        for (final Bullet bullet : bullets) {
            if (Physics.isIntersecting(bullet.getShape(), tank.getShape())) {
                return true;
            }
        }
        return false;
    }

    boolean isReloading() {
        return bullets.size() == MAX_BULLETS;
    }
}