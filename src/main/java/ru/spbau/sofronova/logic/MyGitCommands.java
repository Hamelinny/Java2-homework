package ru.spbau.sofronova.logic;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.entities.Blob;
import ru.spbau.sofronova.entities.Commit;
import ru.spbau.sofronova.entities.Tree;
import ru.spbau.sofronova.exceptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ru.spbau.sofronova.entities.Blob.getBlob;
import static ru.spbau.sofronova.logic.MyGitBranch.*;
import static ru.spbau.sofronova.logic.MyGitHead.*;
import static ru.spbau.sofronova.logic.MyGitIndex.*;
import static ru.spbau.sofronova.logic.MyGitLogs.*;
import static ru.spbau.sofronova.logic.MyGitUtils.*;

/**
 * A class which provides basic commands from git.
 */
public class MyGitCommands {

    /**
     * Method to initialize git.
     * @throws GitAlreadyInitializedException if git is already initialized
     * @throws GitDoesNotExistException if git does not exists then we make a branch
     * @throws BranchAlreadyExistsException if branch is already exist
     * @throws LogIOException if there are problems during log IO
     * @throws BranchIOException if there are problems during interaction with info about branch
     * @throws IndexIOException if there are problems during interaction with INDEX file
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws ObjectAddException if there are problems during adding some object in git
     * @throws ObjectIOException if there are problems during object IO
     */
    public static void init() throws GitAlreadyInitializedException, GitDoesNotExistException,
            BranchAlreadyExistsException, LogIOException, BranchIOException, IndexIOException,
            HeadIOException, ObjectAddException, ObjectIOException {
        if (Files.exists(GIT_DIRECTORY))
            throw new GitAlreadyInitializedException();
        try {
            makeDirs();
            Commit initial = commit("initial commit");
            createBranch("master", initial.getHash());
            updateLog("master", initial.getInfo());
            updateHead("master");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to make a commit.
     * @param message commit message
     * @return Commit object corresponding to commit we made
     * @throws IndexIOException if there are problems during interaction with INDEX file
     * @throws GitDoesNotExistException if git does not exist
     * @throws LogIOException if there are problems during log IO
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws BranchIOException if there are problems during interaction with info about branches
     * @throws ObjectAddException if there are problems during adding object to git
     * @throws ObjectIOException if there are problems with object IO
     */
    public static Commit commit(@NotNull String message) throws IndexIOException, GitDoesNotExistException,
            LogIOException, HeadIOException, BranchIOException, ObjectAddException, ObjectIOException {
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException();

        List<String> indexContent = getCurrentIndexState();
        List<String> files = new ArrayList<>();
        List<String> hashes = new ArrayList<>();
        for (String anIndexContent : indexContent) {
            files.add(Paths.get(anIndexContent).toString());
        }

        for (String file : files) {
            Blob blob = getBlob(Paths.get(file));
            blob.addObject();
            hashes.add(blob.getHash());
        }
        Tree tree = new Tree(files, hashes);
        tree.addObject();
        Commit commit = new Commit(tree.getHash(), message);
        commit.addObject();
        String currentBranchName = getCurrentBranch();
        if (!currentBranchName.equals("")){
            updateRefs(currentBranchName, commit.getHash());
            updateLog(currentBranchName, commit.getInfo());
        }
        cleanIndex();
        return commit;
    }

    /**
     * Method to make add file to INDEX.
     * @param files files we need to add to INDEX
     * @throws IndexIOException if there are problems during interaction with INDEX file
     * @throws GitDoesNotExistException if git does not exist
     */
    public static void add(@NotNull List<Path> files) throws IndexIOException, GitDoesNotExistException {
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException();
        updateIndex(files);
    }

    /**
     * Method to make checkout to branch or commit.
     * @param toCheckout name of branch or hash of commit
     * @throws GitDoesNotExistException if git does not exist
     * @throws BranchAlreadyExistsException if branch we want to make from commit already exists
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws BranchIOException if there are problems during interaction with info about branches
     */
    public static void checkout(@NotNull String toCheckout) throws GitDoesNotExistException,
            BranchAlreadyExistsException, HeadIOException, BranchIOException {
        if (isHash(toCheckout))
            checkoutCommit(toCheckout);
        else
            checkoutBranch(toCheckout);
    }

    /**
     * Method to make a new branch with specified name.
     * @param name name of branch to make
     * @throws BranchIOException if there are problems during interaction with info about branches
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws GitDoesNotExistException if git does not exist
     * @throws BranchAlreadyExistsException if branch we want to make already exists
     */
    public static void branch(@NotNull String name) throws BranchIOException, HeadIOException,
            GitDoesNotExistException, BranchAlreadyExistsException {
        createBranch(name, getCurrentCommit());
    }

    /**
     * Method to delete a branch.
     * @param name name of branch to delete
     * @throws BranchDeletionException if there are problems during branch deletion
     */
    public static void branchWithDOption(@NotNull String name) throws BranchDeletionException {
        deleteBranch(name);
    }

    /**
     * Method to merge branch with specified name into current branch.
     * @param branch name of branch to merge
     * @throws GitDoesNotExistException if git does not exist
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws LogIOException if there are problems during interaction with log
     * @throws IndexIOException if there are problems during interaction with INDEX file
     * @throws ObjectAddException if there are problems during object adding
     * @throws BranchIOException if there are problems during interaction with information about branch
     * @throws MergeIOException if there are IO problems during merge
     * @throws ObjectIOException if there are problems with objects IO
     */
    public static void merge(@NotNull String branch) throws GitDoesNotExistException, HeadIOException,
            LogIOException, IndexIOException, ObjectAddException, BranchIOException, MergeIOException, ObjectIOException {
        if (Files.notExists(GIT_DIRECTORY)){
            throw new GitDoesNotExistException();
        }
        try {
            String currentBranch = getCurrentBranch();
            Path branchToMerge = buildPath(REFS_DIRECTORY, branch);
            List<String> filesAndHashes = null;
            Path pathToCommit = buildPath(OBJECTS_DIRECTORY, Files.lines(branchToMerge).findFirst().get());
            Path pathToTree = buildPath(OBJECTS_DIRECTORY, Files.lines(pathToCommit).findFirst().get());
            filesAndHashes = Files.readAllLines(pathToTree);
            List<Path> filesToAdd = new ArrayList<>();
            for (int i = 0; i < (filesAndHashes == null ? 0 : filesAndHashes.size()); i += 2) {
                Path path = Paths.get(filesAndHashes.get(i));
                filesToAdd.add(path);
                String hash = filesAndHashes.get(i + 1);
                byte[] content = Files.readAllBytes(buildPath(OBJECTS_DIRECTORY, hash));
                Files.write(path, content);
            }
            add(filesToAdd);
            commit("merge " + branch + " into " + currentBranch);
        }
        catch (IOException e) {
            throw new MergeIOException();
        }
    }

    /**
     * Method returns a log for current branch.
     * @return log content
     * @throws GitDoesNotExistException if git does not exist
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws LogIOException if there are problems during log IO
     */
    public static byte[] log() throws GitDoesNotExistException, HeadIOException, LogIOException {
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException();
        try {
            return Files.readAllBytes(buildPath(LOGS_DIRECTORY, getCurrentBranch()));
        } catch (IOException e) {
            throw new LogIOException();
        }
    }
}
