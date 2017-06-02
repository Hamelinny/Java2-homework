package ru.spbau.sofronova.logic;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.entities.Blob;
import ru.spbau.sofronova.exceptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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

    public void updateIndex(@NotNull List <Path> files) throws IndexIOException, ObjectIOException,
            ObjectStoreException {
        List <String> indexContent = getCurrentIndexState();
        List <String> alreadyHave = new ArrayList<>();
        List <String> hashesOld = new ArrayList<>();
        for (int i = 0; i < indexContent.size(); i += 2) {
            alreadyHave.add(indexContent.get(i));
            hashesOld.add(indexContent.get(i + 1));
        }
        List <String> toAdd = files.stream().map(Path::toString).collect(Collectors.toList());
        alreadyHave.removeAll(toAdd);
        cleanIndex();
        for (int i = 0; i < alreadyHave.size(); i++) {
            try {
                Files.write(repository.INDEX, alreadyHave.get(i).getBytes(), StandardOpenOption.APPEND);
                Files.write(repository.INDEX, newLine(), StandardOpenOption.APPEND);
                Files.write(repository.INDEX, hashesOld.get(i).getBytes(),
                        StandardOpenOption.APPEND);
                Files.write(repository.INDEX, newLine(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new IndexIOException("cannot update INDEX\n");
            }
        }
        for (String line : toAdd) {
            try {
                Files.write(repository.INDEX, line.getBytes(), StandardOpenOption.APPEND);
                Files.write(repository.INDEX, newLine(), StandardOpenOption.APPEND);
                Blob blob = Blob.getBlob(Paths.get(line), repository);
                blob.storeObject();
                Files.write(repository.INDEX, blob.getHash().getBytes(),
                        StandardOpenOption.APPEND);
                Files.write(repository.INDEX, newLine(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new IndexIOException("cannot update INDEX\n");
            }
        }

    }

    /**
     * Method to update index with list of files and given list of hashes.
     * @param files list of files
     * @param hashes list of hashes
     */
    public void updateIndex(@NotNull List <String> files, @NotNull List <String> hashes) throws
            IndexIOException {
        try {
            for (int i = 0; i < files.size(); i++) {
                Files.write(repository.INDEX, files.get(i).getBytes(), StandardOpenOption.APPEND);
                Files.write(repository.INDEX, newLine(), StandardOpenOption.APPEND);
                Files.write(repository.INDEX, hashes.get(i).getBytes(), StandardOpenOption.APPEND);
                Files.write(repository.INDEX, newLine(), StandardOpenOption.APPEND);
            }
        }
        catch (IOException e) {
            throw new IndexIOException("cannot update INDEX\n");
        }
    }

    /**
     * Method to clean INDEX file.
     * @throws IndexIOException if there are IO problems during interaction with INDEX file
     */
    public void cleanIndex() throws IndexIOException {
        try {
            Files.delete(repository.INDEX);
            Files.createFile(repository.INDEX);
        }
        catch (IOException e) {
            throw new IndexIOException("cannot clean INDEX\n");
        }
    }

}
