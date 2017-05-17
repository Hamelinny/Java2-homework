package TestClasses;

import annotations.Test;

import java.util.MissingResourceException;

public class ThrowsExceptionsTestClass {

    @Test(expected = NullPointerException.class)
    public void testPassed() {
        throw new NullPointerException();
    }

    @Test(expected = MissingResourceException.class)
    public void testFailed() {
        throw new NullPointerException();
    }
}
