package ru.spbau.sofronova.logic;


import com.sun.istack.internal.NotNull;
import ru.spbau.sofronova.exceptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ru.spbau.sofronova.logic.MyGitUtils.*;

/**
 * A class provides interaction with HEAD file.
 */
public class MyGitHead {

    /**
     * Method to update name of current branch in HEAD file
     * @param currentBranch name of current branch
     * @throws HeadIOException if there are IO problems during interaction with HEAD file
     */
    static void updateHead(@NotNull String currentBranch) throws HeadIOException {
        try {
            Files.write(HEAD, currentBranch.getBytes());
        }
        catch (IOException e) {
            throw new HeadIOException();
        }
    }

    /**
     * Method to get a name of a current branch from HEAD file.
     * @return name of a current branch
     * @throws HeadIOException if there are IO problems during interaction with HEAD file
     */
    public static String getCurrentBranch() throws HeadIOException {
        try {
            return new String(Files.readAllBytes(HEAD));
        }
        catch (IOException e) {
            throw new HeadIOException();
        }
    }

    /**
     * Method to get a hash of a current commit.
     * @return hash of a current commit
     * @throws GitDoesNotExistException if git does not exist
     * @throws HeadIOException if there are IO problems during interaction with HEAD file
     * @throws BranchIOException if there are IO problems during interaction with information about branches
     */
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
