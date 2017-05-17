package junit;
import org.jetbrains.annotations.NotNull;

/**An entity that corresponds to the report about test.*/
public class TestReport {
    private String testName;
    private String result;
    private long time;

    /**
     * Makes report from name of the test, its result and execution time.
     * @param testName name of the test
     * @param result result of the test
     * @param time execution time
     */
    public TestReport(@NotNull String testName, @NotNull String result, long time) {
        this.testName = testName;
        this.result = result;
        this.time = time;
    }

    /**
     * Makes report from name of the test and its result.
     * @param testName name of the test
     * @param result result of the test
     */
    public TestReport(@NotNull String testName, @NotNull String result) {
        this.testName = testName;
        this.result = result;
        this.time = -1;
    }

    /**
     * Method to get name of the test
     * @return name of the test
     */
    public String getTestName() {
        return testName;
    }

    /**
     * Method to get result of the test
     * @return result of the test
     */
    public String getResult() {
        return result;
    }

    /**
     * Method to get all information from report in one string.
     * @return all info
     */
    public String getInfo() {
        String response = testName + ": " + result;
        if (time != -1) {
            response += " in time " + Long.toString(time) + "\n";
        }
        return response;
    }
}
