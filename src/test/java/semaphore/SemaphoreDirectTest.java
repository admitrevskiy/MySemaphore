package semaphore;

import org.junit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Leshka on 28.02.18.
 */
public class SemaphoreDirectTest {

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before SemaphoreDirectTest.class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("After SemaphoreDirectTest.class");
    }


    @Test
    public void testTryAcquireEnoughPermits() throws InterruptedException {
        int permitsCount = 2;

        final CountDownLatch doneAcquireSignal = new CountDownLatch(permitsCount);

        final MySemaphore semaphore = new MySemaphoreImpl(permitsCount);
        final List<Boolean> results = new ArrayList<>();

        Runnable runnable = () -> {
            synchronized (results) {

                //Acquiring; Before each acquire boolean tryAcquire is added to results;
                results.add(semaphore.tryAcquire());
                try {
                    semaphore.acquire();
                    doneAcquireSignal.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // Create "acquiring" threads and start acquiring
        for (int i = 0; i < permitsCount; i++) {
            new Thread(runnable).start();
        }

        // Wait for all threads to finish acquiring
        assertTrue(doneAcquireSignal.await(1, TimeUnit.SECONDS));

        // Assert that all threads have successfully acquired permits
        assertEquals(permitsCount, results.size());
        for (boolean result : results) {
            assertTrue(result);
        }
    }

    @Test
    public void testTryAcquireNotEnoughPermits() throws InterruptedException {

        int threadCount = 3;
        final MySemaphore semWithTwoPerms = new MySemaphoreImpl(2);
        final CountDownLatch attemptAcquireSignal = new CountDownLatch(threadCount);
        final List<Boolean> results = new ArrayList<>();

        Runnable runnable = () -> {
            synchronized (results) {

                //Acquiring; Before each acquire boolean tryAcquire is added to results;
                results.add(semWithTwoPerms.tryAcquire());
                try {
                    attemptAcquireSignal.countDown();
                    semWithTwoPerms.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // Crete acquiring threads and starts acquiring trying
        for (int i = 0; i < threadCount; i++) {
            new Thread(runnable, "Thread " + i).start();
            System.out.println("Go-go, lil thread!");
        }

        // Wait for all threads to finish acquiring trying
        assertTrue(attemptAcquireSignal.await(2, TimeUnit.SECONDS));

        // Assert that first two threads have successfully acquired permits and last one haven't
        assertEquals(threadCount, results.size());
        assertTrue(results.get(0));
        assertTrue(results.get(1));
        assertFalse(results.get(2));
    }

    @Test
    public void testReleaseNoPermits() throws InterruptedException {
        int permitsCount = 0;
        int threadsCount = 2;
        int doubleCount = threadsCount*2;

        final MySemaphore semaphore = new MySemaphoreImpl(permitsCount);
        final List<Boolean> results = new ArrayList<>();

        final CountDownLatch doneAcquireSignal = new CountDownLatch(threadsCount);
        final CountDownLatch doneReleaseSignal = new CountDownLatch(threadsCount);

        Runnable acquiringRunnable = () -> {
            synchronized (results) {
                // Acquiring; Before acquire tryout boolean tryAcquire is added to results;
                results.add(semaphore.tryAcquire());
                try {
                    semaphore.acquire();
                    doneAcquireSignal.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable releaseRunnable = () -> {
            synchronized (results) {
                //Releasing; After each release boolean tryAcquire is added to results;
                semaphore.release();
                doneReleaseSignal.countDown();
                results.add(semaphore.tryAcquire());
            }
        };

        //Before all threads starts checkout that there is no available permits
        assertFalse(semaphore.tryAcquire());

        //Create threadCount  threads that calls release()
        for (int i = 0; i < threadsCount; i++) {
            new Thread(releaseRunnable).start();
        }

        //Waiting for all releases passed
        doneReleaseSignal.await(1, TimeUnit.SECONDS);

        //Assert that all available permits were at the list
        assertEquals(threadsCount, results.size());
        for (boolean result : results) {
            assertTrue(result);
        }
        //Create threadCount  threads that calls acquire()
        for (int i = 0; i < threadsCount; i++) {
            new Thread(acquiringRunnable).start();
        }
        //Waiting for all acquires passed
        doneAcquireSignal.await(1, TimeUnit.SECONDS);

        //Assert that all available permits are at the list
        assertEquals(doubleCount, results.size());
        assertFalse(semaphore.tryAcquire());
        for (boolean result : results) {
            assertTrue(result);
        }

    }

    /**
     * I use CopyOnWriteArrayList ints to check the correctness of the acquire() method.
     * First thread records beforeValue before it gets locked and afterValue after it gets unlocked.
     * At the next step another thread records blockedValue during first thread is waiting for permit.
     * Thus, the sequence of records is proof of locking the first stream.
     * @throws InterruptedException
     */

    @Test
    public void testAcquireNoPermits() throws InterruptedException {

        int beforeValue = 1;
        int blockedValue = 2;
        int afterValue = 3;

        int permitsCount = 0;

        final MySemaphore semaphore = new MySemaphoreImpl(permitsCount);

        final List<Boolean> results = new ArrayList<>();
        final CopyOnWriteArrayList<Integer> ints = new CopyOnWriteArrayList<>();

        Runnable acquiringRunnable = () -> {
            synchronized (results) {

                // Step 1: Acquiring; Before acquire tryout boolean tryAcquire is added to results;
                // Moreover, adding  "1" to ints. This should be first value in the List
                results.add(semaphore.tryAcquire());
                ints.add(beforeValue);
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Step 2: Release time.
                // Moreover, adding "3" to ints. This should be the third value in the List
                synchronized (results) {
                    semaphore.release();
                    results.add(semaphore.tryAcquire());
                    ints.add(afterValue);
                }
            }
        };

        Runnable releasingRunnable = () -> {

            // We release one permit from semaphore
            // Moreover, adding "2" to ints. This should be the second value in the List
            ints.add(blockedValue);
            semaphore.release();

        };

        // Create acquiring thread that should wait
        Thread acquiringThread = new Thread(acquiringRunnable);
        acquiringThread.start();

        // Create releasing thread
        new Thread(releasingRunnable).start();

        // Waiting for acquiring thread to finish it job
        acquiringThread.join();

        // Assert that first tryAcquire() was not successful and second one succeed
        assertEquals(2, results.size());
        assertFalse(results.get(0));
        assertTrue(results.get(1));

        // Assert that acquiring thread was first to recorded "1" to the list.
        // Then releasing thread recorded "2" to the list
        // Acquiring thread recorded "3" finally
        assertEquals(3, ints.size());
        assertTrue(ints.get(0)==beforeValue);
        assertTrue(ints.get(1)==blockedValue);
        assertTrue(ints.get(2)==afterValue);

        System.out.println(ints);
    }
}

