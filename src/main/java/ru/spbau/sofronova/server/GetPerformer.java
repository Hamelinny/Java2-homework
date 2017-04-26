package ru.spbau.sofronova.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Class which performs a "get" command.*/
public class GetPerformer extends Performer {

    /**
     * Methos which performs a "get" command.
     * @param path file to get content from
     * @return content
     */
    @Override
    public byte[] perform(Path path)  {
        if (Files.isDirectory(path) || Files.notExists(path)) {
            return null;
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
