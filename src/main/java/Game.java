import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class for simple game.
 */
public class Game extends Application {

    /**
     * Main method.
     * @param args arguments from command line.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * It starts here.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Game");
        primaryStage.setScene(new GameBuildScene(primaryStage).getScene());
        primaryStage.show();

    }
}
