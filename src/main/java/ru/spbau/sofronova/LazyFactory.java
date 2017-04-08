package ru.spbau.sofronova;

import java.util.function.Supplier;

/**
 * A class that contains three implementations of lazy computation.
 */
public class LazyFactory <T> {

    /**
     * Creates a lazy computation which is suitable just for single thread.
     * @param supplier evaluation to perform
     * @param <T> type of the result for evaluation
     * @return lazy computation
     */
    public static <T> Lazy<T> createSimpleLazy(Supplier<T> supplier) {
        return new SimpleLazy(supplier);
    }

    /**
     * Creates a lazy computation which is suitable for multithreaded usage.
     * @param supplier evaluation to perform
     * @param <T> type of the result for evaluation
     * @return lazy computation
     */
    public static <T> Lazy<T> createConcurrentLazy(Supplier<T> supplier) {
        return new ConcurrentLazy(supplier);
    }

    /**
     * Creates a lock-free lazy computation which is suitable for multithreaded usage.
     * Supplier's method 'get' can be called more than once.
     * @param supplier evaluation to perform
     * @param <T> type of the result for evaluation
     * @return lazy computation
     */
    public static <T> Lazy<T> createLockFreeLazy(Supplier<T> supplier) {
        return new LockFreeLazy(supplier);
    }
}
