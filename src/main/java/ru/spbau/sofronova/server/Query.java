package ru.spbau.sofronova.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.channels.SelectionKey;

/** Class which corresponds a query entity.*/
public class Query {

    private SelectionKey key;
    private byte[] content;

    /**
     * Makes Query from selection key and information about command in byte array.
     * @param key
     * @param content
     */
    public Query(@NotNull SelectionKey key, @Nullable byte[] content) {
        this.key = key;
        this.content = content;
    }

    /**
     * Method to get selection key.
     * @return selection key
     */
    public SelectionKey getKey() {
        return key;
    }

    /**
     * Method to get info about command.
     * @return info about command in byte array.
     */
    public byte[] getContent() {
        return content;
    }
}
