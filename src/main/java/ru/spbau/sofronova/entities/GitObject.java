package ru.spbau.sofronova.entities;

import ru.spbau.sofronova.exceptions.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *An abstract class which describes an object from git. It provides hash and
 */
abstract public class GitObject {

    protected String hash;

    protected GitObject(byte[] content) {
        hash = buildHash(content);
    }

    private String buildHash(byte[] content){
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

    abstract public void addObject() throws ObjectAddException;

    public String getHash() {
        return hash;
    }

}
