package ru.spbau.sofronova;

import java.util.function.Supplier;

/**
 * Lazy computation which is suitable just for single thread.
 * @param <T> type of result for computation
 */
public class SimpleLazy<T> extends LazyImpl<T> {

    /**
     * Creates simple lazy computation from supplier.
     * @param sup evaluation to perform
     */
    public SimpleLazy(Supplier<T> sup) {
        super(sup);
    }

    /**
     * Returns the result of computation.
     * @return the result
     */
    @Override
    public T get() {
        if (result == NOTCOMPUTED) {
            result = supplier.get();
        }
        return result;
    }

}
