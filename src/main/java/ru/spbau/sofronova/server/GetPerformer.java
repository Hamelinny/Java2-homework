package ru.spbau.sofronova.server;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.FileInteractionIOException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Class which performs a "get" command.*/
public class GetPerformer extends Performer {

    /**
     * Methos which performs a "get" command.
     * @param path file to get content from
     * @return content
     * @throws FileInteractionIOException IOException during interaction with file
     */
    @Override
    public byte[] perform(@NotNull Path path) throws FileInteractionIOException {
        if (Files.isDirectory(path) || Files.notExists(path)) {
            return null;
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new FileInteractionIOException(e);
        }
    }
}
