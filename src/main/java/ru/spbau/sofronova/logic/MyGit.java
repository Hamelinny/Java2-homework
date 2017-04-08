package ru.spbau.sofronova.logic;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.entities.Blob;
import ru.spbau.sofronova.entities.Branch;
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
import static ru.spbau.sofronova.logic.MyGitUtils.*;

/**
 * A class which provides basic commands from git.
 */
public class MyGit {

    /**
     * A constant stores a path to repository.
     */
    public final Path GIT_DIRECTORY;
    /**
     * A constant stores a path to index.
     */
    public final Path INDEX;
    /**
     * A constant stores a path to git objects.
     */
    public final Path OBJECTS_DIRECTORY;
    /**
     * A constant stores a path to references.
     */
    public final Path REFS_DIRECTORY;
    /**
     * A constant stores a path to head.
     */
    public final Path HEAD;
    /**
     * A constant stores a path to log files.
     */
    public final Path LOGS_DIRECTORY;

    private final MyGitHead headManager;
    private final MyGitIndex indexManager;
    private final MyGitBranch branchManager;
    private final MyGitLogs logsManager;

    /**
     * Creates a git repository in specified folder.
     * @param folder folder that will contain repository
     */
    public MyGit(@NotNull String folder) {
        GIT_DIRECTORY = buildPath(folder, ".git");
        INDEX = buildPath(GIT_DIRECTORY, "index");
        OBJECTS_DIRECTORY = buildPath(GIT_DIRECTORY, "objects");
        REFS_DIRECTORY = buildPath(GIT_DIRECTORY, "refs");
        HEAD = buildPath(GIT_DIRECTORY, "HEAD");
        LOGS_DIRECTORY = buildPath(GIT_DIRECTORY, "logs");
        headManager = new MyGitHead(this);
        indexManager = new MyGitIndex(this);
        branchManager = new MyGitBranch(this);
        logsManager = new MyGitLogs(this);
    }

