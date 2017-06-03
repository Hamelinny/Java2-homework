package ru.spbau.sofronova.exceptions;

/**
 * An exception to detect problems during adding git object in repository.
 */
public class ObjectStoreException extends Exception {
    public ObjectStoreException(String msg) {
        super(msg);
    }
}
