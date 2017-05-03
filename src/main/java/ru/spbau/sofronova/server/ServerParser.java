package ru.spbau.sofronova.server;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/** Parser for arguments to server. */
public class ServerParser {

    static void parse(@NotNull Iterator<String> iter) {
        Server server = new Server();
        while (true) {
            String query = iter.next();
            if (query.equals("start")) {
                server.start();
                continue;
            }
            if (query.equals("stop")) {
                server.stop();
                continue;
            }
            System.out.println("unknown command");
        }
    }
}
