package exceptions;

/**An exception to detect that something went wrong in method with @After annotation.*/
public class AfterException extends Exception {
    public AfterException(String msg) {
        super(msg);
    }
}
