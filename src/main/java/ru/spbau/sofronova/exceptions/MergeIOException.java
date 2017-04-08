package ru.spbau.sofronova.exceptions;

/**
 * An exception to detect problems with IO during merge.
 */
public class MergeIOException extends Exception {
    public MergeIOException(String msg) {
        super(msg);
    }
}
