package tank;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;


// EXPLAIN WHY NO NEED FOR CONCURRENCY MECHIANSM: https://www.java-forums.org/blogs/javafx/1598-where-gui-events-happen-javafx-application-thread.html
// EXPLAIN WHY GAME LOOP DESIGN. time step.
public class Main extends Application {

    final int WIDTH = 600;
    final int HEIGHT = 400;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Basic JavaFX demo");

        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        Tank tank = new Tank();
        scene.setOnKeyPressed(tank::handle);
        scene.setOnKeyReleased(tank::handle);
        root.getChildren().add(tank.group);

        stage.setScene(scene);
        stage.show();

        final AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                tank.update();
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}