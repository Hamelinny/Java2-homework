package ru.spbau.sofronova;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

/** Class which purpose is writing to channel.*/
public class Writer {

    private ByteBuffer buffer;
    private ByteChannel channel;
    private int bytesWritten = 0;
    private boolean flag = true;

    /**
     * Creates writer from data to write and channel.
     * @param data bytes to write
     * @param channel channel
     */
    public Writer(byte[] data, ByteChannel channel) {
        if (data == null)
            flag = false;
        else {
            this.buffer = ByteBuffer.wrap(data);
            this.channel = channel;
        }
    }

    /**
     * Writes as much as can.
     * @throws IOException if there are IO troubles
     */
    public void write() throws IOException {
        if (!flag) {
            bytesWritten = -1;
            return;
        }
        if (buffer.hasRemaining()) {
            bytesWritten = channel.write(buffer);
        } else {
            bytesWritten = -1;
        }
    }

    /**
     * Returns number of bytes written the last time.
     * @return number of bytes
     */
    public int getBytesWritten() {
        return bytesWritten;
    }
}
