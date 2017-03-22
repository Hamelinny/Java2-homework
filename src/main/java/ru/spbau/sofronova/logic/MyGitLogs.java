package ru.spbau.sofronova.logic;


import com.sun.istack.internal.NotNull;
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

    /**
     * Method to add a message to log file of specified branch.
     * @param branchName branch which has a new event happened
     * @param message message to write to log
     * @throws LogIOException if there are IO problems during interaction with log file
     */
    public static void updateLog(@NotNull String branchName, @NotNull String message) throws LogIOException {
        Path logLocation = buildPath(LOGS_DIRECTORY, branchName);
        try {
            if (Files.notExists(logLocation))
                Files.createFile(logLocation);
            Files.write(logLocation, message.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new LogIOException();
        }
    }
}
