package exceptions;


/**An exception to detect that something went wrong in method with @Before annotation.*/
public class BeforeException extends Exception {
    public BeforeException(String msg) {
        super(msg);
    }
}
