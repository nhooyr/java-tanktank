package tank;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URI;

// TODO decorate the main menu with tanks and also use idiomatic javafx layouting instead of hard coding.
class MainMenu {
    private static final double WIDTH = 300;
    private static final double HEIGHT = 200;

    static void display(final Stage stage) {
        final Group root = new Group();
        final Scene scene = new Scene(root, WIDTH, HEIGHT);

        final Text title = new Text("Tank Trouble");
        title.setFont(Font.font(30));
        final Bounds titleBounds = title.getLayoutBounds();
        // Center.
        title.setLayoutX(WIDTH / 2 - titleBounds.getWidth() / 2);
        title.setLayoutY(titleBounds.getHeight() + 10);

        final Button playButton = new Button("play");
        playButton.setDefaultButton(true);
        playButton.setPrefWidth(60);
        playButton.setOnAction(event -> {
            final Game game = new Game(stage);
            game.start();
        });

        final Button helpButton = new Button("help");
        helpButton.setPrefWidth(60);
        helpButton.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/nhooyr/java-tanktank"));
            } catch (final Exception e) {
                e.printStackTrace();
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Tank Tank");
                alert.setHeaderText("Help link could not be opened");
                alert.setContentText("Please take a look at the printed stack trace.");
                alert.show();
            }
        });

        final VBox vbox = new VBox(20);
        vbox.getChildren().addAll(
                playButton,
                helpButton
        );
        vbox.setAlignment(Pos.CENTER);
        vbox.setLayoutX(WIDTH / 2 - playButton.getPrefWidth() / 2);
        vbox.setLayoutY(title.getLayoutY() + vbox.getSpacing() * 1.5);

        root.getChildren().addAll(title, vbox);
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}
