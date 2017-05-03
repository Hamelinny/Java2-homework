package ru.spbau.sofronova.client;


import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.server.Server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static javafx.scene.layout.Priority.ALWAYS;


/**Class which builds scene for given stage.*/
public class ClientScene {

    private final String TEXT_BACKGROUND_COLOR = "-fx-background-color: #F6E3CE";
    private final String FIELD_BACKGROUND_COLOR = "-fx-control-inner-background: #F7F2E0";

    Stage stage;

    ClientScene(@NotNull Stage stage) {
        this.stage = stage;
    }

    Scene getScene() {
        List<Node> leftNodes = new LinkedList<>();
        List<Node> rightNodes = new LinkedList<>();

        Label lastDir = new Label("");
        lastDir.setFont(new Font(15.0));
        lastDir.setStyle(TEXT_BACKGROUND_COLOR);
        rightNodes.add(lastDir);
        ListView<String> fileList = new ListView<>();
        fileList.setStyle(FIELD_BACKGROUND_COLOR);
        rightNodes.add(fileList);

        Label pathLabel = new Label("Path to list/download:");
        pathLabel.setFont(new Font(15.0));
        pathLabel.setStyle(TEXT_BACKGROUND_COLOR);
        leftNodes.add(pathLabel);
        TextField pathTextField = new TextField();
        pathTextField.setStyle(FIELD_BACKGROUND_COLOR);
        leftNodes.add(pathTextField);

        Button listButton = new Button("List");
        Button getButton = new Button("Download");
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, ALWAYS);
        leftNodes.add(new HBox(listButton, spacer, getButton));

        Label toSave = new Label("Path to save (must be nonexistent file):");
        toSave.setFont(new Font(15.0));
        toSave.setStyle(TEXT_BACKGROUND_COLOR);
        leftNodes.add(toSave);
        TextField pathToSave = new TextField();
        pathToSave.setStyle(FIELD_BACKGROUND_COLOR);
        leftNodes.add(pathToSave);

        Button chooseButton = new Button("Choose directory");
        leftNodes.add(chooseButton);

        listButton.setOnAction(event ->
                executeList(pathTextField.getText(), lastDir, fileList));

        fileList.setOnMouseClicked(event -> {
            if (fileList.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            if (event.getClickCount() == 1) {
                String selectedItem = fileList.getSelectionModel().getSelectedItem();
                pathTextField.setText(Paths.get(lastDir.getText(), selectedItem).toString());
                return;
            }
            if (event.getClickCount() == 2) {
                executeList(pathTextField.getText(), lastDir, fileList);
            }
        });

        getButton.setOnAction(event -> {
            if (!pathToSave.getText().equals("")) {
                if (Files.notExists(Paths.get(pathToSave.getText())))
                    executeGet(pathTextField.getText(), pathToSave.getText());
                else
                    showMessage(ERROR, "Fail: ", "file where you want to store content already exists (or it's directory)");
            }
            else {
                chooseFolder(pathToSave, stage);
            }
        });

        chooseButton.setOnAction(event -> chooseFolder(pathToSave, stage));
        return new Scene(buildView(leftNodes, rightNodes));
    }

    private void chooseFolder(@NotNull TextInputControl textInput, @NotNull Stage stage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a directory where to save file and when specify name of saved file:");
        File selected = chooser.showDialog(stage);
        if (selected != null) {
            textInput.setText(selected.getAbsolutePath());
        }
    }


    @NotNull
    private Parent buildView(@NotNull List<Node> leftNodes, @NotNull List<Node> rightNodes) {
        Node[] left = new Node[leftNodes.size()];
        leftNodes.toArray(left);
        Node[] right = new Node[rightNodes.size()];
        rightNodes.toArray(right);

        VBox leftVbox = new VBox(left);
        leftVbox.setSpacing(5);
        leftVbox.setMinWidth(300);
        leftVbox.setPadding(new Insets(10, 10, 10, 10));

        VBox rightVbox = new VBox(right);
        rightVbox.setSpacing(5);
        rightVbox.setMinWidth(400);
        rightVbox.setPadding(new Insets(10, 10, 10, 10));

        HBox hbox = new HBox(leftVbox, rightVbox);
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(10, 10, 10, 10));
        hbox.setStyle("-fx-background-image: url('" + "cute.jpeg" + "'); " +
                "-fx-background-position: left top, center; " +
                "-fx-background-repeat: repeat;");
        return hbox;
    }

    private void executeGet(@NotNull String pathFrom, String toSave) {
        Client client = new Client(Server.SERVER_PORT);
        try {
            client.executeGet(pathFrom, toSave);
            showMessage(INFORMATION, "Success",
                    "Downloaded file " + pathFrom + " to " + toSave);

        } catch (Exception e) {
            showMessage(ERROR, "Fail: ", e.getMessage());
        }
    }

    private void executeList(@NotNull String pathFrom, @NotNull Label lastDir,
                                        @NotNull ListView<String> fileList) {
        Client client = new Client(Server.SERVER_PORT);
        try {
            List<String> paths = client.executeList(pathFrom);
            if (paths == null) {
                showMessage(ERROR, "Fail: ", "Error response");
                return;
            }
            lastDir.setText(pathFrom);
            fileList.getItems().clear();
            for (String file : paths) {
                fileList.getItems().add(file);
            }
        } catch (Exception e) {
            showMessage(ERROR, "Fail: ", e.getMessage());
        }
    }

    private static void showMessage(@NotNull Alert.AlertType type, @NotNull String header, @NotNull String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
    }
}
