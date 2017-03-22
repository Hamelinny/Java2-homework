package ru.spbau.sofronova.logic;

import ru.spbau.sofronova.entities.Blob;
import ru.spbau.sofronova.entities.Commit;
import ru.spbau.sofronova.entities.Tree;
import ru.spbau.sofronova.exceptions.*;

import java.io.File;
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

public class MyGitCommands {

    public static void init() throws GitAlreadyInitedException, GitDoesNotExistException,
            BranchAlreadyExistsException, LogIOException, BranchIOException, IndexIOException,
            HeadIOException, ObjectAddException, ObjectIOException {
        if (Files.exists(GIT_DIRECTORY))
            throw new GitAlreadyInitedException();
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

    public static Commit commit(String message) throws IndexIOException, GitDoesNotExistException,
            LogIOException, HeadIOException, BranchIOException, ObjectAddException, ObjectIOException {
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException();

        List<String> indexContent = getCurrentIndexState();
        List<String> files = new ArrayList<>();
        List<String> hashes = new ArrayList<>();
        for (int i = 0; i < indexContent.size(); i++) {
            files.add(Paths.get(indexContent.get(i)).toString());
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

    public static void add(List<Path> files) throws IndexIOException, GitDoesNotExistException {
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException();
        updateIndex(files);
    }

    public static void checkout(String toCheckout) throws GitDoesNotExistException,
            BranchAlreadyExistsException, HeadIOException, BranchIOException {
        if (isHash(toCheckout))
            checkoutCommit(toCheckout);
        else
            checkoutBranch(toCheckout);
    }

    public static void branch(String name) throws BranchIOException, HeadIOException,
            GitDoesNotExistException, BranchAlreadyExistsException {
        createBranch(name, getCurrentCommit());
    }

    public static void branchWithDOption(String name) throws BranchDeletionException {
        deleteBranch(name);
    }

    public static void merge(String branch) throws GitDoesNotExistException, HeadIOException,
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
