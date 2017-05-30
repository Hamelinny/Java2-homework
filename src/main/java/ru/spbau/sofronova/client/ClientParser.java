package ru.spbau.sofronova.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.ConvertDataIOException;
import ru.spbau.sofronova.exceptions.GetExecutionIOException;
import ru.spbau.sofronova.exceptions.SendDataException;
import ru.spbau.sofronova.server.Server;

import java.util.Iterator;
import java.util.List;

/** Class for parsing arguments for client in command line. */
public class ClientParser {


    static void parse(@NotNull Iterator <String> iter) {
        Client client = new Client(Server.SERVER_PORT);
        System.out.println("Enter \"list <directory>\" or \"get <path-to-download> <path-to-save>\"");
        while (true) {
            try {
                String query = iter.next();
                if (query.equals("list")) {
                    String path = iter.next();
                    List<String> answer = client.executeList(path);
                    if (answer == null)
                        System.out.println("not a directory or doesn't exist");
                    else {
                        for (String file : answer) {
                            System.out.println(file);
                        }
                    }
                    System.out.println("\n\n");
                    System.out.println("list executed\n");
                    continue;
                }
                if (query.equals("get")) {
                    String path = iter.next();
                    String to = iter.next();
                    client.executeGet(path, to);
                    System.out.println("get executed\n");
                    continue;
                }
                if (query.equals("exit")) {
                    break;
                }
                System.out.println("unknown command");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }
}
