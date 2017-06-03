package ru.spbau.sofronova.exceptions;

/**
 * An exception to detect that repository does not exist.
 */
public class GitDoesNotExistException extends Exception {
    public GitDoesNotExistException(String msg) {
        super(msg);
    }
}
