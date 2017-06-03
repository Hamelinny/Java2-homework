package ru.spbau.sofronova.exceptions;

/**
 * An exception to detect that repository we want to init is already initialized.
 */
public class GitAlreadyInitializedException extends Exception {
    public GitAlreadyInitializedException(String msg) {
        super(msg);
    }
}
