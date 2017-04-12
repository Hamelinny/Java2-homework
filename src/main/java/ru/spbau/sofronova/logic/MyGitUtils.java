package ru.spbau.sofronova.logic;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A class that contains some useful constants and methods.
 */
public class MyGitUtils {


    private static final int HASH_LENGTH = 40;

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
