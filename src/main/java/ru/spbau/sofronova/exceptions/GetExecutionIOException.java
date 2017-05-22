package ru.spbau.sofronova.exceptions;

/**IOException during execution get command.*/
public class GetExecutionIOException extends Exception {
    /**
     * To make an exception from other exception.
     * @param e exception
     */
    public GetExecutionIOException(Exception e) {
        super(e);
    }
}
