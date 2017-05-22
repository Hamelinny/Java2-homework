package ru.spbau.sofronova.server;

import ru.spbau.sofronova.Reader;
import ru.spbau.sofronova.Writer;
import ru.spbau.sofronova.exceptions.FileInteractionIOException;
import ru.spbau.sofronova.exceptions.ServerProcessIOException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/** Class which corresponds a server that can perform queries "get" and "list".*/
public class Server {

    /**Default server port.*/
    public static final int SERVER_PORT = 50000;
    /**Code for list command.*/
    public final static int LIST_CODE = 1;
    /**Code for get command.*/
    public final static int GET_CODE = 2;
    private List<Query> toProcess;
    private volatile boolean isStopped;
    private Selector selector;
    private int port;

    /** Method to create server with default port.*/
    public Server() {
        isStopped = false;
        toProcess = new ArrayList<Query>();
        this.port = SERVER_PORT;
    }

    /**
     * Method to create server with specified port.
     * @param port port number
     */
    public Server(int port) {
        isStopped = false;
        toProcess = new ArrayList<Query>();
        this.port = port;
    }

    /**
     * Method to get port number
     * @return port number
     */
    public int getPort() {
        return port;
    }

    /** Method to run server and to start processing connections.*/
    public void start() {
        new Thread(() -> {
            try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                 Selector s = Selector.open()) {
                selector = s;
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.bind(new InetSocketAddress(port));
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                while (true) {
                    int ready = selector.selectNow();
                    if (ready > 0) {
                        process();
                    }
                    if (isStopped)
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }

        }).start();
    }

    /** Method to stop server.*/
    public void stop() {
        isStopped = true;
    }

    private void process() throws ServerProcessIOException, FileInteractionIOException {
        Set<SelectionKey> selected = selector.selectedKeys();
        Iterator<SelectionKey> iter = selected.iterator();
        while (iter.hasNext()) {
            SelectionKey key = iter.next();
            if (key.isAcceptable()) {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel client;
                try {
                    client = serverSocketChannel.accept();
                    client.configureBlocking(false);
                } catch (IOException e) {
                    throw new ServerProcessIOException(e);
                }
                try {
                    client.register(selector, SelectionKey.OP_READ, new Reader((ByteChannel)client));
                } catch (ClosedChannelException e) {
                    throw new ServerProcessIOException(e);
                }
            } else if (key.isReadable()) {
                try {
                    Reader reader = (Reader)key.attachment();
                    reader.read();
                    if (reader.getBytesNumber() == -1) {
                        byte[] content = reader.getContent();
                        key.interestOps(0);
                        toProcess.add(new Query(key, content));
                    }
                } catch (IOException e) {
                    throw new ServerProcessIOException(e);
                }
                
            } else if (key.isWritable()) {
                try {
                    Writer writer = (Writer)key.attachment();
                    writer.write();
                    if (writer.getBytesWritten() == -1) {
                        key.channel().close();
                    }
                } catch (IOException e) {
                    throw new ServerProcessIOException(e);
                }

            }
            iter.remove();
        }
        processQueries();
    }

    private void processQueries() throws FileInteractionIOException {
        for (Query query : toProcess) {
            byte[] response;
            try (ByteArrayInputStream byteStream = new ByteArrayInputStream(query.getContent());
                 DataInputStream inputStream = new DataInputStream(byteStream)) {
                int code = inputStream.readInt();
                if (code == LIST_CODE) {
                    final String path = inputStream.readUTF();
                    response = new ListPerformer().perform(Paths.get(path));
                } else if (code == GET_CODE){
                    final String path = inputStream.readUTF();
                    response = new GetPerformer().perform(Paths.get(path));
                } else {
                    throw new RuntimeException();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                SelectableChannel channel = query.getKey().channel();
                channel.register(selector, SelectionKey.OP_WRITE, new Writer(response, (ByteChannel)query.getKey().channel()));
            } catch (ClosedChannelException e) {
                throw new RuntimeException(e);
            }
        }
        toProcess = new ArrayList<>();
    }
}
