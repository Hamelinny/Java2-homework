
import org.junit.Before;
import org.junit.Test;
import ru.spbau.sofronova.Lazy;
import ru.spbau.sofronova.LazyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class LazyFactoryTests {

    private static final int THREADS = 4;
    private static Supplier<Object> NULL = () -> null;
    private static int callsNumber = 0;
    private static Supplier<Integer> fortyTwo = () -> {
        callsNumber++;
        return 42;
    };


    @Before
    public void preTest() {
        callsNumber = 0;
    }

    @Test
    public void testSimpleLazyNullSupplier() {
        Lazy <Object> nullLazy = LazyFactory.createSimpleLazy(NULL);
        assertNull(nullLazy.get());
    }

    @Test
    public void testSimpleLazyGetNotCalled() {
        Lazy<Integer> uselessLazy = LazyFactory.createSimpleLazy(fortyTwo);
        assertEquals(0, callsNumber);
    }

    @Test
    public void testSimpleLazyOnSimpleCase() {
        testSimpleCase(LazyFactory.createSimpleLazy(fortyTwo));
    }

    @Test
    public void testConcurrentLazyOnSimpleCase() {
        testSimpleCase(LazyFactory.createConcurrentLazy(fortyTwo));
    }

    @Test
    public void testLockFreeLazyOnSimpleCase() {
        testSimpleCase(LazyFactory.createLockFreeLazy(fortyTwo));
    }

    @Test
    public void testConcurrentLazy() throws InterruptedException {
        testConcurrentWithOrWithoutLock(LazyFactory.createConcurrentLazy(fortyTwo));
        assertEquals(1, callsNumber);
    }

    @Test
    public void testLockFreeLazy() throws InterruptedException {
        testConcurrentWithOrWithoutLock(LazyFactory.createLockFreeLazy(fortyTwo));
    }


    private void testSimpleCase(Lazy <Integer> lazy) {
        Integer answer = lazy.get();
        assertEquals(fortyTwo.get(), answer);
        Integer anotherAnswer = lazy.get();
        assertSame(answer, anotherAnswer);
    }

    private void testConcurrentWithOrWithoutLock(Lazy <Integer> lazy) throws InterruptedException {
        Integer[] results = new Integer[THREADS];
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < threads.length; i++) {
            int index = i;
            threads[i] = new Thread(() -> results[index] = lazy.get());
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        assertEquals(new Integer(42), results[0]);
        for (int i = 1; i < results.length; ++i) {
            assertSame(results[0], results[i]);
        }
    }
}