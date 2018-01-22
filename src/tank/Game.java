package tank;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;

// All methods that will be called from JavaFX onto user code run on a
// single thread so no synchronization across anything is necessary.
// https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-architecture.htm
class Game {
    private Maze maze = new Maze();
    private Tank tank1 = new Tank("blue", Color.SKYBLUE, Color.DARKBLUE, Color.LIGHTBLUE, maze, Tank.keyCodeOpHashMap1, 0);
    private Tank tank2 = new Tank("pink", Color.PINK, Color.DARKRED, Color.LIGHTPINK, maze, Tank.keyCodeOpHashMap2, Math.PI);

    private final Stage stage;

    // WIDTH and HEIGHT of the scene.
    // We add the thickness because at far right and bottom edges of the screen we are going to place
    // the final sides of the grid and they need additional space because of how the grid drawing algorithm works.
    // See the Maze class.
    private final static double WIDTH = Cell.LENGTH * Maze.COLUMNS + Maze.THICKNESS;
    private final static double HEIGHT = Cell.LENGTH * Maze.ROWS + Maze.THICKNESS;

    protected Game(Stage stage) {
        this.stage = stage;
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
        stage.centerOnScreen();
    }

    private AnimationTimer timer;

    protected void start() {
        Game g = this;
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                g.handle(now);
            }
        };
        timer.start();
    }

    private final static ButtonType restartButtonType = new ButtonType("RESTART", ButtonBar.ButtonData.NEXT_FORWARD);
    // This is unfortunate but javafx sucks. One of the buttons need to be a cancel button otherwise you cant X the dialog...
    // I'd rather not add a third button so this is how its going to work unfortunately. Worse part is that it treats
    // closing the window as clicking the cancel button, which is certainly not necessarily the case. Maybe this is a misuse
    // of alerts but whatever.
    private final static ButtonType mainMenuButtonType = new ButtonType("MAIN MENU", ButtonBar.ButtonData.NO);
    private final FPSMeter fpsMeter = new FPSMeter();

    // The game loop runs on an AnimationTimer which calls handle() about every 1/60 of a second.
    // Rendering and updating are handled separately in JavaFX so this is the standard design of a game loop.
    // https://gafferongames.com/post/fix_your_timestep/
    // https://gamedev.stackexchange.com/questions/1589/when-should-i-use-a-fixed-or-variable-time-step
    // There are many other articles recommending this design.
    // Though, I am not positive it works the way I think it does and the docs are not very clear. So whatever,
    // no big deal.
    private void handle(long nanos) {
        fpsMeter.handle(nanos);

        if (isTank1Dead() || isTank2Dead()) {
            timer.stop();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tank Tank");
            alert.setHeaderText("Game Over!");

            String alertContent = "Wow, what a close game. It's a tie!";
            // If later we allow the game to keep continuing in the background, we will need to ensure we keep
            // the alert's graphic in sync with the winning tank.
            // We could use either tank here for a tie because we just need to show a black tank.
            // Probably cleaner to create a brand new one. But whatever, this means when tank1 does win, we do not need
            // to update the graphic because its already correct.
            Node graphic = tank1.getNodeFacingRight();
            Tank winningTank = null;

            if (isTank1Dead()) {
                if (!isTank2Dead()) {
                    winningTank = tank2;
                    graphic = tank2.getNodeFacingRight();
                }
            } else {
                winningTank = tank1;
            }

            if (winningTank != null) {
                alertContent = String.format("Congratulations to the %s tank for winning!", winningTank.getColorName());
            }
            alert.setGraphic(graphic);
            alert.setContentText(alertContent);

            alert.getButtonTypes().setAll(mainMenuButtonType, restartButtonType);

            // Must run later because we cannot call alert.showAndWait() during animation processing. See its docs.
            // And we might want animation to continue down the road anyhow.
            Platform.runLater(() -> {

                // This is optional because the alert can be abnormally closed and return no result.
                // See https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Dialog.html
                Optional<ButtonType> buttonType = alert.showAndWait();

                // If the alert had no result, then we default to showing the main menu.
                if (!buttonType.isPresent() || buttonType.get() == mainMenuButtonType) {
                    MainMenu.display(stage);
                    return;
                }

                Game game = new Game(stage);
                game.start();
            });
            return;
        }

        // TODO in future use a single bullet manager and a separate bullet limiter.
        // TODO in the future another possibility would be to allow the nondead tank to move. Not a big deal right now.
        if (tank1.getBulletManager().isDeadTank(tank1) || tank2.getBulletManager().isDeadTank(tank1)) {
            tank1.kill();
        }
        if (tank1.getBulletManager().isDeadTank(tank2) || tank2.getBulletManager().isDeadTank(tank2)) {
            tank2.kill();
        }
        if (isTank1Dead() || isTank2Dead()) {
            // We draw the dead tanks before we prompt the players.
            return;
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

    protected boolean isTank1Dead() {
        return tank1.isDead();
    }

    protected boolean isTank2Dead() {
        return tank2.isDead();
    }
}
