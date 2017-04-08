package ru.spbau.sofronova.exceptions;

/**
 * An exception to detect problems during branch deletion.
 */
public class BranchDeletionException extends Exception {
    public BranchDeletionException(String msg) {
        super(msg);
    }
}
