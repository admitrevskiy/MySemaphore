import org.junit.*;
import semaphore.MySemaphore;
import semaphore.MySemaphoreImpl;
import threads.concurrent.ProducerFinishThread;

import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.Assert.*;

/**
 * Created by Leshka on 26.02.18.
 */
public class MySemaphoreTwoSetsTest {
    private MySemaphore sem;
    private CopyOnWriteArraySet<Integer> set;
    private CopyOnWriteArraySet<Integer> finishSet;
    private ProducerFinishThread firstThread;
    private ProducerFinishThread secondThread;
    private ProducerFinishThread thirdThread;
    private ProducerFinishThread fourthThread;
    private ProducerFinishThread fifthThread;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before MySemaphoreAtomicIntegerIncrementTest.class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("After MySemaphoreAtomicIntegerIncrementTest.class");
    }

    /**
     * In this method, we create a semaphore with three permits.
     * In addition we create five threads that puts values into the set and after timeout puts values into the finishSet;
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        sem = new MySemaphoreImpl(3);
        set = new CopyOnWriteArraySet<>();
        finishSet = new CopyOnWriteArraySet<>();
        firstThread = new ProducerFinishThread(sem, set, finishSet, 1, 500);
        secondThread = new ProducerFinishThread(sem, set, finishSet, 2, 200);
        thirdThread = new ProducerFinishThread(sem, set, finishSet, 3, 100);
        fourthThread = new ProducerFinishThread(sem, set, finishSet, 4, 100);
        fifthThread = new ProducerFinishThread(sem, set, finishSet, 5, 100);
    }

    @After
    public void tearDown() throws Exception {
        sem = null;
        set = null;
        finishSet = null;
    }

    /**
     * We're launching three threads.
     * After 50ms trying to launch two more threads, if semaphore works correctly only first three threads should gets into it.
     * After 20ms trying to get CopyOnWriteCollection size.
     * Each thread increases size by 1.
     * Therefore, if the semaphore is working correctly and only three threads get inside, the set size should equals 3 and finishSet size should equals zero;
     * After 60ms when thread with 100ms timeout leaving semaphore and another one gets inside, set.size() should equals 4 and finishSet.size() should equals 1;
     * After 100ms when threads with 100ms and 200ms leaving semaphore, set.size() should equals 5 and finishSet.size() should equals 3;
     * After 300ms when all threads leaving semaphore set and finishSet sizes should equals 5;
     * @throws InterruptedException
     */
    @Test
    public void getTest() throws InterruptedException {
        new Thread (firstThread, "firstProducerThread").start();
        new Thread (secondThread, "secondProducerThread").start();
        new Thread (thirdThread, "thirdProducerThread").start();
        Thread.sleep(50);
        new Thread (fourthThread, "fourthProducerThread").start();
        new Thread (fifthThread, "fifthProducerThread").start();
        Thread.sleep(20);
        assertEquals(3, set.size());
        assertEquals(0, finishSet.size());
        Thread.sleep(60);
        assertEquals(4, set.size());
        assertEquals(1, finishSet.size());
        Thread.sleep(100);
        assertEquals(5, set.size());
        assertEquals(3, finishSet.size());
        Thread.sleep(300);
        assertEquals(5, set.size());
        assertEquals(5, finishSet.size());


    }

}