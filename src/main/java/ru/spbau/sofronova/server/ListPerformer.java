package ru.spbau.sofronova.server;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.exceptions.FileInteractionIOException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/** Class to perform a "list" command.*/
public class ListPerformer extends Performer {

    /**
     * Method to perform a "list" command.
     * @param path directory to get list of files
     * @return list of files in byte array
     * @throws FileInteractionIOException IOException during interaction with file
     */
    @Override
    public byte[] perform(@NotNull Path path) throws FileInteractionIOException {
        if (!Files.isDirectory(path) || !Files.exists(path)) {
            return null;
        }
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            List<Path> paths = Files.list(path).collect(Collectors.toList());
            outputStream.writeInt(paths.size());
            for (Path p : paths) {
                outputStream.writeUTF(p.getFileName().toString());
            }
            outputStream.flush();
            return byteStream.toByteArray();
        } catch (IOException e) {
            throw new FileInteractionIOException(e);
        }
    }
}
