package ru.spbau.sofronova;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

/**
 * Lock-free lazy computation which is suitable for multithreaded usage.
 * Method 'get' from supplier can be called more than once.
 * @param <T> type of the result for computation
 */
public class LockFreeLazy<T> extends LazyImpl<T> {

    /**
     * An atomic updater of result.
     */
    private static final AtomicReferenceFieldUpdater<LazyImpl, Object> atomicResultUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LazyImpl.class, Object.class, "result");

    /**
     * Creates lock-free lazy from supplier.
     * @param sup evaluation to perform
     */
    public LockFreeLazy(Supplier<T> sup) {
        super(sup);
    }

    /**
     * Returns the result of computation.
     * @return the result of computation
     */
    @Override
    public T get() {
        if (result == NOTCOMPUTED) {
            atomicResultUpdater.compareAndSet(this, NOTCOMPUTED, supplier.get());
        }
        return result;
    }
}

