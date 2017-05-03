package ru.spbau.sofronova.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.sofronova.Reader;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/** Class which corresponds to a client. It can make requests "get" and "list". */
public class Client {

    private final int port;
    private final static int LIST_CODE = 1;
    private final static int GET_CODE = 2;

    /**
     * Creates client which will be connected with server using given port.
     * @param port
     */
    public Client(int port) {
        this.port = port;
    }

    /**
     * Method which send "list" request and receive list of files in a given directory.
     * @param path given directory
     * @return list of names of files
     */
    public List<String> executeList(String path) {
        byte[] content = sendAndReceive(LIST_CODE, path);
        return getListOfString(content);
    }

    /**
     * Method which send "get" request and receive a content of file with specified name.
     * @param path
     * @param pathToWrite
     */
    public void executeGet(@NotNull String path, @NotNull String pathToWrite) {
        byte[] content = sendAndReceive(GET_CODE, path);
        try {
            if (content == null || Arrays.equals(content, new byte[0])) {
                Files.write(Paths.get(pathToWrite), Long.toString(0).getBytes());
            } else {
                Files.write(Paths.get(pathToWrite), Long.toString(content.length).getBytes());
                Files.write(Paths.get(pathToWrite), "\n".getBytes());
                Files.write(Paths.get(pathToWrite), content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private byte[] sendAndReceive(int code, @NotNull String arg) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(port));
            channel.configureBlocking(false);;
            byte[] bytes;

            outputStream.writeInt(code);
            outputStream.writeUTF(arg);
            outputStream.flush();
            bytes = byteStream.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            channel.shutdownOutput();
            Reader reader = new Reader(channel);
            reader.read();
            while (reader.getBytesNumber() != -1) {
                reader.read();
            }
            channel.close();
            return reader.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private List <String> getListOfString(byte[] content) {
        List <String> newContent = new ArrayList<>();
        if (content == null) {
            newContent.add(Integer.toString(0));
            return newContent;
        }
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(content);
             DataInputStream inputStream = new DataInputStream(byteStream)) {
            int size = inputStream.readInt();
            while (size > 0) {
                size--;
                newContent.add(inputStream.readUTF());
            }
            return newContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
