import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.reactfx.util.FxTimer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * A class which purpose is creating a scene for a game.
 */
public class GameBuildScene {

    private static Stage stage;

    GameBuildScene(Stage stage) {
        getField();
        this.stage = stage;
    }

    private final static int N = 4;

    private static int[][] field = new int[N][N];
    private static int count = 0;
    List<Integer> numbers = new ArrayList<>();
    private ListView<ListView<Node>> buttons = new ListView<>();

    Scene getScene() {
        for (int i = 0; i < N; i++) {
            ListView n = new ListView<>();
            n.setOrientation(Orientation.HORIZONTAL);
            n.setMinWidth(((double)N * 10));
            n.setMaxHeight(50);
            for (int j = 0; j < N; j++) {
                Button button = new Button();
                int i1 = i;
                int j1 = j;
                (button).setOnAction(event -> {
                    handle(i1, j1);
                });
                n.getItems().add(button);
            }
            buttons.getItems().add(n);
        }
        HBox[] hbox = new HBox[N];
        for (int i = 0; i < N; i++) {
            hbox[i] = new HBox(buttons.getItems().get(i));
            hbox[i].setSpacing(5);
            hbox[i].setMinWidth(10);
            hbox[i].setMinHeight(10);
        }
        VBox vbox = new VBox(hbox);
        return new Scene(vbox);
    }

    private void handle(int i, int j) {
        Label label = new Label(Integer.toString(field[i][j]));
        label.setFont(new Font(15.0));
        buttons.getItems().get(i).getItems().set(j, label);
        count++;
        numbers.add(i);
        numbers.add(j);
        if (count == 2) {
            if (!checkIfMatch()) {
                FxTimer.runLater(Duration.ofMillis(1000), this::retButtons);
            } else {
                count = 0;
                numbers.clear();
            }
        }

    }

    private void retButtons() {

        int i1 = numbers.get(0);
        int j1 = numbers.get(1);
        Button button1 = new Button();
        (button1).setOnAction((event -> {
            handle(i1, j1);
        }));
        buttons.getItems().get(i1).getItems().set(j1, button1);
        int i2 = numbers.get(2);
        int j2 = numbers.get(3);
        Button button2 = new Button();
        (button2).setOnAction((event -> {
            handle(i2, j2);
        }));
        buttons.getItems().get(i2).getItems().set(j2, button2);
        count = 0;
        numbers.clear();
    }


    private boolean checkIfMatch() {
        return ((Label)buttons.getItems().get(numbers.get(0)).getItems().get(numbers.get(1))).getText().equals(
                ((Label)buttons.getItems().get(numbers.get(2)).getItems().get(numbers.get(3))).getText()); //sorry
    }


    private static void getField() {
        int c0 = 0, c1 = 0;
        Random ran = new Random();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                field[i][j] = ((ran.nextInt() % 2) + 2) % 2;
                if (field[i][j] == 0)
                    c0++;
                else
                    c1++;
            }
        }
        if (c0 % 2 == 1) {
            for (int i = 0; i < N; i++) {
                boolean flag = false;
                for (int j = 0; j < N; j++) {
                    if (field[i][j] == 1) {
                        field[i][j] = 0;
                        flag = true;
                        break;
                    }
                }
                if (flag)
                    break;
            }
        }
    }
}
