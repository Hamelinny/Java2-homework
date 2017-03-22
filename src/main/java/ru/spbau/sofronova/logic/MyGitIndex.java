package ru.spbau.sofronova.logic;


import ru.spbau.sofronova.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static ru.spbau.sofronova.logic.MyGitUtils.*;

public class MyGitIndex {

    public static List<String> getCurrentIndexState() throws IndexIOException {
        try {
            return Files.readAllLines(INDEX);
        }
        catch (IOException e) {
            throw new IndexIOException();
        }
    }

    static void updateIndex(List <Path> files) throws IndexIOException {
        List <String> indexContent = getCurrentIndexState();
        List <String> toAdd = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            toAdd.add(files.get(i).toString());
        }
        indexContent.removeAll(toAdd);
        indexContent.addAll(toAdd);
        for (String line : indexContent) {
            try {
                Files.write(INDEX, line.getBytes(), StandardOpenOption.APPEND);
                Files.write(INDEX, newLine(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new IndexIOException();
            }
        }

    }

    public static void cleanIndex() throws IndexIOException {
        try {
            Files.delete(INDEX);
            Files.createFile(INDEX);
        }
        catch (IOException e) {
            throw new IndexIOException();
        }
    }

}
