package ru.spbau.sofronova;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Arrays;

/** Class which purpose is reading from channel.*/
public class Reader {

    private final static int SIZE = 1024;
    private byte[] content = null;
    private ByteBuffer buf;
    private int bytesNumber = 0;
    private ByteChannel channel;
    private int position = 0;

    /**
     * Creates reader from channel.
     * @param channel channel
     */
    public Reader(ByteChannel channel) {
        this.buf = ByteBuffer.allocate(SIZE);
        this.channel = channel;
    }

    /**
     * Reads as much as can.
     * @throws IOException if there are IO troubles
     */
    public void read() throws IOException {
        bytesNumber = channel.read(buf);
        if (bytesNumber == -1)
            return;
        if (content == null)
            content = new byte[SIZE];
        while (position + bytesNumber > content.length) {
            byte[] newData = new byte[2 * content.length];
            System.arraycopy(content, 0, newData, 0, position);
            content = newData;
        }
        buf.flip();
        buf.get(content, position, bytesNumber);
        position += bytesNumber;
        buf.clear();
    }

    /**
     * Method to get content we read.
     * @return content in byte array
     */
    public byte[] getContent() {
        if (content == null)
            return content;
        return Arrays.copyOf(content, position);
    }

    /**
     * Returns number of bytes read the last time
     * @return number of bytes
     */
    public int getBytesNumber() {
        return bytesNumber;
    }

}
