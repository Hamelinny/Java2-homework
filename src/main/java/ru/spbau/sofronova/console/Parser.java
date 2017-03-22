package ru.spbau.sofronova.console;


import ru.spbau.sofronova.exceptions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ru.spbau.sofronova.logic.MyGitCommands.*;

/**
 * A class which provides parsing of arguments from console.
 */

public class Parser {

    /**
     * Method for parsing arguments from console.
     * @param arg arguments from console
     */
    public static void parse(String[] arg) {
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
                init();
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
                add(files);
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
                commit(arg[1]);
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
                checkout(arg[1]);
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
                    branch(arg[1]);
                    return;
                }
                if (arg.length == 3) {
                    if (arg[1].equals("-d")) {
                        branchWithDOption(arg[2]);
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
                merge(arg[1]);
                return;
            }
            if (arg[0].equals("log")) {
                if (arg.length > 1) {
                    System.out.println("cannot parse parameters after log\n");
                    return;
                }
                String logContent = new String(log());
                System.out.println(logContent);
            }
            System.out.println("unknown command\n");
        }
        catch (GitDoesNotExistException e) {
            System.out.println("git does not exist\n");
        }
        catch (BranchAlreadyExistsException e) {
            System.out.println("branch already exists\n");
        }
        catch (BranchDeletionException e) {
            System.out.println("cannot delete this branch\n");
        }
        catch (BranchIOException e) {
            System.out.println("cannot change this branch\n");
        }
        catch (GitAlreadyInitedException e) {
            System.out.println("git already inited\n");
        }
        catch (HeadIOException e) {
            System.out.println("cannot change HEAD file\n");
        }
        catch (LogIOException e) {
            System.out.println("cannot change log");
        }
        catch (IndexIOException e) {
            System.out.println("cannot change INDEX file\n");
        }
        catch (MergeIOException e) {
            System.out.println("IO exception in merge\n");
        }
        catch (ObjectAddException e) {
            System.out.println("cannot add one of objects\n");
        }
        catch (ObjectIOException e) {
            System.out.println("IO exception in GitObject");
        }

    }
}
