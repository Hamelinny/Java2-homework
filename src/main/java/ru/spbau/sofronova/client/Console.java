package ru.spbau.sofronova.client;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

import static ru.spbau.sofronova.client.ClientParser.parse;

/** Class for interaction with user. */
public class Console {

    /** Main method.
     * @param args arguments from command line.
     */
    public static void main(@NotNull String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            parse(scanner);
        }
    }
}
