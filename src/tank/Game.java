package tank;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;

// All methods that will be called from JavaFX onto user code run on a
// single thread so no synchronization across anything is necessary.
// https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-architecture.htm
class Game {
    private Maze maze = new Maze();
    private Tank tank1 = new Tank(0, Color.BLUE, maze);
    private Tank tank2 = new Tank(Math.PI, Color.ORANGE, maze);

    // WIDTH and HEIGHT of the scene.
    // We add the thickness because at far right and bottom edges of the screen we are going to place
    // the final sides of the grid and they need additional space because of how the grid drawing algorithm works.
    // See the Maze class.
    private final static double WIDTH = Cell.LENGTH * Maze.COLUMNS + Maze.THICKNESS;
    private final static double HEIGHT = Cell.LENGTH * Maze.ROWS + Maze.THICKNESS;

    // keys pressed since the last frame.
    private HashSet<KeyCode> pressedKeys = new HashSet<>();

    // bulletLock is used to ensure that a new bullet cannot be fired
    // until the bullet firing key is released.
    private boolean bulletLock;

    protected Game(Stage stage) {
        stage.setResizable(false);

        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        root.getChildren().addAll(
                maze.getNode(),
                tank1.getNode(),
                tank2.getNode()
        );

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
                g.handle(now);
            }
        };
        timer.start();
    }

    private FPSMeter fpsMeter = new FPSMeter();

    // The game loop runs on an AnimationTimer which calls handle() about every 1/60 of a second.
    // Rendering and updating are handled separately in JavaFX so this is the standard design of a game loop.
    // https://gafferongames.com/post/fix_your_timestep/
    // https://gamedev.stackexchange.com/questions/1589/when-should-i-use-a-fixed-or-variable-time-step
    // There are many other articles recommending this design.
    // Though, I am not positive it works the way I think it does and the docs are not very clear. So whatever,
    // no big deal.
    private void handle(long nanos) {
        fpsMeter.handle(nanos);

        tank1.getBulletManager().update(nanos);
        if (pressedKeys.contains(KeyCode.SPACE) && !bulletLock) {
            bulletLock = true;
            tank1.getBulletManager().addBullet(
                    tank1.getBulletLaunchPoint(),
                    tank1.getTheta(),
                    nanos
            );
        }
        tank1.getBulletManager().handleCollisions();

        if (pressedKeys.contains(KeyCode.RIGHT)) {
            tank1.right();
        }
        if (pressedKeys.contains(KeyCode.LEFT)) {
            tank1.left();
        }
        maze.handleCollision(tank1);

        if (pressedKeys.contains(KeyCode.UP)) {
            tank1.forward();
        }
        if (pressedKeys.contains(KeyCode.DOWN)) {
            tank1.back();
        }
        maze.handleCollision(tank1);
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
