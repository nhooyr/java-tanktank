package tank;

import javafx.scene.Group;

import java.util.ArrayList;

class BulletManager {
    private ArrayList<Bullet> bullets;
    private Group group;
    private Maze maze;

    protected BulletManager(Group root, Maze maze) {
        bullets = new ArrayList<>(100);
        group = new Group();
        root.getChildren().add(group);
        this.maze = maze;
    }

    protected void addBullet(double x, double y, double theta) {
        Bullet bullet = new Bullet(group, x, y, theta);
        bullets.add(bullet);
    }

    protected void update() {
        bullets.forEach(bullet -> {
            bullet.update();
            Maze.CollisionStatus collisionStatus = maze.checkBulletCollision(bullet);
            // If there was a collision, the bullet will be moved back such that there is no collision anymore.
            // We do not need to move it back ourselves.
            switch (collisionStatus) {
                case HORIZONTAL:
                    bullet.horizontalBounce();
                    bullet.update();
                case VERTICAL:
                    bullet.verticalBounce();
                    bullet.update();
            }
        });
        // TODO check collisions.
    }
}