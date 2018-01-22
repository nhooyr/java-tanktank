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

// TODO decorate the main menu with tanks and also use proper javafx layouting instead of hard coding.
class MainMenu {
    private final static double WIDTH = 300;
    private final static double HEIGHT = 200;

    static protected void display(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        Text title = new Text("Tank Trouble");
        title.setFont(Font.font(30));
        Bounds titleBounds = title.getLayoutBounds();
        // Center.
        title.setLayoutX(WIDTH / 2 - titleBounds.getWidth() / 2);
        title.setLayoutY(titleBounds.getHeight() + 10);


        Button playButton = new Button("play");
        playButton.setPrefWidth(60);
        playButton.setOnAction(event -> {
            Game game = new Game(stage);
            game.start();
        });

        Button helpButton = new Button("help");
        helpButton.setPrefWidth(60);
        helpButton.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/nhooyr/java-tanktank"));
            } catch (Exception e) {
                e.printStackTrace()
                ;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Tank Tank");
                alert.setHeaderText("Help link could not be opened");
                alert.setContentText("Please take a look at the printed stack trace.");
                alert.show();
            }
        });

        VBox vbox = new VBox(20);
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
