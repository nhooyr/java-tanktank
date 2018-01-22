package tank;

import javafx.application.Application;
import javafx.stage.Stage;


// TODO probably bad UX to always center the stage when first showing a scene. oh well
// TODO write docs/cleanup
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);
        stage.setTitle("Tank Tank");

        MainMenu.display(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}