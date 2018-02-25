import org.junit.*;
import semaphore.MySemaphore;
import semaphore.MySemaphoreImpl;
import threads.concurrent.AtomicIncrementThread;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Created by Leshka on 26.02.18.
 */
public class MySemaphoreAtomicIntegerIncrementTest {

    private MySemaphore sem;
    private AtomicInteger atomicInt;
    private AtomicIncrementThread firstThread;
    private AtomicIncrementThread secondThread;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before MySemaphoreAtomicIntegerIncrementTest.class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("After MySemaphoreAtomicIntegerIncrementTest.class");
    }

    /**
     * In this method, we create a semaphore with one permit.
     * In addition we create thread that increments AtomicInteger;
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        sem = new MySemaphoreImpl(1);
        atomicInt = new AtomicInteger(0);
        firstThread = new AtomicIncrementThread(sem, atomicInt, 2, 200);
        secondThread = new AtomicIncrementThread(sem, atomicInt, 1, 200);
    }

    @After
    public void tearDown() throws Exception {
        sem = null;
        atomicInt = null;
    }

    /**
     * We're launching two threads with a 50ms break.
     * After 50ms trying to get AtomicInteger value.
     * Each thread increases the value by 1.
     * Therefore, if the semaphore is working correctly and only one thread get inside, the value should equals 1;
     * Moreover, it shouldn't be any available permits;
     * After 110ms with one thread inside value should equals 2;
     * After the last thread will leave semaphore value should equals 3 and there must be available permits;
     * @throws InterruptedException
     */
    @Test
    public void getTest() throws InterruptedException {
        new Thread (firstThread, "firstAtomicIncrementThread").start();
        Thread.sleep(50);
        new Thread (secondThread, "secondAtomicIncrementThread").start();
        Thread.sleep(50);
        assertEquals(1, atomicInt.get());
        assertFalse(sem.tryAcquire());
        Thread.sleep(110);
        assertEquals(2, atomicInt.get());
        assertFalse(sem.tryAcquire());
        Thread.sleep(200);
        assertEquals(3, atomicInt.get());
        Thread.sleep(300);
        assertTrue(sem.tryAcquire());
    }
}