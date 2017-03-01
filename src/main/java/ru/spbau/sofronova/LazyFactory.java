package ru.spbau.sofronova;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
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


    /**
     * An abstract class that gives a model of implementation of Lazy.
     * @param <T> type of result for evaluation
     */

    public static abstract class LazyImpl<T> implements Lazy<T> {

        /**
         * Evaluation to perform.
         */

        protected Supplier <T> supplier;

        /**
         * Result for evaluation.
         */

        protected volatile T result;

        /**
         * Object to determine that there's no result yet.
         */

        protected static final Object notComputed = new Object();

        /**
         * Creates a lazy computation from a supplier.
         * @param sup evaluation to perform
         */

        private LazyImpl(Supplier<T> sup) {
            supplier = sup;
            result = (T)notComputed;
        }
    }

    /**
     * Lazy computation which is suitable just for single thread.
     * @param <T> type of result for computation
     */

    public static class SimpleLazy<T> extends LazyImpl<T> {

        /**
         * Creates simple lazy computation from supplier.
         * @param sup evaluation to perform
         */

        public SimpleLazy(Supplier <T> sup) {
            super(sup);
        }

        /**
         * Returns the result of computation.
         * @return the result
         */

        @Override
        public T get() {
            if (result == notComputed) {
                result = supplier.get();
            }
            return result;
        }

    }


    /**
     * Lazy computation which is suitable for multithreaded usage.
     * @param <T> type of result for computation
     */

    public static class ConcurrentLazy<T> extends LazyImpl<T> {

        /**
         * Creates concurrent lazy from supplier.
         * @param sup evaluation to perform
         */

        public ConcurrentLazy(Supplier <T> sup) {
            super(sup);
        }

        /**
         * Returns the result of computation.
         * @return the result
         */

        @Override
        public T get() {
            if (result != notComputed)
                return result;
            synchronized (this) {
                if (result == notComputed) {
                    result = supplier.get();
                }
                return result;
            }
        }
    }


    /**
     * Lock-free lazy computation which is suitable for multithreaded usage.
     * Method 'get' from supplier can be called more than once.
     * @param <T> type of the result for computation
     */

    public static class LockFreeLazy<T> extends LazyImpl<T> {

        /**
         * An atomic updater of result.
         */

        private static final AtomicReferenceFieldUpdater<LazyImpl, Object> atomicResultUpdater =
                AtomicReferenceFieldUpdater.newUpdater(LazyImpl.class, Object.class, "result");

        /**
         * Creates lock-free lazy from supplier.
         * @param sup evaluation to perform
         */

        public LockFreeLazy(Supplier <T> sup) {
            super(sup);
        }

        /**
         * Returns the result of computation.
         * @return the result of computation
         */

        @Override
        public T get() {
            if (result == notComputed) {
                atomicResultUpdater.compareAndSet(this, notComputed, supplier.get());
            }
            return result;
        }
    }

}
