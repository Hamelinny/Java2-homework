package ru.spbau.sofronova.entities;


import ru.spbau.sofronova.exceptions.ObjectAddException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static ru.spbau.sofronova.logic.MyGitUtils.*;

public class Tree extends GitObject {

    List <String> fileName;
    List <String> fileHash;

    public Tree(List <String> fileName, List <String> fileHash) {
        super(getContent(fileName, fileHash));

        this.fileHash = fileHash;
        this.fileName = fileName;
    }

    private static byte[] getContent(List <String> fileName, List <String> fileHash) {
        String allInfo = "";
        for (int i = 0; i < fileName.size(); i++) {
            allInfo += fileName.get(i) + fileHash.get(i) + '\n';
        }
        return allInfo.getBytes();
    }

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
