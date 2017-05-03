package ru.spbau.sofronova.server;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

import static ru.spbau.sofronova.server.ServerParser.*;

/** Class for controlling server from console */
public class ServerConsole {
    /**
     * Main method with arguments from command line
     * @param args arguments from command line
     */
    public static void main(@NotNull String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            parse(scanner);
        }
    }
}
