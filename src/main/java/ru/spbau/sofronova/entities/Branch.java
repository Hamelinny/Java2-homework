package ru.spbau.sofronova.entities;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.*;
import ru.spbau.sofronova.logic.MyGit;
import ru.spbau.sofronova.logic.MyGitHead;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static ru.spbau.sofronova.logic.MyGitUtils.*;

/**
 * Object which corresponds to a branch. It stores name and hash of last commit.
 */
public class Branch extends GitObject {

    private final String name;

    private String lastCommit;

    /**
     * Make a branch with specified name.
     * @param name name of branch
     * @param repository repository which branch belongs to
     * @param commit hash of last commit
     */
    public Branch(@NotNull String name, @NotNull MyGit repository, @NotNull String commit) {
        super(name.getBytes(), repository);
        this.name = name;
        this.lastCommit = commit;
    }

    /**
     * Set new last commit
     * @param hash hash of commit
     */
    public void setLastCommit(@NotNull String hash) {
        lastCommit = hash;
    }

    /**
     * This method create a file in repository which name is a branch name, content is a hash of last commit
     * @throws ObjectStoreException
     */
    @Override
    public void storeObject() throws ObjectStoreException {
        Path branchLoc = buildPath(repository.REFS_DIRECTORY, name);
        try {
            if (Files.notExists(branchLoc))
                Files.createFile(branchLoc);
            Files.write(branchLoc, lastCommit.getBytes());
        }
        catch (IOException e) {
            throw new ObjectStoreException("cannot change branch " + name + "\n");
        }
    }



}
