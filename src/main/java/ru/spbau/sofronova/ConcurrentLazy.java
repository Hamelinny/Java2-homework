package ru.spbau.sofronova;

import java.util.function.Supplier;

/**
 * Lazy computation which is suitable for multithreaded usage.
 * @param <T> type of result for computation
 */
public class ConcurrentLazy<T> extends LazyImpl<T> {

    /**
     * Creates concurrent lazy from supplier.
     * @param sup evaluation to perform
     */
    public ConcurrentLazy(Supplier<T> sup) {
        super(sup);
    }

    /**
     * Returns the result of computation.
     * @return the result
     */
    @Override
    public T get() {
        if (result != NOTCOMPUTED)
            return result;
        synchronized (this) {
            if (result == NOTCOMPUTED) {
                result = supplier.get();
            }
            return result;
        }
    }
}
