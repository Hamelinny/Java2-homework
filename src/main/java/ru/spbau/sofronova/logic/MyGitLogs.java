package ru.spbau.sofronova.logic;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static ru.spbau.sofronova.logic.MyGitUtils.*;

/**
 * A class provides interaction with log files.
 */
public class MyGitLogs {

    private final MyGit repository;

    /**
     * Create a log interactor from repository.
     * @param repository repository entity
     */
    public MyGitLogs(@NotNull MyGit repository) {
        this.repository = repository;
    }

    /**
     * Method to add a message to log file of specified branch.
     * @param branchName branch which has a new event happened
     * @param message message to write to log
     * @throws LogIOException if there are IO problems during interaction with log file
     */
    public void updateLog(@NotNull String branchName, @NotNull String message) throws LogIOException {
        Path logLocation = buildPath(repository.LOGS_DIRECTORY, branchName);
        try {
            if (Files.notExists(logLocation))
                Files.createFile(logLocation);
            Files.write(logLocation, message.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new LogIOException("cannot update log\n");
        }
    }
}
