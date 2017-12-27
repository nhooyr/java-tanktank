package tank;

import javafx.scene.Group;
import javafx.scene.Scene;

class Game {
    private Tank tank;

    Game(Group root, Scene scene) {
        tank = new Tank(root, scene);
    }

    void update() {
        tank.update();
    }
}
