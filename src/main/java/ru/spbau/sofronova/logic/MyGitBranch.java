package ru.spbau.sofronova.logic;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.entities.Branch;
import ru.spbau.sofronova.exceptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.spbau.sofronova.logic.MyGitUtils.*;

/**
 * A class that provides work with branches.
 */
public class MyGitBranch {

    private final MyGit repository;


    /**
     * Creates MyGitBranch from repository
     * @param repository repository
     */
    public MyGitBranch(@NotNull MyGit repository) {
        this.repository = repository;
    }

    /**
     * Method to create a branch with specified name.
     *
     * @param name       name of branch to create.
     * @param commitHash hash of commit to start new branch from it
     * @throws GitDoesNotExistException     if git does not exist
     * @throws BranchAlreadyExistsException if branch with that name already exists
     * @throws ObjectStoreException         if there are some IO problems during interaction with information about branches
     */
    public void createBranch(@NotNull String name, @NotNull String commitHash) throws GitDoesNotExistException,
            BranchAlreadyExistsException, ObjectStoreException, LogIOException {
        if (Files.notExists(repository.GIT_DIRECTORY))
            throw new GitDoesNotExistException("git does not exist\n");
        if (Files.exists(buildPath(repository.REFS_DIRECTORY, name)))
            throw new BranchAlreadyExistsException("branch already exists\n");
        Branch newBranch = new Branch(name, repository, commitHash);
        newBranch.storeObject();
        try {
            repository.getLogsManager().updateLog(name, Files.readAllLines(
                    buildPath(repository.OBJECTS_DIRECTORY, commitHash)).get(1) + "\n");
        } catch (IOException e) {
            throw new LogIOException("IO exception with log during branch creation");
        }

    }

    /**
     * Method to delete branch with specified name.
     *
     * @param name name of branch to delete.
     * @throws BranchDeletionException if there are problems during deletion
     */
    public void deleteBranch(@NotNull String name) throws BranchDeletionException {
        try {
            Files.deleteIfExists(buildPath(repository.REFS_DIRECTORY, name));
        } catch (IOException e) {
            throw new BranchDeletionException("cannot delete this branch");
        }
    }


    /**
     * Method to switch from current branch to another.
     *
     * @param branch name of this another branch
     * @throws BranchIOException        if there are some problems during interaction with information about branches.
     * @throws HeadIOException          if there are problems during interaction with HEAD file
     * @throws GitDoesNotExistException if git does not exist
     */
    public void checkoutBranch(@NotNull String branch) throws BranchIOException, HeadIOException,
            GitDoesNotExistException, IndexIOException, ObjectIOException, ObjectStoreException {
        try {
            MyGitHead headUpdater = new MyGitHead(repository);
            String commitHash = headUpdater.getCurrentCommit();
            deleteFilesFromCommit(commitHash);
            String lastCommit = Files.lines(buildPath(repository.REFS_DIRECTORY, branch)).findFirst().get();
            addFilesFromCommit(lastCommit);
            headUpdater.updateHead(branch);
        } catch (IOException e) {
            throw new BranchIOException("cannot checkout branch " + branch + "\n");
        }
    }

    /**
     * Method that takes a commit and makes from it a new branch which name is hash of the commit.
     *
     * @param hash hash of the commit
     * @throws GitDoesNotExistException     if git does not exist
     * @throws BranchAlreadyExistsException if branch with that name already exists
     * @throws BranchIOException            if there are some problems during interaction with information about branches.
     * @throws HeadIOException              if there are problems during interaction with HEAD file
     * @throws ObjectStoreException         if there are some problems during storing branch
     */
    public void checkoutCommit(@NotNull String hash) throws GitDoesNotExistException, BranchAlreadyExistsException,
            HeadIOException, ObjectStoreException, BranchIOException, LogIOException, IndexIOException, ObjectIOException {
        createBranch(hash, hash);
        checkoutBranch(hash);
    }

    private void addFilesFromCommit(@NotNull String commitHash) throws BranchIOException,
            ObjectStoreException, IndexIOException, ObjectIOException {
        try {
            String hashTree = Files.lines(buildPath(repository.OBJECTS_DIRECTORY, commitHash)).findFirst().get();
            List<String> filesToRestore = Files.readAllLines(buildPath(repository.OBJECTS_DIRECTORY, hashTree));
            List <Path> toIndex = new ArrayList<>();
            for (int i = 0; i < filesToRestore.size(); i += 2) {
                Path blobPath = buildPath(repository.OBJECTS_DIRECTORY, filesToRestore.get(i + 1));
                byte[] blobContent = Files.readAllBytes(blobPath);
                Files.write(Paths.get(filesToRestore.get(i)), blobContent);
                toIndex.add(Paths.get(filesToRestore.get(i)));
            }
            repository.getIndexManager().updateIndex(toIndex);
        } catch (IOException e) {
            throw new BranchIOException("cannot restore files from commit\n");
        }
    }

    private void deleteFilesFromCommit(@NotNull String commitHash) throws BranchIOException, IndexIOException {
        try {
            String hashTree = Files.lines(buildPath(repository.OBJECTS_DIRECTORY, commitHash)).findFirst().get();
            List<String> filesToDelete = Files.readAllLines(buildPath(repository.OBJECTS_DIRECTORY, hashTree));
            for (int i = 0; i < filesToDelete.size(); i += 2) {
                Files.delete(Paths.get(filesToDelete.get(i)));
            }
            repository.getIndexManager().cleanIndex();
        } catch (IOException e) {
            throw new BranchIOException("cannot delete files from commit\n");
        }
    }
}
