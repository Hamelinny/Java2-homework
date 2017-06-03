package ru.spbau.sofronova.exceptions;

/**IOException when server processing data.*/
public class ServerProcessIOException extends Exception {
    /**
     * To make an exception from other exception.
     * @param e exception
     */
    public ServerProcessIOException(Exception e) {
        super(e);
    }
}
