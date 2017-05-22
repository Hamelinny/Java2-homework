package ru.spbau.sofronova.exceptions;

/**Exception to detect problems then you convert received bytes into something else.*/
public class ConvertDataIOException extends Exception {
    /**
     * To make an exception from other exception.
     * @param e exception
     */
    public ConvertDataIOException(Exception e) {
        super(e);
    }
}
