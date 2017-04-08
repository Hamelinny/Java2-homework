package ru.spbau.sofronova.exceptions;

/**
 * An exception to detect that branch we want to make already exists.
 */
public class BranchAlreadyExistsException extends Exception {
    public BranchAlreadyExistsException(String msg) {
        super(msg);
    }
}