    /**
     * Method to initialize git.
     * @throws GitAlreadyInitializedException if git is already initialized
     * @throws GitDoesNotExistException if git does not exists then we make a branch
     * @throws BranchAlreadyExistsException if branch is already exist
     * @throws LogIOException if there are problems during log IO
     * @throws BranchIOException if there are problems during interaction with info about branch
     * @throws IndexIOException if there are problems during interaction with INDEX file
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws ObjectStoreException if there are problems during adding some object in git
     * @throws ObjectIOException if there are problems during object IO
     */
    public void init() throws GitAlreadyInitializedException, GitDoesNotExistException,
            BranchAlreadyExistsException, LogIOException, BranchIOException, IndexIOException,
            HeadIOException, ObjectStoreException, ObjectIOException, InitIOException {
        if (Files.exists(GIT_DIRECTORY))
            throw new GitAlreadyInitializedException("git already initialized\n");
        try {
            makeDirs();
            Commit initial = commit("initial commit");
            branchManager.createBranch("master", initial.getHash());
            logsManager.updateLog("master", initial.getInfo());
            headManager.updateHead("master");
        } catch (IOException e) {
            throw new InitIOException("IO problem during git initialization\n");
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
     * @throws ObjectStoreException if there are problems during adding object to git
     * @throws ObjectIOException if there are problems with object IO
     */
    public Commit commit(@NotNull String message) throws IndexIOException, GitDoesNotExistException,
            LogIOException, HeadIOException, BranchIOException, ObjectStoreException, ObjectIOException {
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException("git does not exist\n");

        List<String> indexContent = indexManager.getCurrentIndexState();
        List<String> files = new ArrayList<>();
        List<String> hashes = new ArrayList<>();
        for (String anIndexContent : indexContent) {
            files.add(Paths.get(anIndexContent).toString());
        }

        for (String file : files) {
            Blob blob = getBlob(Paths.get(file), this);
            blob.storeObject();
            hashes.add(blob.getHash());
        }
        Tree tree = new Tree(files, hashes, this);
        tree.storeObject();
        Commit commit = new Commit(tree.getHash(), message, this);
        commit.storeObject();
        String currentBranchName = headManager.getCurrentBranch();
        if (!currentBranchName.isEmpty()){
            Branch currentBranch = new Branch(currentBranchName, this, commit.getHash());
            currentBranch.storeObject();
            logsManager.updateLog(currentBranchName, commit.getInfo());
        }
        indexManager.cleanIndex();
        return commit;
    }

    /**
     * Method to make add file to INDEX.
     * @param files files we need to add to INDEX
     * @throws IndexIOException if there are problems during interaction with INDEX file
     * @throws GitDoesNotExistException if git does not exist
     */
    public void add(@NotNull List<Path> files) throws IndexIOException, GitDoesNotExistException {
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException("git does not exist\n");
        indexManager.updateIndex(files);
    }

    /**
     * Method to make checkout to branch or commit.
     * @param toCheckout name of branch or hash of commit
     * @throws GitDoesNotExistException if git does not exist
     * @throws BranchAlreadyExistsException if branch we want to make from commit already exists
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws BranchIOException if there are problems during interaction with info about branches
     */
    public void checkout(@NotNull String toCheckout) throws GitDoesNotExistException,
            BranchAlreadyExistsException, HeadIOException, BranchIOException, ObjectStoreException {
        if (isHash(toCheckout))
            branchManager.checkoutCommit(toCheckout);
        else
            branchManager.checkoutBranch(toCheckout);
    }

    /**
     * Method to make a new branch with specified name.
     * @param name name of branch to make
     * @throws BranchIOException if there are problems during interaction with info about branches
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws GitDoesNotExistException if git does not exist
     * @throws BranchAlreadyExistsException if branch we want to make already exists
     */
    public void branch(@NotNull String name) throws BranchIOException, HeadIOException,
            GitDoesNotExistException, BranchAlreadyExistsException, ObjectStoreException {
        branchManager.createBranch(name, headManager.getCurrentCommit());
    }

    /**
     * Method to delete a branch.
     * @param name name of branch to delete
     * @throws BranchDeletionException if there are problems during branch deletion
     */
    public void branchWithDOption(@NotNull String name) throws BranchDeletionException {
        branchManager.deleteBranch(name);
    }

    /**
     * Method to merge branch with specified name into current branch.
     * @param branch name of branch to merge
     * @throws GitDoesNotExistException if git does not exist
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws LogIOException if there are problems during interaction with log
     * @throws IndexIOException if there are problems during interaction with INDEX file
     * @throws ObjectStoreException if there are problems during object adding
     * @throws BranchIOException if there are problems during interaction with information about branch
     * @throws MergeIOException if there are IO problems during merge
     * @throws ObjectIOException if there are problems with objects IO
     */
    public void merge(@NotNull String branch) throws GitDoesNotExistException, HeadIOException,
            LogIOException, IndexIOException, ObjectStoreException, BranchIOException, MergeIOException, ObjectIOException {
        if (Files.notExists(GIT_DIRECTORY)){
            throw new GitDoesNotExistException("git does not exist\n");
        }
        try {
            String currentBranch = headManager.getCurrentBranch();
            Path branchToMerge = buildPath(REFS_DIRECTORY, branch);
            Path pathToCommit = buildPath(OBJECTS_DIRECTORY, Files.lines(branchToMerge).findFirst().get());
            Path pathToTree = buildPath(OBJECTS_DIRECTORY, Files.lines(pathToCommit).findFirst().get());
            List<String> filesAndHashes = Files.readAllLines(pathToTree);
            List<Path> filesToAdd = new ArrayList<>();
            if (filesAndHashes != null) {
                for (int i = 0; i < filesAndHashes.size(); i += 2) {
                    Path path = Paths.get(filesAndHashes.get(i));
                    filesToAdd.add(path);
                    String hash = filesAndHashes.get(i + 1);
                    byte[] content = Files.readAllBytes(buildPath(OBJECTS_DIRECTORY, hash));
                    Files.write(path, content);
                }
            }
            add(filesToAdd);
            commit("merge " + branch + " into " + currentBranch);
        }
        catch (IOException e) {
            throw new MergeIOException("IO exception during merge\n");
        }
    }

    /**
     * Method returns a log for current branch.
     * @return log content
     * @throws GitDoesNotExistException if git does not exist
     * @throws HeadIOException if there are problems during interaction with HEAD file
     * @throws LogIOException if there are problems during log IO
     */
    public byte[] log() throws GitDoesNotExistException, HeadIOException, LogIOException {
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException("git does not exist\n");
        try {
            return Files.readAllBytes(buildPath(LOGS_DIRECTORY, headManager.getCurrentBranch()));
        } catch (IOException e) {
            throw new LogIOException("IO exception with log\n");
        }
    }


    private void makeDirs() throws IOException {
        Files.createDirectory(GIT_DIRECTORY);
        Files.createDirectory(OBJECTS_DIRECTORY);
        Files.createDirectory(REFS_DIRECTORY);
        Files.createDirectory(LOGS_DIRECTORY);
        Files.createFile(INDEX);
        Files.createFile(HEAD);
    }

}
