package ru.spbau.sofronova.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MyGitUtils {
    public static final Path GIT_DIRECTORY = buildPath(System.getProperty("user.dir"), ".git");
    public static final Path INDEX = buildPath(GIT_DIRECTORY, "index");
    public static final Path OBJECTS_DIRECTORY = buildPath(GIT_DIRECTORY, "objects");
    public static final Path REFS_DIRECTORY = buildPath(GIT_DIRECTORY, "refs");
    public static final Path HEAD = buildPath(GIT_DIRECTORY, "HEAD");
    public static final Path LOGS_DIRECTORY = buildPath(GIT_DIRECTORY, "logs");

    private static final int HASH_LENGTH = 40;

    static void makeDirs() throws IOException {
        Files.createDirectory(GIT_DIRECTORY);
        Files.createDirectory(OBJECTS_DIRECTORY);
        Files.createDirectory(REFS_DIRECTORY);
        Files.createDirectory(LOGS_DIRECTORY);
        Files.createFile(INDEX);
        Files.createFile(HEAD);
    }

    public static byte[] newLine() {
        return System.getProperty("line.separator").getBytes();
    }

    public static boolean isHash(String mayBeHashMayBeNot) {
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

    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }

    public static Path buildPath(Path dir, String name) {
        return Paths.get(dir + File.separator + name);
    }

    public static Path buildPath(String dir, String name) {
        return Paths.get(dir + File.separator + name);
    }
}
