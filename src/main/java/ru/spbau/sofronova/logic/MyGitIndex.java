package ru.spbau.sofronova.logic;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import static ru.spbau.sofronova.logic.MyGitUtils.*;

/**
 * A class provides interaction with INDEX file.
 */
public class MyGitIndex {

    /**
     * Method to get a list of files tracking in INDEX
     * @return list of tracking files
     * @throws IndexIOException if there are IO problems during interaction with INDEX
     */
    public static List<String> getCurrentIndexState() throws IndexIOException {
        try {
            return Files.readAllLines(INDEX);
        }
        catch (IOException e) {
            throw new IndexIOException();
        }
    }

    /**
     * Method to update INDEX with list of files to add.
     * @param files files to add to INDEX
     * @throws IndexIOException if there are IO problems during interaction with INDEX
     */
    static void updateIndex(@NotNull List <Path> files) throws IndexIOException {
        List <String> indexContent = getCurrentIndexState();
        List <String> toAdd = files.stream().map(Path::toString).collect(Collectors.toList());
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

    /**
     * Method to clean INDEX file.
     * @throws IndexIOException if there are IO problems during interaction with INDEX file
     */
    static void cleanIndex() throws IndexIOException {
        try {
            Files.delete(INDEX);
            Files.createFile(INDEX);
        }
        catch (IOException e) {
            throw new IndexIOException();
        }
    }

}
