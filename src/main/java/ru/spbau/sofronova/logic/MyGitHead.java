package ru.spbau.sofronova.logic;


import ru.spbau.sofronova.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.spbau.sofronova.logic.MyGitUtils.*;

public class MyGitHead {

    static void updateHead(String currentBranch) throws HeadIOException {
        try {
            Files.write(HEAD, currentBranch.getBytes());
        }
        catch (IOException e) {
            throw new HeadIOException();
        }
    }

    public static String getCurrentBranch() throws HeadIOException {
        try {
            return new String(Files.readAllBytes(HEAD));
        }
        catch (IOException e) {
            throw new HeadIOException();
        }
    }

    public static String getCurrentCommit() throws GitDoesNotExistException, HeadIOException, BranchIOException {
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException();

        Path branchLocation = buildPath(REFS_DIRECTORY, getCurrentBranch());
        try {
            return new String(Files.readAllBytes(branchLocation));
        } catch (IOException e) {
            throw new BranchIOException();
        }
    }


}
