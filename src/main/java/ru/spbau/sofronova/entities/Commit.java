package ru.spbau.sofronova.entities;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.ObjectStoreException;
import ru.spbau.sofronova.logic.MyGit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import static ru.spbau.sofronova.logic.MyGitUtils.buildPath;
import static ru.spbau.sofronova.logic.MyGitUtils.newLine;

/**
 * Object which corresponds a commit. It stores commit author, message, date of commit and hash of the tree.
 */
public class Commit extends GitObject {

    @NotNull
    private String message;
    @NotNull
    private String author;
    @NotNull
    private Date date;
    @NotNull
    private String treeHash;

    /**
     * Make a commit object from hash of the tree and message.
     * @param treeHash hash of the tree.
     * @param message commit message.
     * @param repository repository entity
     */
    public Commit(@NotNull String treeHash, @NotNull String message, @NotNull MyGit repository) {
        super((treeHash + message).getBytes(), repository);
        this.message = message;
        this.author = System.getProperty("user.name");
        this.date = new Date();
        this.treeHash = treeHash;
    }

    /**
     * Method which return an information about commit: author, message and date.
     * @return String with the information.
     */
    public String getInfo() {
        return author + ' ' + message + ' ' + date.toString() + '\n';
    }

    /**
     * Method to create a file in repository which name is commit hash and which content is hash of the tree.
     * @throws ObjectStoreException if there are some IO problems during add.
     */
    @Override
    public void storeObject() throws ObjectStoreException {
        try {
            Files.write(buildPath(repository.OBJECTS_DIRECTORY, hash), treeHash.getBytes());
            Files.write(buildPath(repository.OBJECTS_DIRECTORY, hash), newLine(), StandardOpenOption.APPEND);
            Files.write(buildPath(repository.OBJECTS_DIRECTORY, hash), getInfo().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new ObjectStoreException("cannot store commit\n");
        }
    }
}
