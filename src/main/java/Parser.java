import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**Class to parse input.*/
public class Parser {
    private static final String CLASS_SUFFIX = ".class";

    /**
     * Makes list of classes to test from paths to .jar files.
     * @param args paths to .jar files
     * @return list of classes
     * @throws IOException if there are something wrong with IO
     * @throws ClassNotFoundException if there is no such class
     */
    public static List<Class> parse(@NotNull String[] args) throws IOException, ClassNotFoundException {
        List<Class> classesToTest = new ArrayList<>();
        for (String pathToJar : args) {
            Enumeration<JarEntry> entries = new JarFile(pathToJar).entries();
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{
                    new URL("jar:file:" + pathToJar + "!/")
            });
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(CLASS_SUFFIX)) {
                    continue;
                }
                String className = jarEntry.getName()
                        .substring(0, jarEntry.getName().length() - CLASS_SUFFIX.length())
                        .replace('/', '.');;
                classesToTest.add(classLoader.loadClass(className));
            }
        }
        return classesToTest;
    }
}
