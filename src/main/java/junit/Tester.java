package junit;

import annotations.*;
import exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**Class that runs all tests in specified class respecting the rules of annotations.*/
public class Tester {
    /**Constant to detect that method should not be ignored.*/
    public static final String NOT_IGNORED = "Not ignored";
    private List<Method> toTest = new ArrayList<>();
    private List<TestReport> reports = new ArrayList<>();
    private List<Method> beforeClass = new ArrayList<>();
    private List<Method> afterClass = new ArrayList<>();
    private List<Method> before = new ArrayList<>();
    private List<Method> after = new ArrayList<>();
    private Object instance = null;

    /**
     * Method to test specified class.
     * @param classToTest class to test
     * @throws BeforeClassException if something went wrong in @BeforeClass-method
     * @throws AfterClassException if something went wrong in @AfterClass-method
     * @throws IncorrectUsageException if there is incorrect combination of annotations
     * @throws AfterException if something went wrong in @After-method
     * @throws BeforeException if something went wrong in @Before-method
     */
    public void testClass(@NotNull Class classToTest) throws BeforeClassException,
            AfterClassException, IncorrectUsageException, AfterException, BeforeException {
        prepareClass(classToTest);
        runBeforeClass();
        testAll();
        runAfterClass();
    }

    /**
     * Method to get reports about results of tests.
     * @return reports
     */
    public List<TestReport> getReports() {
        return reports;
    }

    private void testAll() throws AfterException, BeforeException {
        for (Method method : toTest) {
            runBefore();
            long start = System.currentTimeMillis();
            Throwable exception = null;
            try {
                method.invoke(instance);

            } catch (Exception e) {
                exception = e.getCause();
            }
            long stop = System.currentTimeMillis();
            Class expected = method.getAnnotation(Test.class).expected();
            if ((expected == NotAnException.class && exception == null) ||
                    (exception != null && expected != NotAnException.class && expected.equals(exception.getClass()))) {
                reports.add(new TestReport(method.getName(), "passed: ", stop - start));
            } else {
                reports.add(new TestReport(method.getName(), "failed: " + exception.getMessage()));
            }
            runAfter();
        }
    }

    private void runAfter() throws AfterException {
        for (Method method : after)
            try {
                method.invoke(instance);
            } catch (Exception e) {
                throw new AfterException(String.format("Fail in %s, %s", method.getName(), e.getMessage()));
            }
    }

    private void runBefore() throws BeforeException {
        for (Method method : before)
            try {
                method.invoke(instance);
            } catch (Exception e) {
                throw new BeforeException(String.format("Fail in %s, %s", method.getName(), e.getMessage()));
            }
    }

    private void runBeforeClass() throws BeforeClassException {
        for (Method method : beforeClass)
            try {
                method.invoke(instance);
            } catch (Exception e) {
                throw new BeforeClassException(String.format("Fail in %s, %s", method.getName(), e.getMessage()));
            }
    }

    private void runAfterClass() throws AfterClassException {
        for (Method method : afterClass)
            try {
                method.invoke(instance);
            } catch (Exception e) {
                throw new AfterClassException(String.format("Fail in %s, %s", method.getName(), e.getMessage()));
            }
    }

    private void prepareClass(@NotNull Class classToTest) throws IncorrectUsageException {
        Method[] methods = classToTest.getDeclaredMethods();
        for (Method method : methods) {
            if (!valid(method))
                throw new IncorrectUsageException(String.format("incorrect annotation usage in %s",
                        method.getName()));
            if (method.isAnnotationPresent(Test.class)) {
                String ignore = method.getAnnotation(Test.class).ignore();
                if (!ignore.equals(NOT_IGNORED)) {
                    reports.add(new TestReport(method.getName(), "ignored: " + ignore));
                } else {
                    toTest.add(method);
                }
            }
            if (method.isAnnotationPresent(After.class))
                after.add(method);
            if (method.isAnnotationPresent(Before.class))
                before.add(method);
            if (method.isAnnotationPresent(AfterClass.class))
                afterClass.add(method);
            if (method.isAnnotationPresent(BeforeClass.class))
                beforeClass.add(method);
        }
        try {
            instance = classToTest.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean valid(@NotNull Method method) {
        return !method.isAnnotationPresent(Test.class) ||
                !(method.isAnnotationPresent(Before.class) || method.isAnnotationPresent(After.class)
                || method.isAnnotationPresent(BeforeClass.class) || method.isAnnotationPresent(AfterClass.class));
    }
}
