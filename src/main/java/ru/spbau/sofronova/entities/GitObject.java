package ru.spbau.sofronova.entities;

import com.sun.istack.internal.NotNull;
import ru.spbau.sofronova.exceptions.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * An abstract class which describes an object from git. It provides hash.
 */
abstract public class GitObject {

    @NotNull
    protected String hash;

    protected GitObject(@NotNull byte[] content) {
        hash = buildHash(content);
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
     * @throws ObjectAddException if there are some IO problems during add.
     */
    abstract public void addObject() throws ObjectAddException;

    /**
     * Method returns a hash of object.
     * @return hash of object.
     */
    public String getHash() {
        return hash;
    }

}
