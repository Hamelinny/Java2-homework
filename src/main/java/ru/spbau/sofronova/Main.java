package ru.spbau.sofronova;

import ru.spbau.sofronova.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ru.spbau.sofronova.console.Parser.*;
import static ru.spbau.sofronova.logic.MyGitCommands.*;


public class Main {
    public static void main(String[] args) throws GitDoesNotExistException, IndexIOException, HeadIOException, IOException, BranchIOException, LogIOException, ObjectAddException, BranchAlreadyExistsException, GitAlreadyInitedException {
        parse(args);
    }
}
