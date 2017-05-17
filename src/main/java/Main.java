import junit.TestReport;
import junit.Tester;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**Main class.*/
public class Main {
    /**
     * Main method. Takes paths to .jar files as arguments. Prints reports of the tests.
     * @param args paths to .jar files
     * @throws IOException if there is something wrong with IO
     * @throws ClassNotFoundException if there is no such class
     */
    public static void main(@NotNull String[] args) throws IOException, ClassNotFoundException {
        List<Class> classesToTest = Parser.parse(args);
        try {
            for (Class clazz : classesToTest) {
                Tester tester = new Tester();
                tester.testClass(clazz);
                tester.getReports().forEach(r -> System.out.println(r.getInfo()));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
