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

    private final MyGit repository;

    /**
     * Create an index interactor from repository
     * @param repository repository entity
     */
    public MyGitIndex(@NotNull MyGit repository) {
        this.repository = repository;
    }

    /**
     * Method to get a list of files tracking in INDEX
     * @return list of tracking files
     * @throws IndexIOException if there are IO problems during interaction with INDEX
     */
    public List<String> getCurrentIndexState() throws IndexIOException {
        try {
            return Files.readAllLines(repository.INDEX);
        }
        catch (IOException e) {
            throw new IndexIOException("cannot get index state\n");
        }
    }

    /**
     * Method to update INDEX with list of files to add.
     * @param files files to add to INDEX
     * @throws IndexIOException if there are IO problems during interaction with INDEX
     */
    void updateIndex(@NotNull List <Path> files) throws IndexIOException {
        List <String> indexContent = getCurrentIndexState();
        List <String> toAdd = files.stream().map(Path::toString).collect(Collectors.toList());
        indexContent.addAll(toAdd);
        List <String> toWrite = indexContent.stream().distinct().collect(Collectors.toList());
        for (String line : toWrite) {
            try {
                Files.write(repository.INDEX, line.getBytes(), StandardOpenOption.APPEND);
                Files.write(repository.INDEX, newLine(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new IndexIOException("cannot update INDEX\n");
            }
        }

    }

    /**
     * Method to clean INDEX file.
     * @throws IndexIOException if there are IO problems during interaction with INDEX file
     */
    void cleanIndex() throws IndexIOException {
        try {
            Files.delete(repository.INDEX);
            Files.createFile(repository.INDEX);
        }
        catch (IOException e) {
            throw new IndexIOException("cannot clean INDEX\n");
        }
    }

}
