package tank;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Tank Tank");
        Game game = new Game(stage);
        game.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}