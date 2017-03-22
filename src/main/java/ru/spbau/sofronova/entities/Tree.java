package ru.spbau.sofronova.entities;

import com.sun.istack.internal.NotNull;
import ru.spbau.sofronova.exceptions.ObjectAddException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static ru.spbau.sofronova.logic.MyGitUtils.*;

/**
 * A class which for now is not a tree at all, but it contains names and hashes of files from commit.
 */
public class Tree extends GitObject {

    @NotNull
    private List <String> fileName;
    @NotNull
    private List <String> fileHash;

    /**
     * Makes a tree from list of names and list of hashes.
     * @param fileName names of files.
     * @param fileHash hashes of files.
     */
    public Tree(@NotNull List <String> fileName, @NotNull List <String> fileHash) {
        super(getContent(fileName, fileHash));
        this.fileHash = fileHash;
        this.fileName = fileName;
    }

    private static byte[] getContent(@NotNull List <String> fileName, @NotNull List <String> fileHash) {
        String allInfo = "";
        for (int i = 0; i < fileName.size(); i++) {
            allInfo += fileName.get(i) + fileHash.get(i) + '\n';
        }
        return allInfo.getBytes();
    }

    /**
     * Method to create a file in repository which name is tree hash and which content is names and hashes of files in commit.
     * @throws ObjectAddException if there are some IO problems during add.
     */
    @Override
    public void addObject() throws ObjectAddException {
        try {
            Path pathToTree = buildPath(OBJECTS_DIRECTORY, hash);
            Files.deleteIfExists(pathToTree);
            Files.createFile(pathToTree);
            for (int i = 0; i < fileName.size(); i++) {
                Files.write(pathToTree, fileName.get(i).getBytes(), StandardOpenOption.APPEND);
                Files.write(pathToTree, newLine(), StandardOpenOption.APPEND);
                Files.write(pathToTree, fileHash.get(i).getBytes(), StandardOpenOption.APPEND);
                Files.write(pathToTree, newLine(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ObjectAddException();
        }
    }
}
