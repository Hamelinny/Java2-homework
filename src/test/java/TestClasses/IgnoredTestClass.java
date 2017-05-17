package TestClasses;

import annotations.Test;

public class IgnoredTestClass {

    @Test(ignore = "ignore this")
    public void method() {}
}
