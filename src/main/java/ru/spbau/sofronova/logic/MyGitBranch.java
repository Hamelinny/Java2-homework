package ru.spbau.sofronova.logic;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static ru.spbau.sofronova.logic.MyGitHead.*;
import static ru.spbau.sofronova.logic.MyGitUtils.*;

/**
 * A class that provides work with branches.
 */
public class MyGitBranch {

    /**
     * Method to create a branch with specified name.
     * @param name name of branch to create.
     * @param commitHash hash of commit to start new branch from it
     * @throws GitDoesNotExistException if git does not exist
     * @throws BranchAlreadyExistsException if branch with that name already exists
     * @throws BranchIOException if there are some IO problems during interaction with information about branches
     */
    static void createBranch(@NotNull String name, @NotNull String commitHash) throws GitDoesNotExistException,
            BranchAlreadyExistsException, BranchIOException {
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException();
        if (Files.exists(buildPath(REFS_DIRECTORY, name)))
            throw new BranchAlreadyExistsException();
        updateRefs(name, commitHash);
    }

    /**
     * Method to delete branch with specified name.
     * @param name name of branch to delete.
     * @throws BranchDeletionException if there are problems during deletion
     */
    static void deleteBranch(@NotNull String name) throws BranchDeletionException {
        try {
            Files.deleteIfExists(buildPath(REFS_DIRECTORY, name));
        } catch (IOException e) {
            throw new BranchDeletionException();
        }
    }

    /**
     * Method to update references after making a new commit in branch.
     * @param branch name of branch
     * @param commitHash hash of commit to refer to
     * @throws BranchIOException if there are problems during interaction with information
     */
    static void updateRefs(@NotNull String branch, @NotNull String commitHash) throws  BranchIOException {
        Path branchLoc = buildPath(REFS_DIRECTORY, branch);
        try {
            if (Files.notExists(branchLoc))
                Files.createFile(branchLoc);
            Files.write(branchLoc, commitHash.getBytes());
        }
        catch (IOException e) {
            throw new BranchIOException();
        }
    }

    /**
     * Method to switch from current branch to another.
     * @param branch name of this another branch
     * @throws BranchIOException if there are some problems during interaction with information about branches.
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws GitDoesNotExistException if git does not exist
     */
    static void checkoutBranch(@NotNull String branch) throws BranchIOException, HeadIOException,
            GitDoesNotExistException {
        try {
            String commitHash = getCurrentCommit();
            deleteFilesFromCommit(commitHash);
            String lastCommit = Files.lines(buildPath(REFS_DIRECTORY, branch)).findFirst().get();
            addFilesFromCommit(lastCommit);
            updateHead(branch);
        }
        catch (IOException e) {
            throw new BranchIOException();
        }
    }

    /**
     * Method that takes a commit and makes from it a new branch which name is hash of the commit.
     * @param hash hash of the commit
     * @throws GitDoesNotExistException if git does not exist
     * @throws BranchAlreadyExistsException if branch with that name already exists
     * @throws BranchIOException if there are some problems during interaction with information about branches.
     * @throws HeadIOException if there are problems during interaction with HEAD file
     */
    static void checkoutCommit(@NotNull String hash) throws GitDoesNotExistException, BranchAlreadyExistsException,
            BranchIOException, HeadIOException {
        createBranch(hash, hash);
        checkoutBranch(hash);
    }

    private static void addFilesFromCommit(@NotNull String commitHash) throws BranchIOException {
        try {
            String hashTree = Files.lines(buildPath(OBJECTS_DIRECTORY, commitHash)).findFirst().get();
            List<String> filesToRestore = Files.readAllLines(buildPath(OBJECTS_DIRECTORY, hashTree));
            for (int i = 0; i < filesToRestore.size(); i += 2) {
                Path blobPath = buildPath(OBJECTS_DIRECTORY, filesToRestore.get(i + 1));
                byte[] blobContent = Files.readAllBytes(blobPath);
                Files.write(Paths.get(filesToRestore.get(i)), blobContent);
            }
        }
        catch (IOException e) {
            throw new BranchIOException();
        }
    }

    private static void deleteFilesFromCommit(@NotNull String commitHash) throws BranchIOException {
        try {
            String hashTree = Files.lines(buildPath(OBJECTS_DIRECTORY, commitHash)).findFirst().get();
            List<String> filesToDelete = Files.readAllLines(buildPath(OBJECTS_DIRECTORY, hashTree));
            for (int i = 0; i < filesToDelete.size(); i += 2) {
                Files.delete(Paths.get(filesToDelete.get(i)));
            }
        }
        catch (IOException e) {
            throw new BranchIOException();
        }
    }
}
