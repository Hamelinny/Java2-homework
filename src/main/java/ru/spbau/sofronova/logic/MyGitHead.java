package ru.spbau.sofronova.logic;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ru.spbau.sofronova.logic.MyGitUtils.*;

/**
 * A class provides interaction with HEAD file.
 */
public class MyGitHead {

    private final MyGit repository;

    /**
     * Create head interactor from repository.
     * @param repository repository entity
     */
    public MyGitHead(@NotNull MyGit repository) {
        this.repository = repository;
    }

    /**
     * Method to update name of current branch in HEAD file
     * @param currentBranch name of current branch
     * @throws HeadIOException if there are IO problems during interaction with HEAD file
     */
    public void updateHead(@NotNull String currentBranch) throws HeadIOException {
        try {
            Files.delete(repository.HEAD);
            Files.write(repository.HEAD, currentBranch.getBytes());
        }
        catch (IOException e) {
            throw new HeadIOException("IO exception during updating HEAD\n");
        }
    }

    /**
     * Method to get a name of a current branch from HEAD file.
     * @return name of a current branch
     * @throws HeadIOException if there are IO problems during interaction with HEAD file
     */
    public String getCurrentBranch() throws HeadIOException {
        try {
            return new String(Files.readAllBytes(repository.HEAD));
        }
        catch (IOException e) {
            throw new HeadIOException("cannot get current branch\n");
        }
    }

    /**
     * Method to get a hash of a current commit.
     * @return hash of a current commit
     * @throws GitDoesNotExistException if git does not exist
     * @throws HeadIOException if there are IO problems during interaction with HEAD file
     * @throws BranchIOException if there are IO problems during interaction with information about branches
     */
    public String getCurrentCommit() throws GitDoesNotExistException, HeadIOException, BranchIOException {
        if (Files.notExists(repository.GIT_DIRECTORY))
            throw new GitDoesNotExistException("git does not exist\n");

        Path branchLocation = buildPath(repository.REFS_DIRECTORY, getCurrentBranch());
        try {
            return new String(Files.readAllBytes(branchLocation));
        } catch (IOException e) {
            throw new BranchIOException("cannot get current commit\n");
        }
    }
}
