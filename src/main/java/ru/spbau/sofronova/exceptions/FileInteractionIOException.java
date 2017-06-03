package ru.spbau.sofronova.exceptions;

/**IOException during interaction with file.*/
public class FileInteractionIOException extends Exception {
    /**
     * To make an exception from other exception.
     * @param e exception
     */
    public FileInteractionIOException(Exception e) {
        super(e);
    }
}
