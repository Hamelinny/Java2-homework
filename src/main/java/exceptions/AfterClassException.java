package exceptions;

/**An exception to detect that something went wrong in method with @AfterClass annotation.*/
public class AfterClassException extends Exception {
    public AfterClassException(String msg) {
        super(msg);
    }
}
