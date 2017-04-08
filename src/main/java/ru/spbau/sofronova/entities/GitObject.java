package ru.spbau.sofronova.entities;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.*;
import ru.spbau.sofronova.logic.MyGit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * An abstract class which describes an object from git. It provides hash.
 */
abstract public class GitObject {

    @NotNull
    protected String hash;
    protected final MyGit repository;

    protected GitObject(@NotNull byte[] content, @NotNull MyGit repository) {
        hash = buildHash(content);
        this.repository = repository;
    }

    private String buildHash(@NotNull byte[] content){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        if (digest == null)
            return "";
        digest.update(content);
        return javax.xml.bind.DatatypeConverter.printHexBinary(digest.digest()).toLowerCase();

    }

    /**
     * An abstract method which makes all of the inherits to be able add itself to git.
     * @throws ObjectStoreException if there are some IO problems during add.
     */
    abstract public void storeObject() throws ObjectStoreException;

    /**
     * Method returns a hash of object.
     * @return hash of object.
     */
    public String getHash() {
        return hash;
    }

}
