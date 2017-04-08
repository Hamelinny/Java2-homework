package ru.spbau.sofronova.exceptions;

/**
 * An exception to detect problems with IO in INDEX file.
 */
public class IndexIOException extends Exception {
    public IndexIOException(String msg) {
        super(msg);
    }
}
