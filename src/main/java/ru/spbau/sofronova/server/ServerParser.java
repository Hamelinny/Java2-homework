package ru.spbau.sofronova.server;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/** Parser for arguments to server. */
public class ServerParser {

    static void parse(@NotNull Iterator<String> iter) {
        Server server = new Server();
        System.out.println("Enter \"start\" to run server or \"stop\" to stop it " +
                "of \"exit\" for exit\n");
        while (true) {
            String query = iter.next();
            if (query.equals("start")) {
                server.start();
                System.out.println("started");
                continue;
            }
            if (query.equals("stop")) {
                server.stop();
                System.out.println("stopped");
                continue;
            }
            if (query.equals("exit")) {
                server.stop();
                break;
            }

            System.out.println("unknown command");
        }
    }
}
