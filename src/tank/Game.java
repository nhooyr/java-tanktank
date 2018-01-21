package tank;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// All methods that will be called from JavaFX onto user code run on a
// single thread so no synchronization across anything is necessary.
// https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-architecture.htm
class Game {
    private Maze maze = new Maze();
    private Tank tank1 = new Tank(0, Color.SKYBLUE, Color.DARKBLUE, Color.LIGHTBLUE, maze, Tank.keyCodeOpHashMap1);
    private Tank tank2 = new Tank(Math.PI, Color.PINK, Color.DARKRED, Color.LIGHTPINK, maze, Tank.keyCodeOpHashMap2);

    // WIDTH and HEIGHT of the scene.
    // We add the thickness because at far right and bottom edges of the screen we are going to place
    // the final sides of the grid and they need additional space because of how the grid drawing algorithm works.
    // See the Maze class.
    private final static double WIDTH = Cell.LENGTH * Maze.COLUMNS + Maze.THICKNESS;
    private final static double HEIGHT = Cell.LENGTH * Maze.ROWS + Maze.THICKNESS;

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
                tank2.getNode(),
                tank1.getBulletManager().getNode(),
                tank2.getBulletManager().getNode()
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

        // TODO in future use a single bullet manager and a separate bullet limiter.
        if (tank1.getBulletManager().isDeadTank(tank1) || tank2.getBulletManager().isDeadTank(tank1)) {
            // TODO tank1 wins.
        }
        if (tank1.getBulletManager().isDeadTank(tank2) || tank2.getBulletManager().isDeadTank(tank2)) {
            // TODO tank2 wins.
        }


        tank1.handle(nanos);
        tank2.handle(nanos);
    }

    private void handlePressed(KeyEvent e) {
        tank1.handlePressed(e.getCode());
        tank2.handlePressed(e.getCode());
    }

    private void handleReleased(KeyEvent e) {
        tank1.handleReleased(e.getCode());
        tank2.handleReleased(e.getCode());
    }
}
