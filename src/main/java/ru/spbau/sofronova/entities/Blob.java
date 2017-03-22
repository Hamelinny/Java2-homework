package ru.spbau.sofronova.entities;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.ObjectAddException;
import ru.spbau.sofronova.exceptions.ObjectIOException;

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

    private Blob(@NotNull byte[] content) {
        super(content);
        this.content = content;
    }

    /** This method creates a file in repository which name is a hash from blob.
     * This file stores content from blob.
     * @throws ObjectAddException if there are some IO problems during add.
     */
    @Override
    public void addObject() throws ObjectAddException {
        try {
            Files.write(buildPath(OBJECTS_DIRECTORY, hash), content);
        } catch (IOException e) {
            throw new ObjectAddException();
        }
    }

    /**
     * Method which purpose is creating a blob from path to file.
     * @param file path to file
     * @return blob with content from file
     * @throws ObjectIOException if there are some problems with file IO
     */
    public static Blob getBlob(@NotNull Path file) throws ObjectIOException {
        try {
            return new Blob(Files.readAllBytes(file));
        } catch (IOException e) {
            throw new ObjectIOException();
        }
    }

}
