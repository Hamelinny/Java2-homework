package ru.spbau.sofronova.client;

import ru.spbau.sofronova.server.Server;

import java.util.Iterator;
import java.util.List;

/** Class for parsing arguments for client in command line. */
public class ClientParser {


    static void parse(Iterator <String> iter) {
        Client client = new Client(Server.SERVER_PORT);
        while (true) {
            String query = iter.next();
            if (query.equals("list")) {
                String path = iter.next();
                List<String> answer = client.executeList(path);
                for (String file : answer) {
                    System.out.println(file);
                }
                continue;
            }
            if (query.equals("get")) {
                String path = iter.next();
                String to = iter.next();
                client.executeGet(path, to);
                continue;
            }
            if (query.equals("exit")) {
                break;
            }
            System.out.println("unknown command");
        }
    }
}
