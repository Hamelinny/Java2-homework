package ru.spbau.sofronova.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/** Class which interacts with user using graphical interface. */
public class ClientGUI extends Application {

    /**
     * Main method.
     * @param args arguments from command line
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The point there application starts.
     * @param primaryStage scene must be set here (actually, idk what that thing is)
     */
    @Override
    public void start(@NotNull Stage primaryStage) {
        primaryStage.setTitle("Simple FTP client");
        primaryStage.setScene(new ClientScene(primaryStage).getScene());
        primaryStage.show();
    }
}
