package semaphore;

import testers.*;
import org.junit.*;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Created by Leshka on 26.02.18.
 */
public class SemaphoreIndirectTest {

    private PermitsInvader invaderForTwoPermits;
    private StoppableThread threadForTwoPermitsTest;
    private StoppableThreadWithResult threadForTwoPermitsResultTest;
    private AtomicInteger result;
    private MySemaphore semTwoPermits, semOnePermit, semNoPermits;
    private TestThreadWithTimeoutAndResult threadForTestWithTimeoutAndResult;
    private TestThreadWithTimeout threadForTestWithTimeout;
    private AtomicInteger startInt, stopInt;


    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before SemaphoreIndirectTest.class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("After SemaphoreIndirectTest.class");
    }

    @Before
    public void setUp() {

        semTwoPermits = new MySemaphoreImpl(2);
        semOnePermit = new MySemaphoreImpl(1);
        semNoPermits = new MySemaphoreImpl(0);

        startInt = new AtomicInteger(0);
        stopInt = new AtomicInteger(0);
        result = new AtomicInteger(0);

        invaderForTwoPermits = new PermitsInvader(semTwoPermits);

        threadForTestWithTimeout = new TestThreadWithTimeout(semTwoPermits, 1000);
        threadForTestWithTimeoutAndResult = new TestThreadWithTimeoutAndResult(semNoPermits, 1000, startInt, stopInt);

        threadForTwoPermitsTest = new StoppableThread(semTwoPermits);
        threadForTwoPermitsResultTest = new StoppableThreadWithResult(semTwoPermits, result);
    }

    @After
    public void tearDown() throws Exception {
        semTwoPermits = null;
        semOnePermit = null;
        semNoPermits = null;
        stopInt = null;
        startInt = null;
        result = null;
        invaderForTwoPermits = null;
        threadForTestWithTimeout = null;
        threadForTestWithTimeoutAndResult = null;
        threadForTwoPermitsResultTest = null;
        threadForTwoPermitsTest = null;

    }

    /**
     * Check the semaphore logic for two permits and four threads;
     * Thread.join(100) is used to control which thread will get permission first;
     * After the first two threads are finished, there should be no permits;
     * After all threads are finished, there must be available permits
     * @throws InterruptedException
     */
    @Test
    public void testTryAcquireFourThreadsTwoPermits() throws InterruptedException {

        Thread firstThread = new Thread(threadForTestWithTimeout, "firstThread");
        Thread secondThread = new Thread(threadForTestWithTimeout, "secondThread");
        Thread thirdThread = new Thread(threadForTestWithTimeout, "thirdThread");
        Thread fourthThread = new Thread(threadForTestWithTimeout, "fourthThread");

        try {
            firstThread.start();
            secondThread.start();

            firstThread.join(100);

            thirdThread.start();
            fourthThread.start();

            firstThread.join();
            secondThread.join();

//            assertFalse(semTwoPermits.tryAcquire());

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
    public void testTryAcquireTwoThreadsNoPermitsWithReleaseAndResult()  {

        Thread firstThread = new Thread(threadForTestWithTimeoutAndResult, "firstThread");
        Thread secondThread = new Thread(threadForTestWithTimeoutAndResult, "secondThread");

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

    /**
     * Test using two threads that capture all available permits.
     * After starting, wait for the timeout and check that there is no available permits.
     * Report runnable object threadForTwoPermitsTest, that it's time to stop waiting cycle, wait for the end of the threads working and checkout available permits
     */
    @Test
    public void testAcquireEnoughPermits() throws InterruptedException {
        Thread firstThread = new Thread(threadForTwoPermitsTest, "FirstThread");
        Thread secondThread = new Thread(threadForTwoPermitsTest, "SecondThread");

        firstThread.start();
        secondThread.start();

        firstThread.join(1000);
        secondThread.join(1000);

        assertFalse(semTwoPermits.tryAcquire());

        threadForTwoPermitsTest.setStopped(true);

        firstThread.join();
        secondThread.join();

        assertTrue(semTwoPermits.tryAcquire());

    }

    /**
     * I want to emphasize that this is a partially indirect test.
     *
     * The invader gets all available permissions for the semaphore.
     * After waiting for the timeout, check that there are no available permissions and the value of the result variable has not changed.
     * The invader releases one resolution, one of the threads enters the semaphore and gets on the waiting cycle.
     * Thus, the included in the semaphore thread can not change the value of the result.
     * Report runnable object threadForTwoPermitsResultTest, that it's time to stop waiting cycle, wait for the end of the threads working and checkout available permits and result value;
     *
     */
    @Test
    public void testAcquireWithInvaderAndResult() {
        Thread firstThread = new Thread(threadForTwoPermitsResultTest, "FirstThread");
        Thread secondThread = new Thread(threadForTwoPermitsResultTest, "SecondThread");
        invaderForTwoPermits.invasion(2);

        firstThread.start();
        secondThread.start();

        try {
            firstThread.join(1000);
            secondThread.join(1000);

            assertFalse(semTwoPermits.tryAcquire());
            assertEquals(0, result.get());

            invaderForTwoPermits.retreat(1);

            firstThread.join(2000);
            secondThread.join(2000);

            assertEquals(0, result.get());

            threadForTwoPermitsResultTest.setStopped(true);

            firstThread.join();
            secondThread.join();

            assertTrue(semTwoPermits.tryAcquire());
            assertEquals(2, result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}