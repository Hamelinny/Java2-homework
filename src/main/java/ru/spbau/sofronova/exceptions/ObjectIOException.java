package ru.spbau.sofronova.exceptions;

/**
 * An exception to detect IO problems with git objects.
 */
public class ObjectIOException extends Exception {
    public ObjectIOException(String msg) {
        super(msg);
    }
}
