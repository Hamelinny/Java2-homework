import TestClasses.*;
import exceptions.IncorrectUsageException;
import junit.TestReport;
import junit.Tester;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MyJUnitTests {

    @Test
    public void beforeAndAfterTest() throws Exception {
        Tester tester = new Tester();
        tester.testClass(BeforeAndAfterTestClass.class);
    }

    @Test
    public void exceptionsTest() throws Exception {
        Tester tester = new Tester();
        tester.testClass(ThrowsExceptionsTestClass.class);
        List<TestReport> reports = tester.getReports();
        assertEquals(2, reports.size());
        assertTrue(reports.stream().anyMatch(e ->
            e.getResult().equals("passed: ") && e.getTestName().equals("testPassed")
        ));
        assertTrue(reports.stream().anyMatch(e ->
                e.getResult().startsWith("failed: ") && e.getTestName().equals("testFailed")
        ));
    }

    @Test(expected = IncorrectUsageException.class)
    public void incorrectUsage() throws Exception {
        Tester tester = new Tester();
        tester.testClass(IncorrectUsageTestClass.class);
    }

    @Test
    public void testIgnored() throws Exception {
        Tester tester = new Tester();
        tester.testClass(IgnoredTestClass.class);
        assertTrue(tester.getReports().stream().allMatch(e ->
                e.getTestName().equals("method") && e.getResult().startsWith("ignored: ")));
    }

    @Test
    public void simpleTest() throws Exception {
        Tester tester = new Tester();
        tester.testClass(SimpleTestClass.class);
        tester.getReports().forEach(r -> System.out.println(r.getInfo()));
    }
}
