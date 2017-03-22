package ru.spbau.sofronova.entities;


import com.sun.istack.internal.NotNull;
import ru.spbau.sofronova.exceptions.ObjectAddException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import static ru.spbau.sofronova.logic.MyGitUtils.OBJECTS_DIRECTORY;
import static ru.spbau.sofronova.logic.MyGitUtils.buildPath;

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
     */
    public Commit(@NotNull String treeHash, @NotNull String message) {
        super((treeHash + message).getBytes());
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
     * @throws ObjectAddException if there are some IO problems during add.
     */
    @Override
    public void addObject() throws ObjectAddException {
        try {
            Files.write(buildPath(OBJECTS_DIRECTORY, hash), treeHash.getBytes());
        } catch (IOException e) {
            throw new ObjectAddException();
        }
    }
}
