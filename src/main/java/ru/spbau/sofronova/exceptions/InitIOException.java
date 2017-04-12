package ru.spbau.sofronova.exceptions;

/**
 * An exception to detect IO problems during git initialization.
 */
public class InitIOException extends Exception {
    public InitIOException(String msg) {
        super(msg);
    }
}
