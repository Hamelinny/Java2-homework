package ru.spbau.sofronova.exceptions;

/**Exception during sending data to server.*/
public class SendDataException extends Exception {
    /**
     * To make an exception from other exception.
     * @param e exception
     */
    public SendDataException(Exception e) {
        super(e);
    }
}
