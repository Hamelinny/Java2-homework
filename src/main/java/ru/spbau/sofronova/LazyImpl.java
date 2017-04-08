package ru.spbau.sofronova;

import java.util.function.Supplier;

/**
 * An abstract class that gives a model of implementation of Lazy.
 * @param <T> type of result for evaluation
 */
public abstract class LazyImpl<T> implements Lazy<T> {

    /**
     * Evaluation to perform.
     */
    protected Supplier<T> supplier;

    /**
     * Result for evaluation.
     */
    protected volatile T result;

    /**
     * Object to determine that there's no result yet.
     */
    protected static final Object NOTCOMPUTED = new Object();

    /**
     * Creates a lazy computation from a supplier.
     * @param sup evaluation to perform
     */
    protected LazyImpl(Supplier<T> sup) {
        supplier = sup;
        result = (T)NOTCOMPUTED;
    }
}


