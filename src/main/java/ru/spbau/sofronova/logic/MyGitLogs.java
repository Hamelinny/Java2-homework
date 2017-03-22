package ru.spbau.sofronova.logic;


import ru.spbau.sofronova.entities.Commit;
import ru.spbau.sofronova.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static ru.spbau.sofronova.logic.MyGitUtils.*;

public class MyGitLogs {

    public static void updateLog(String branchName, String message) throws LogIOException {
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
