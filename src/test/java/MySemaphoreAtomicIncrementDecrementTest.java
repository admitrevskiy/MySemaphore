import org.junit.*;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Created by Leshka on 26.02.18.
 */
public class MySemaphoreAtomicIncrementDecrementTest {

    private MySemaphore sem;
    private AtomicInteger atomicInt;
    private AtomicIncrementThread incrementThread;
    private AtomicDecrementThread decrementThread;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before MySemaphoreAtomicIntegerIncrementTest.class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("After MySemaphoreAtomicIntegerIncrementTest.class");
    }

    /**
     * In this method, we create a semaphore with two permits.
     * In addition, we create two types of Runnable:
     * One thread that increments AtomicInteger, and another that decrements it;
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        sem = new MySemaphoreImpl(2);
        atomicInt = new AtomicInteger(0);
        incrementThread = new AtomicIncrementThread(sem, atomicInt, 2, 200);
        decrementThread = new AtomicDecrementThread(sem, atomicInt, 2, 200);

    }

    @After
    public void tearDown() throws Exception {
        sem = null;
        atomicInt = null;
    }

    /**
     * We're launching three threads with a 50ms break.
     * After 50ms trying to get AtomicInteger value.
     * The first thread increases the value by 1.
     * The second thread decreases the value by 2.
     * The third thread works on the same principle as the first
     * Therefore, if the semaphore is working correctly and only first two threads get inside, the value should equals -1;
     * Moreover, it shouldn't be any available permits;
     * After 100ms with two threads inside value should equals -2;
     * After the third thread will leave semaphore value should equals 0;
     * @throws InterruptedException
     */
    @Test
    public void getTest() throws InterruptedException {
        new Thread (incrementThread, "firstAtomicIncrementThread").start();
        Thread.sleep(50);
        new Thread (decrementThread, "firstAtomicDecrementThread").start();
        Thread.sleep(50);
        new Thread (incrementThread, "secondIncrementThread").start();
        Thread.sleep(50);
        assertEquals(-1, atomicInt.get());
        assertFalse(sem.tryAcquire());
        Thread.sleep(100);
        assertEquals(-2, atomicInt.get());
        Thread.sleep(400);
        assertEquals(0, atomicInt.get());
        assertTrue(sem.tryAcquire());
    }

}