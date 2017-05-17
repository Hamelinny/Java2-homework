package exceptions;


/**An exception to detect that something went wrong in method with @BeforeClass annotation.*/
public class BeforeClassException extends Exception {
    public BeforeClassException(String msg) {
        super(msg);
    }
}
