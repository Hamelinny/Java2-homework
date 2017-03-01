package ru.spbau.sofronova;

/**
 * An interface for lazy computation.
 * Computation occurs only at the first time it's need to be.
 * Next 'get' calls returns the same object as the first.
 * @param <T> type of result for computation
 */

public interface Lazy <T>{

    /**
     * Returns the result of computation.
     * @return the result
     */

    T get();
}
