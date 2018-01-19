package tank;

import javafx.scene.Group;

import java.util.ArrayList;

class BulletManager {
    private ArrayList<Bullet> bullets;
    private Group group;

    protected BulletManager(Group root) {
        bullets = new ArrayList<>(100);
        group = new Group();
        root.getChildren().add(group);
    }

    protected void addBullet(double x, double y, double theta) {
        Bullet bullet = new Bullet(group, x, y, theta);
        bullets.add(bullet);
    }

    protected void update() {
        bullets.forEach(bullet -> bullet.update());
        // TODO check collisions.
    }
}