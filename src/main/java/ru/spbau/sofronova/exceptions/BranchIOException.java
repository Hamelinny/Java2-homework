package ru.spbau.sofronova.exceptions;

/**
 * An exception to detect problems during reading/writing some information about branch.
 */
public class BranchIOException extends Exception {
    public BranchIOException(String msg) {
        super(msg);
    }
}
