package ru.spbau.sofronova.logic;

import com.sun.istack.internal.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A class that contains some useful constants and methods.
 */
public class MyGitUtils {
    /**
     * A path to directory which contains git repository.
     */
    public static final Path GIT_DIRECTORY = buildPath(System.getProperty("user.dir"), ".git");
    /**
     * A path to INDEX file.
     */
    public static final Path INDEX = buildPath(GIT_DIRECTORY, "index");
    /**
     * A path to directory which contains git objects.
     */
    public static final Path OBJECTS_DIRECTORY = buildPath(GIT_DIRECTORY, "objects");
    /**
     * A path to directory which contains references to current commit in each branch.
     */
    public static final Path REFS_DIRECTORY = buildPath(GIT_DIRECTORY, "refs");
    /**
     * A path to HEAD file.
     */
    public static final Path HEAD = buildPath(GIT_DIRECTORY, "HEAD");
    /**
     * A path to directory which contains logs for each branch.
     */
    public static final Path LOGS_DIRECTORY = buildPath(GIT_DIRECTORY, "logs");

    private static final int HASH_LENGTH = 40;

    /**
     * Method to create all needful directories.
     * @throws IOException if there are problems during creation.
     */
    static void makeDirs() throws IOException {
        Files.createDirectory(GIT_DIRECTORY);
        Files.createDirectory(OBJECTS_DIRECTORY);
        Files.createDirectory(REFS_DIRECTORY);
        Files.createDirectory(LOGS_DIRECTORY);
        Files.createFile(INDEX);
        Files.createFile(HEAD);
    }

    /**
     * Method returns a new line symbol as an array of bytes.
     * @return new line symbol
     */
    public static byte[] newLine() {
        return System.getProperty("line.separator").getBytes();
    }

    /**
     * Method to determine whether the string is hash or not.
     * @param mayBeHashMayBeNot string to check
     * @return true if string is hash, false if not
     */
    static boolean isHash(@NotNull String mayBeHashMayBeNot) {
        if (mayBeHashMayBeNot.length() != HASH_LENGTH){
            return false;
        }
        for (int i = 0; i < mayBeHashMayBeNot.length(); i++){
            char c = mayBeHashMayBeNot.charAt(i);
            if (!('0' <= c && c <= '9' || 'a' <= c && c <= 'f'))
                return false;
        }
        return true;
    }

    /**
     * Method to delete a specified directory recursively.
     * @param dir directory to delete
     */
    public static void deleteDirectory(@NotNull File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                File f = new File(dir, aChildren);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }

    /**
     * Method to create a path to file if we know a path to a parent directory and a name of file.
     * @param dir parent directory (Path type)
     * @param name name of file
     * @return path to file
     */
    public static Path buildPath(@NotNull Path dir, @NotNull String name) {
        return Paths.get(dir + File.separator + name);
    }

    /**
     * Method to create a path to file if we know a path to a parent directory and a name of file.
     * @param dir parent directory (String type)
     * @param name name of file
     * @return path to file
     */
    public static Path buildPath(@NotNull String dir, @NotNull String name) {
        return Paths.get(dir + File.separator + name);
    }
}
