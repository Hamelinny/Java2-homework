package ru.spbau.sofronova.logic;

import ru.spbau.sofronova.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static ru.spbau.sofronova.logic.MyGitHead.*;
import static ru.spbau.sofronova.logic.MyGitUtils.*;

public class MyGitBranch {

    static void createBranch(String name, String commitHash) throws GitDoesNotExistException,
            BranchAlreadyExistsException, BranchIOException {
        if (Files.notExists(GIT_DIRECTORY))
            throw new GitDoesNotExistException();
        if (Files.exists(buildPath(REFS_DIRECTORY, name)))
            throw new BranchAlreadyExistsException();
        updateRefs(name, commitHash);
    }

    static void deleteBranch(String name) throws BranchDeletionException {
        try {
            Files.deleteIfExists(buildPath(REFS_DIRECTORY, name));
        } catch (IOException e) {
            throw new BranchDeletionException();
        }
    }

    static void updateRefs(String branch, String commitHash) throws  BranchIOException {
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

    static void checkoutBranch(String branch) throws BranchIOException, HeadIOException,
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

    static void checkoutCommit(String hash) throws GitDoesNotExistException, BranchAlreadyExistsException,
            BranchIOException, HeadIOException {
        createBranch(hash, hash);
        checkoutBranch(hash);
    }

    private static void addFilesFromCommit(String commitHash) throws BranchIOException {
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

    private static void deleteFilesFromCommit(String commitHash) throws BranchIOException {
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
