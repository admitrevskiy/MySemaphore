package semaphore;

import common.TestAtomicThread;
import org.junit.*;


import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Created by Leshka on 26.02.18.
 */
public class SemaphoreReleaseTest {

    private MySemaphore semTwoPermits;
    private MySemaphore semNoPermits;
    private TestAtomicThread thread;
    private AtomicInteger startInt, stopInt;


    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before SemaphoreTryAcquireTest.class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("After SemaphoreTryAcquireTest.class");
    }

    /**
     * In this method, we create two semaphores with two and 0 permits.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        semTwoPermits = new MySemaphoreImpl(2);
        semNoPermits = new MySemaphoreImpl(0);
        startInt = new AtomicInteger(0);
        stopInt = new AtomicInteger(0);
    }

    @After
    public void tearDown() throws Exception {
        semTwoPermits = null;
        semNoPermits = null;
        stopInt = null;
        startInt = null;
    }

    /**
     * Check the semaphore logic for two permits and four threads;
     * Thread.join(100) is used to control which thread will get permission first;
     * After the first two threads are finished, there should be no permits;
     * After all threads are finished, there must be зукьшеы
     * @throws InterruptedException
     */
    @Test
    public void testTryAcquireFourThreadsTwoPermits() throws InterruptedException {
        thread = new TestAtomicThread(semTwoPermits, 1000, startInt, stopInt);

        Thread firstThread = new Thread(thread, "firstThread");
        Thread secondThread = new Thread(thread, "secondThread");
        Thread thirdThread = new Thread(thread, "thirdThread");
        Thread fourthThread = new Thread(thread, "fourthThread");

        try {
            firstThread.start();
            secondThread.start();

            firstThread.join(100);

            thirdThread.start();
            fourthThread.start();

            firstThread.join();
            secondThread.join();

            assertFalse(semTwoPermits.tryAcquire());
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        try {
            thirdThread.join(1000);
            fourthThread.join(1000);
            assertTrue(semTwoPermits.tryAcquire());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** I want to emphasize that this is a partially indirect test
     *
     * Create two threads and pass them the semaphore without permission
     * After starting waiting for timeout or the end of the thread works
     * startInt and stopInt are AtomicIntegers, which threads extend respectively at start and stop of work.
     * Thus, values of startInt and stopInt should equals zero , if during the timeout the threads could not enter the semaphore and did not perform their work.
     * After call release() method on the semaphore, one permit becomes available;
     * There must be available permit after the completion of threads.
     * In addition, completed threads must change the value of the variables startInt and stopInt
     */
    @Test
    public void testTryAcquireTwoThreadsNoPermitsWithRelease()  {
        thread = new TestAtomicThread(semNoPermits, 1000, startInt, stopInt);

        Thread firstThread = new Thread(thread, "firstThread");
        Thread secondThread = new Thread(thread, "secondThread");

        firstThread.start();
        secondThread.start();

        try {
            firstThread.join(1000);
            secondThread.join(1000);

            assertFalse(semNoPermits.tryAcquire());

            assertEquals(0, stopInt.get());
            assertEquals(0, startInt.get());

            semNoPermits.release();

            firstThread.join();
            secondThread.join();

            assertTrue(semNoPermits.tryAcquire());
            assertEquals(2, stopInt.get());
            assertEquals(2, startInt.get());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
