package tank;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.HashSet;

// All methods that will be called from JavaFX onto user code run on a
// single thread so no synchronization across anything is necessary.
// https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-architecture.htm
class Game {
    private Tank tank;
    private Maze maze;
    private BulletManager bulletManager;

    // WIDTH and HEIGHT of the scene.
    // We add the thickness because at far right and bottom edges of the screen we are going to place
    // the final sides of the grid and they need additional space because of how the grid drawing algorithm works.
    // See the Maze class.
    private final static double WIDTH = Cell.LENGTH * Maze.COLUMNS + Maze.THICKNESS;
    private final static double HEIGHT = Cell.LENGTH * Maze.ROWS + Maze.THICKNESS;

    // keys pressed since the last frame.
    private HashSet<KeyCode> pressedKeys;

    // bulletLock is used to ensure that a new bullet cannot be fired
    // until the bullet firing key is released.
    private boolean bulletLock;

    protected Game(Stage stage) {
        stage.setResizable(false);
        pressedKeys = new HashSet<>();

        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        maze = new Maze(root);
        tank = new Tank(root);
        bulletManager = new BulletManager(root, maze, tank);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::handlePressed);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, this::handleReleased);

        stage.setScene(scene);
        stage.show();
    }

    protected void start() {
        Game g = this;
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                g.handle();
            }
        };
        timer.start();
    }

    // The game loop runs on an AnimationTimer which calls handle() about every 1/60 of a second.
    // Rendering and updating are handled separately in JavaFX so this is the standard design of a game loop.
    // https://gafferongames.com/post/fix_your_timestep/
    // https://gamedev.stackexchange.com/questions/1589/when-should-i-use-a-fixed-or-variable-time-step
    // There are many other articles recommending this design.
    // Though, I am not positive it works the way I think it does and the docs are not very clear. So whatever,
    // no big deal.
    private void handle() {
        if (pressedKeys.contains(KeyCode.RIGHT)) {
            tank.right();
        }
        if (pressedKeys.contains(KeyCode.LEFT)) {
            tank.left();
        }
        if (pressedKeys.contains(KeyCode.UP)) {
            tank.forward();
        }
        if (pressedKeys.contains(KeyCode.DOWN)) {
            tank.back();
        }
        tank.syncPolygons();
        if (pressedKeys.contains(KeyCode.SPACE) && !bulletLock) {
            bulletLock = true;
            bulletManager.addBullet(
                    tank.getBulletLaunchPoint(),
                    tank.getTheta()
            );
        }
        bulletManager.update();
    }

    private void handlePressed(KeyEvent e) {
        pressedKeys.add(e.getCode());
    }

    private void handleReleased(KeyEvent e) {
        if (e.getCode() == KeyCode.SPACE) {
            bulletLock = false;
        }
        pressedKeys.remove(e.getCode());
    }
}
