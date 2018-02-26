package semaphore;

import common.TestThread;
import common.Tester;
import org.junit.*;
import threads.concurrent.AtomicDecrementThread;
import threads.concurrent.AtomicIncrementThread;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Created by Leshka on 26.02.18.
 */
public class testAcquireEnoughPermits {

    private MySemaphore sem;
    private Tester tester;
    private TestThread thread;



    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before testAcquireEnoughPermits.class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("After testAcquireEnoughPermits.class");
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
        //tester = new Tester(4, sem);
        thread = new TestThread(sem, 200);
    }

    @After
    public void tearDown() throws Exception {
        sem = null;
        tester = null;
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

    @Test
    public void methodTest() throws InterruptedException {
       tester.acquirePermits();
       assertFalse(sem.tryAcquire());
       tester.releasePermits(2);
       assertTrue(sem.tryAcquire());
       tester.releasePermits(2);
    }
     */

    @Test
    public void threeThreadsTest() {
        Thread firstThread = new Thread(thread, "firstThread");
        Thread secondThread = new Thread(thread, "secondThread");
        Thread thirdThread = new Thread(thread, "thirdThread");
        Thread fourthThread = new Thread(thread, "fourthThread");
        firstThread.start();
        //assertTrue(sem.tryAcquire());
        secondThread.start();
        thirdThread.start();
        fourthThread.start();
        try {
            thirdThread.join();
            secondThread.join();
            firstThread.join();
            firstThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(sem.tryAcquire());



    }

}