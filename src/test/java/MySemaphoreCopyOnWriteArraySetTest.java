import org.junit.*;
import semaphore.MySemaphore;
import semaphore.MySemaphoreImpl;
import threads.concurrent.ProducerThread;

import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.Assert.*;

/**
 * Created by Leshka on 26.02.18.
 */
public class MySemaphoreCopyOnWriteArraySetTest {

    private MySemaphore sem;
    private CopyOnWriteArraySet<Integer> set;
    private ProducerThread firstThread;
    private ProducerThread secondThread;
    private ProducerThread thirdThread;
    private ProducerThread fourthThread;
    private ProducerThread fifthThread;

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
        sem = new MySemaphoreImpl(3);
        set = new CopyOnWriteArraySet<>();
        firstThread = new ProducerThread(sem, set, 1, 200);
        secondThread = new ProducerThread(sem, set, 2, 200);
        thirdThread = new ProducerThread(sem, set, 3, 200);
        fourthThread = new ProducerThread(sem, set, 4, 200);
        fifthThread = new ProducerThread(sem, set, 5, 200);
    }

    @After
    public void tearDown() throws Exception {
        sem = null;
        set = null;
    }

    /**
     * We're launching five threads.
     * After 50ms trying to get CopyOnWriteCollection size.
     * Each thread increases size by 1.
     * Therefore, if the semaphore is working correctly and only three threads get inside, the size should equals 3;
     * Moreover, it shouldn't be any available permits;
     * After 200ms after top three threads leaving semaphore and another two gets inside, set.size() should equals 5;
     * @throws InterruptedException
     */
    @Test
    public void getTest() throws InterruptedException {
        new Thread (firstThread, "firstProducerThread").start();
        new Thread (secondThread, "secondProducerThread").start();
        new Thread (thirdThread, "thirdProducerThread").start();
        new Thread (fourthThread, "fourthProducerThread").start();
        new Thread (fifthThread, "fifthProducerThread").start();
        Thread.sleep(50);
        assertFalse(sem.tryAcquire());
        assertEquals(3, set.size());
        Thread.sleep(200);
        assertEquals(5, set.size());
    }

}