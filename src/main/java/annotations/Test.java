package annotations;
import exceptions.NotAnException;
import junit.Tester;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Annotation for test methods.*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    Class expected() default NotAnException.class;
    String ignore() default Tester.NOT_IGNORED;
}
