package ru.spbau.sofronova.logic;


import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.io.FileUtils;
import ru.spbau.sofronova.entities.Blob;
import ru.spbau.sofronova.entities.Branch;
import ru.spbau.sofronova.entities.Commit;
import ru.spbau.sofronova.entities.Tree;
import ru.spbau.sofronova.exceptions.*;
import ru.spbau.sofronova.logger.MyGitLogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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


    private final Path loggerPath;
    private final MyGitHead headManager;
    private final MyGitIndex indexManager;
    private final MyGitBranch branchManager;
    private final MyGitLogs logsManager;
    private final Logger logger;

    /**
     * Creates a git repository in specified folder.
     * @param folder folder that will contain repository
     */
    public MyGit(@NotNull String folder) {
        GIT_DIRECTORY = buildPath(folder, ".MyGit");
        INDEX = buildPath(GIT_DIRECTORY, "index");
        OBJECTS_DIRECTORY = buildPath(GIT_DIRECTORY, "objects");
        REFS_DIRECTORY = buildPath(GIT_DIRECTORY, "refs");
        HEAD = buildPath(GIT_DIRECTORY, "HEAD");
        LOGS_DIRECTORY = buildPath(GIT_DIRECTORY, "logs");
        headManager = new MyGitHead(this);
        indexManager = new MyGitIndex(this);
        branchManager = new MyGitBranch(this);
        logsManager = new MyGitLogs(this);
        logger = MyGitLogBuilder.getLogger(Paths.get(folder));
        loggerPath = buildPath(folder, "myGitLogs");
    }

    /**
     * Method to get a Logger from MyGit.
     * @return Logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Method to get a branch manager
     * @return branch manager
     */
    public MyGitBranch getBranchManager() {
        return branchManager;
    }

    /**
     * Method to get a head manager
     * @return head manager
     */
    public MyGitHead getHeadManager() {
        return headManager;
    }

    /**
     * Method to get an index manager
     * @return index manager
     */
    public MyGitIndex getIndexManager() {
        return indexManager;
    }

    /**
     * Method to get a log manager
     * @return log manager
     */
    public MyGitLogs getLogsManager() {
        return logsManager;
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
        logger.trace("git initialization\n");
        if (Files.exists(GIT_DIRECTORY))
            throw new GitAlreadyInitializedException("git already initialized\n");
        try {
            makeDirs();
            Commit initial = commit("initial commit");
            getBranchManager().createBranch("master", initial.getHash());
            getHeadManager().updateHead("master");
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
        logger.trace("commit with \"" + message + "\" message\n");
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException("git does not exist, please perform \"init\" command\n");

        List<String> indexContent = getIndexManager().getCurrentIndexState();
        List<String> files = new ArrayList<>();
        List<String> hashes = new ArrayList<>();
        for (int i = 0; i < indexContent.size(); i += 2) {
            files.add(indexContent.get(i));
            hashes.add(indexContent.get(i + 1));
        }
        Tree tree = new Tree(files, hashes, this);
        tree.storeObject();
        Commit commit = new Commit(tree.getHash(), message, this);
        commit.storeObject();
        String currentBranchName = getHeadManager().getCurrentBranch();
        if (!currentBranchName.isEmpty()){
            Branch currentBranch = new Branch(currentBranchName, this, commit.getHash());
            currentBranch.storeObject();
            getLogsManager().updateLog(currentBranchName, commit.getInfo());
        }
        return commit;
    }

    /**
     * Method to make add file to INDEX.
     * @param files files we need to add to INDEX
     * @throws IndexIOException if there are problems during interaction with INDEX file
     * @throws GitDoesNotExistException if git does not exist
     */
    public void add(@NotNull List<Path> files) throws IndexIOException, GitDoesNotExistException,
            ObjectIOException, ObjectStoreException {
        logger.trace("add " + String.join(" ", files.stream().
                map(Path::toString).
                collect(Collectors.toList())) + "\n");
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException("git does not exist, please perform \"init\" command\n");
        getIndexManager().updateIndex(files.stream().filter(Files::exists).collect(Collectors.toList()));
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
            BranchAlreadyExistsException, HeadIOException, BranchIOException, ObjectStoreException,
            LogIOException, IndexIOException, ObjectIOException {
        logger.trace("checkout " + toCheckout + "\n");
        if (isHash(toCheckout))
            getBranchManager().checkoutCommit(toCheckout);
        else
            getBranchManager().checkoutBranch(toCheckout);
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
            GitDoesNotExistException, BranchAlreadyExistsException, ObjectStoreException,
            LogIOException {
        logger.trace("branch " + name + "\n");
        getBranchManager().createBranch(name, getHeadManager().getCurrentCommit());
    }

    /**
     * Method to delete a branch.
     * @param name name of branch to delete
     * @throws BranchDeletionException if there are problems during branch deletion
     */
    public void branchWithDOption(@NotNull String name) throws BranchDeletionException {
        logger.trace("delete branch " + name + "\n");
        getBranchManager().deleteBranch(name);
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
        logger.trace("merge " + branch + " into " + getHeadManager().getCurrentBranch() + "\n");
        if (Files.notExists(GIT_DIRECTORY)){
            throw new GitDoesNotExistException("git does not exist, please perform \"init\" command\n");
        }
        try {
            String currentBranch = getHeadManager().getCurrentBranch();
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
        logger.trace("log " + getHeadManager().getCurrentBranch() + "\n");
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException("git does not exist, please perform \"init\"\n");
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(getHeadManager().getCurrentBranch().getBytes());
            outputStream.write(newLine());
            outputStream.write(Files.readAllBytes(buildPath(LOGS_DIRECTORY, getHeadManager().getCurrentBranch())));
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new LogIOException("IO exception with log\n");
        }
    }


    /**
     * Method that removes a file from index and from disk
     * @param files files to remove
     * @throws IndexIOException if there are problems during interaction with INDEX file
     * @throws ObjectIOException if there are problems with objects IO
     * @throws ObjectStoreException if there are problems during object adding
     */
    public void rm(@NotNull List <String> files) throws IndexIOException, ObjectIOException,
            ObjectStoreException {
        logger.trace("rm " + String.join(" ", files) + "\n");
        for (String file : files) {
            deleteDirectory(new File(file));
            reset(Paths.get(file));
        }
    }

    /**
     * Method to remove untracked files from disk.
     * @throws IndexIOException if there are problems during interaction with INDEX file
     */
    public void clean() throws IndexIOException, ObjectIOException {
        logger.trace("clean\n");
        List<Path> paths = listFilesInDirectory(GIT_DIRECTORY.getParent());
        List<Path> pathsNotToDelete = listFilesInDirectory(GIT_DIRECTORY);
        pathsNotToDelete.addAll(listFilesInDirectory(loggerPath));
        paths.removeAll(pathsNotToDelete);
        List <String> fromIndex = getIndexManager().getCurrentIndexState();
        List <Path> pathsFromIndex = new ArrayList<>();
        for (int i = 0; i < fromIndex.size(); i += 2) {
            pathsFromIndex.add(Paths.get(fromIndex.get(i)));
        }
        paths.removeAll(pathsFromIndex);
        List<String> filesFromCommit = null;
        try {
            String hashTree = Files.lines(buildPath(OBJECTS_DIRECTORY, headManager.getCurrentCommit())).findFirst().get();
            filesFromCommit = Files.readAllLines(buildPath(OBJECTS_DIRECTORY, hashTree));
        } catch (Exception e) {
            throw new ObjectIOException("cannot get files from commit");
        }
        for (int i = 0; i < filesFromCommit.size(); i += 2) {
            Path path = Paths.get(filesFromCommit.get(i));
            String hash = filesFromCommit.get(i + 1);
            if (Files.notExists(path)) {
                paths.remove(path);
                continue;
            }
            if (!pathsFromIndex.contains(path) && Blob.getBlob(path, this).getHash().equals(hash))
                paths.remove(path);
        }
        for (Path path: paths) {
            deleteDirectory(new File(path.toString()));
        }
    }

    /**
     * Method to get a message with status which contains untracked, staged, modified and deleted files
     * @return string with that message
     * @throws IndexIOException if there are problems during interaction with INDEX file
     * @throws ObjectIOException if there are problems with objects IO
     */
    public String status() throws IndexIOException, ObjectIOException, BranchIOException, HeadIOException,
            GitDoesNotExistException {
        logger.trace("status\n");
        String answer = "";
        List <Path> modified = new ArrayList<>();
        List <Path> staged = new ArrayList<>();
        List <Path> deleted = new ArrayList<>();
        List<Path> untracked = listFilesInDirectory(GIT_DIRECTORY.getParent());

        List<Path> pathsNotToShow = listFilesInDirectory(GIT_DIRECTORY);
        pathsNotToShow.addAll(listFilesInDirectory(loggerPath));
        untracked.removeAll(pathsNotToShow);

        List <String> fromIndex = getIndexManager().getCurrentIndexState();
        List <Path> pathsToExamine = new ArrayList<>();
        List <String> hashes = new ArrayList<>();
        for (int i = 0; i < fromIndex.size(); i += 2) {
            pathsToExamine.add(Paths.get(fromIndex.get(i)));
            hashes.add(fromIndex.get(i + 1));
        }
        untracked.removeAll(pathsToExamine);
        List<String> filesFromCommit = null;
        try {
            String hashTree = Files.lines(buildPath(OBJECTS_DIRECTORY, headManager.getCurrentCommit())).findFirst().get();
            filesFromCommit = Files.readAllLines(buildPath(OBJECTS_DIRECTORY, hashTree));
        } catch (Exception e) {
            throw new ObjectIOException("cannot get files from commit");
        }
        List <Path> pathsFromCommit = new ArrayList<>();
        List <String> hashesFromCommit = new ArrayList<>();
        for (int i = 0; i < filesFromCommit.size(); i += 2) {
            Path path = Paths.get(filesFromCommit.get(i));
            String hash = filesFromCommit.get(i + 1);
            pathsFromCommit.add(path);
            hashesFromCommit.add(hash);
            if (Files.notExists(path)) {
                untracked.remove(path);
                continue;
            }
            if (pathsToExamine.contains(path))
                untracked.remove(path);
            if (!pathsToExamine.contains(path) && Blob.getBlob(path, this).getHash().equals(hash))
                untracked.remove(path);
        }
        for (int i = 0; i < pathsToExamine.size(); i++) {
            Path path = pathsToExamine.get(i);
            String hash = hashes.get(i);
            if (Files.notExists(path)) {
                deleted.add(path);
                continue;
            }
            int ind = pathsFromCommit.indexOf(path);
            if (Blob.getBlob(path, this).getHash().equals(hash) && (ind == -1 || !hashesFromCommit.get(ind).equals(hash)))
                staged.add(path);
            else if (!Blob.getBlob(path, this).getHash().equals(hash))
                modified.add(path);
        }
        staged = distinct(staged);
        modified = distinct(modified);
        deleted = distinct(deleted);
        untracked = distinct(untracked);

        if (!staged.isEmpty()) {
            answer += "staged:\n";
            for (Path path : staged) {
                answer += path.toString();
                answer += "\n";
            }
        }

        if (!modified.isEmpty()) {
            answer += "modified:\n";
            for (Path path : modified) {
                answer += path.toString();
                answer += "\n";
            }
        }

        if (!deleted.isEmpty()) {
            answer += "deleted:\n";
            for (Path path : deleted) {
                answer += path.toString();
                answer += "\n";
            }
        }

        if (!untracked.isEmpty()) {
            answer += "untracked:\n";
            for (Path path : untracked) {
                answer += path.toString();
                answer += "\n";
            }
        }

        answer += "current commit: " + getHeadManager().getCurrentCommit() + "\n";

        return answer;
    }

    /**
     * Method to delete file from INDEX
     * @param file file to delete
     * @throws IndexIOException if there are problems during interaction with INDEX file
     */
    public void reset(Path file) throws IndexIOException {
        logger.trace("reset " + file.toString() + "\n");
        List <String> index = getIndexManager().getCurrentIndexState();
        List <String> files = new ArrayList<>();
        List <String> hashes = new ArrayList<>();
        for (int i = 0; i < index.size(); i += 2) {
            files.add(index.get(i));
            hashes.add(index.get(i + 1));
        }
        int ind = index.indexOf(file.toString());
        if (ind == -1)
            return;
        files.remove(ind);
        hashes.remove(ind);
        getIndexManager().cleanIndex();;
        getIndexManager().updateIndex(files, hashes);
    }


    private List <Path> listFilesInDirectory(Path path) {
        return FileUtils.listFiles(path.toFile(), null, true)
                .stream()
                .map(File::toPath)
                .collect(Collectors.toList());
    }

    private void makeDirs() throws IOException {
        Files.createDirectory(GIT_DIRECTORY);
        Files.createDirectory(OBJECTS_DIRECTORY);
        Files.createDirectory(REFS_DIRECTORY);
        Files.createDirectory(LOGS_DIRECTORY);
        Files.createFile(INDEX);
        Files.createFile(HEAD);
    }

    private List<Path> distinct(List <Path> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }


}
