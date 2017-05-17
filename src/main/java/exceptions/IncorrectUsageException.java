package exceptions;

/**An exception to detect some incorrect combination of annotations.*/
public class IncorrectUsageException extends Exception {
    public IncorrectUsageException(String msg) {
        super(msg);
    }
}
