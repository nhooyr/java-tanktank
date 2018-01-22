package tank;

import javafx.application.Application;
import javafx.stage.Stage;


// TODO probably bad UX to always center the stage when first showing a scene. oh well
// TODO write docs/cleanup
public class Main extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        stage.setResizable(false);
        stage.setTitle("Tank Tank");

        MainMenu.display(stage);
        stage.show();
    }
}