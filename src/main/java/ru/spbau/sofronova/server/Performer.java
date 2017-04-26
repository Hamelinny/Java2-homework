package ru.spbau.sofronova.server;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/** An abstract class which gives a template for performing a command.*/
public abstract class Performer {
    /**
     * An abstract method to perform a command.
     * @param path file or directory
     * @return result in byte array
     */
    public abstract byte[] perform(@NotNull Path path);
}
