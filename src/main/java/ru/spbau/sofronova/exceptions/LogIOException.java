package ru.spbau.sofronova.exceptions;

/**
 * An exception to detect problems with IO in log files.
 */
public class LogIOException extends Exception {
    public LogIOException(String msg) {
        super(msg);
    }
}
