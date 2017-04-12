package ru.spbau.sofronova.console;

import ru.spbau.sofronova.logic.MyGit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.spbau.sofronova.logic.MyGit.*;

/**
 * A class which provides parsing of arguments from console.
 */
public class Parser {

    /**
     * Method for parsing arguments from console.
     * @param arg arguments from console
     */
    public static void parse(String[] arg) {
        MyGit repository = new MyGit(System.getProperty("user.dir"));
        try {
            if (arg.length == 0) {
                System.out.println("please enter a command\n");
                return;
            }
            if (arg[0].equals("init")) {
                if (arg.length != 1) {
                    System.out.println("cannot parse parameters after init\n");
                    return;
                }
                repository.init();
                return;
            }
            if (arg[0].equals("add")) {
                if (arg.length == 1) {
                    System.out.println("please specify the parameters\n");
                }
                List<Path> files = new ArrayList<>();
                for (int i = 1; i < arg.length; i++) {
                    files.add(Paths.get(arg[i]));
                }
                repository.add(files);
                return;
            }
            if (arg[0].equals("commit")) {
                if (arg.length == 1) {
                    System.out.println("please enter a message\n");
                    return;
                }
                if (arg.length > 2) {
                    System.out.println("cannot parse parameters after message\n");
                    return;
                }
                repository.commit(arg[1]);
                return;
            }
            if (arg[0].equals("checkout")) {
                if (arg.length == 1) {
                    System.out.println("please enter a branch name\n");
                    return;
                }
                if (arg.length > 2) {
                    System.out.println("cannot parse parameters after branch name");
                    return;
                }
                repository.checkout(arg[1]);
                return;
            }
            if (arg[0].equals("branch")) {
                if (arg.length == 1) {
                    System.out.println("please enter a branch name or an option");
                    return;
                }
                if (arg.length == 2) {
                    if (arg[1].equals("-d")) {
                        System.out.println("please enter a branch name");
                        return;
                    }
                    repository.branch(arg[1]);
                    return;
                }
                if (arg.length == 3) {
                    if (arg[1].equals("-d")) {
                        repository.branchWithDOption(arg[2]);
                        return;
                    }
                    System.out.println("cannot parse the option\n");
                    return;
                }
                System.out.println("cannot parse the parameters after branch name");
                return;
            }
            if (arg[0].equals("merge")) {
                if (arg.length == 1) {
                    System.out.println("please enter a branch name\n");
                    return;
                }
                if (arg.length > 2) {
                    System.out.println("cannot parse parameters after branch name\n");
                    return;
                }
                repository.merge(arg[1]);
                return;
            }
            if (arg[0].equals("log")) {
                if (arg.length > 1) {
                    System.out.println("cannot parse parameters after log\n");
                    return;
                }
                String logContent = new String(repository.log());
                System.out.println(logContent);
                return;
            }
            if (arg[0].equals("rm")) {
                if (arg.length == 1) {
                    System.out.println("please specify parameters\n");
                    return;
                }
                List <String> paths = new ArrayList<>();
                Collections.addAll(paths, arg);
                repository.rm(paths);
                return;
            }
            if (arg[0].equals("reset")) {
                if (arg.length == 1) {
                    System.out.println("please specify parameters\n");
                    return;
                }
                repository.reset(Paths.get(arg[1]));
                return;
            }
            if (arg[0].equals("clean")) {
                if (arg.length > 1) {
                    System.out.println("too many arguments\n");
                    return;
                }
                repository.clean();
                return;
            }
            if (arg[0].equals("status")) {
                if (arg.length > 1) {
                    System.out.println("too many arguments\n");
                    return;
                }
                System.out.println(repository.status());
                return;
            }
            System.out.println("unknown command\n");
        }
        catch (Exception e) {
            repository.getLogger().trace("exception " + e.getMessage() + "\n");
            System.out.println(e.getMessage());
        }

    }
}
