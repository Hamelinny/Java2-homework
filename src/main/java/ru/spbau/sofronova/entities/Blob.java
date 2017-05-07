package ru.spbau.sofronova.entities;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.ObjectStoreException;
import ru.spbau.sofronova.exceptions.ObjectIOException;
import ru.spbau.sofronova.logic.MyGit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ru.spbau.sofronova.logic.MyGitUtils.*;

/**
 * Object which corresponds to a file. It stores a content from file.
 */
public class Blob extends GitObject {

    @NotNull
    private byte[] content;

    private Blob(@NotNull byte[] nameAndContent, @NotNull byte[] content, @NotNull MyGit repository) {
        super(nameAndContent, repository);
        this.content = content;
    }

    /** This method creates a file in repository which name is a hash from blob.
     * This file stores content from blob.
     * @throws ObjectStoreException if there are some IO problems during add.
     */
    @Override
    public void storeObject() throws ObjectStoreException {
        try {
            Files.write(buildPath(repository.OBJECTS_DIRECTORY, hash), content);
        } catch (IOException e) {
            throw new ObjectStoreException("cannot store blob\n");
        }
    }

    /**
     * Method which purpose is creating a blob from path to file.
     * @param file path to file
     * @param repository repository entity
     * @return blob with content from file
     * @throws ObjectIOException if there are some problems with file IO
     */
    public static Blob getBlob(@NotNull Path file, @NotNull MyGit repository) throws ObjectIOException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(file.toString().getBytes());
            outputStream.write(Files.readAllBytes(file));

            return new Blob(outputStream.toByteArray(), Files.readAllBytes(file), repository);
        } catch (IOException e) {
            throw new ObjectIOException("cannot get blob from file\n");
        }
    }

}
