package semaphore;

import org.junit.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Leshka on 28.02.18.
 * Any uncaught exception in the thread will cause the program to crash,
 * so we throw new RuntimeException for each catch (InterruptedException e)
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
        int permitsCount = 4;

        final CountDownLatch doneAcquireSignal = new CountDownLatch(permitsCount/2);

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
                    throw new RuntimeException(e);
                }
            }
        };

        // Create "acquiring" threads and start acquiring
        for (int i = 0; i < 2; i++) {
            new Thread(runnable).start();
        }

        // Wait for all threads to finish acquiring
        assertTrue(doneAcquireSignal.await(1, TimeUnit.SECONDS));

        // Assert that all threads have successfully acquired permits
        assertEquals(permitsCount/2, results.size());
        for (boolean result : results) {
            assertTrue(result);
        }
    }

    @Test
    public void testTryAcquireNotEnoughPermits() throws InterruptedException {

        int threadCount = 3;
        final MySemaphore semWithTwoPerms = new MySemaphoreImpl(4);
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
                    throw new RuntimeException(e);
                }
            }
        };

        // Crete acquiring threads and starts acquiring trying
        for (int i = 0; i <threadCount; i++) {
            new Thread(runnable, "Thread " + i).start();
            System.out.println("Go-go, lil thread!");
        }

        // Wait for all threads to finish acquiring trying
        assertTrue(attemptAcquireSignal.await(1, TimeUnit.SECONDS));

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

        int releaseValue = 2;
        int afterBlockValue = 3;
        CopyOnWriteArrayList<Integer> enters = new CopyOnWriteArrayList<>();

        Runnable acquiringRunnable = () -> {
            synchronized (results) {
                // Acquiring; After acquire boolean int afterBlockValue is added to enters;
                try {
                    semaphore.acquire();
                    enters.add(afterBlockValue);
                    doneAcquireSignal.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Runnable releaseRunnable = () -> {
            synchronized (results) {
                //Releasing; After each release int releaseValue is added to enters;
                semaphore.release();
                enters.add(releaseValue);
                doneReleaseSignal.countDown();

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
        assertEquals(2, enters.size());
        for (int result : enters) {
            assertEquals(releaseValue, result);
        }
        //Create threadCount  threads that calls acquire()
        for (int i = 0; i < threadsCount; i++) {
            new Thread(acquiringRunnable).start();
        }
        //Waiting for all acquires passed
        doneAcquireSignal.await(1, TimeUnit.SECONDS);

        assertEquals(4, enters.size());
        assertFalse(semaphore.tryAcquire());
        for (int i = 2; i < 4; i++) {
            assertTrue(afterBlockValue==enters.get(i));
        }
    }

    /**
     * Semaphore with 0 permissions simulate full semaphore.
     * releasingRunnable simulates thread that stops it work.
     * I use CopyOnWriteArrayList ints to check the correctness of the acquire() method.
     * First thread records beforeValue before it gets locked and afterValue after it gets unlocked.
     * At the next step another thread records blockedValue during first thread is waiting for permit.
     * Thus, the sequence of records is proof of locking the first stream.
     *
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
        final CountDownLatch latch = new CountDownLatch(1);

        Runnable acquiringRunnable = () -> {
            synchronized (results) {

                // Step 1: Acquiring; Before acquire tryout boolean tryAcquire is added to results;
                // Moreover, adding  "1" to ints. This should be first value in the List
                results.add(semaphore.tryAcquire());
                ints.add(beforeValue);
                try {
                    latch.countDown();
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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
            try {
                latch.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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

    /**
     * The same logic with callable
     * @throws InterruptedException
     */
    @Test
    public void testCallableAcquireNoPermits() throws InterruptedException {

        int beforeValue = 1;
        int blockedValue = 2;
        int afterValue = 3;

        int permitsCount = 0;

        final MySemaphore semaphore = new MySemaphoreImpl(permitsCount);
        final CountDownLatch doneAcquireSignal = new CountDownLatch(1);
        final ScheduledExecutorService es = Executors.newScheduledThreadPool(2);

        final List<Boolean> results = new ArrayList<>();
        final CopyOnWriteArrayList<Integer> ints = new CopyOnWriteArrayList<>();

        Callable<Boolean> acquiringCallable = () -> {
            synchronized (results) {
                // Step 1: Acquiring; Before acquire tryout boolean tryAcquire is added to results;
                // Moreover, adding  "1" to ints. This should be first value in the List
                results.add(semaphore.tryAcquire());
                System.out.println("The acquiring thread is going to sleep");
                ints.add(beforeValue);
                doneAcquireSignal.countDown();
                semaphore.acquire();
            }


            // Step 2: Release time.
            // Moreover, adding "3" to ints. This should be the third value in the List
            synchronized (results) {
                System.out.println("The acquiring thread wakes up");
                results.add(semaphore.tryAcquire());
                ints.add(afterValue);
                semaphore.release();


            }
            return true;
        };

        Callable<Boolean> releasingCallable = () -> {

            // We release one permit from semaphore
            // Moreover, adding "2" to ints. This should be the second value in the List
            System.out.println("The releasing thread is going to give a permit");
            ints.add(blockedValue);
            semaphore.release();

            return true;

        };

        Future<Boolean> acquiringFuture = es.submit(acquiringCallable);

        // Waiting for acquiring thread to enter the semaphore.
        assertTrue(doneAcquireSignal.await(1, TimeUnit.SECONDS));

        ScheduledFuture<Boolean> releasingFuture = es.schedule(releasingCallable, 100, TimeUnit.MILLISECONDS);

        while (!acquiringFuture.isDone() && !releasingFuture.isDone()) {
            System.out.println("Waiting");
        }

        // Assert that tryAcquire() was not successful
        assertEquals(2, results.size());
        assertFalse(results.get(0));
        assertFalse(results.get(1));

        // Assert that acquiring thread was first to recorded "1" to the list.
        // Then releasing thread recorded "2" to the list
        // Acquiring thread recorded "3" finally
        assertEquals(3, ints.size());
        assertTrue(ints.get(0)==beforeValue);
        assertTrue(ints.get(1)==blockedValue);
        assertTrue(ints.get(2)==afterValue);

        System.out.println(ints);

        es.shutdown();
    }


    /**
     * Semaphore with 1 permissions
     * acquiringThread gets available permit and waits for releasing signal.
     * I use CopyOnWriteArrayList ints to check the correctness of the acquire() method.
     * First thread records beforeValue before it gets permit and afterValue after it calls release().
     * At the next step another thread records blockedValue during first thread is waiting for releaseSignal and get wait();
     * First thread get releaseSignal, release permit and recorded afterValue to ints.
     * Another thread get permit and records finishValue into ints.
     * Thus, the sequence of records is proof of locking the first stream.
     *
     * @throws InterruptedException
     */

    @Test
    public void testAcquireOnePermit() throws InterruptedException {

        int beforeValue = 1;
        int blockedValue = 2;
        int afterValue = 3;
        int finishValue = 4;

        int permitsCount = 1;

        final MySemaphore semaphore = new MySemaphoreImpl(permitsCount);
        final CountDownLatch doneAcquireSignal = new CountDownLatch(permitsCount);
        final CountDownLatch doneBlockSignal = new CountDownLatch(permitsCount);
        final CountDownLatch startReleaseSignal = new CountDownLatch(1);

        final List<Boolean> results = new ArrayList<>();
        final CopyOnWriteArrayList<Integer> enters = new CopyOnWriteArrayList<>();

        Runnable acquiringRunnable = () -> {
            synchronized (enters) {

                // Step 1: Acquiring;
                // Moreover, adding  "1" to ints. This should be first value in the List
                enters.add(beforeValue);
                System.out.println(enters);
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                doneAcquireSignal.countDown();

                try {
                    assertTrue(startReleaseSignal.await(1, TimeUnit.SECONDS));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Step 2: Release time.
                // Moreover, adding "3" to ints. This should be the third value in the List
                synchronized (enters) {
                    semaphore.release();
                    enters.add(afterValue);
                    System.out.println(enters);
                }
            }
        };

        Runnable blockedRunnable = () -> {

            // Step 1: Acquiring; Before acquire tryout boolean tryAcquire is added to results;
            // Moreover, adding  "1" to ints. This should be first value in the List;
            synchronized (results) {
                enters.add(blockedValue);
                System.out.println(enters);
                doneBlockSignal.countDown();
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // Step 2: Release time.
            // Moreover, adding "4" to ints. This should be the third value in the List
            synchronized (results) {
                enters.add(finishValue);
                semaphore.release();
                System.out.println(enters);
            }

        };


        // Create acquiring thread that should wait
        Thread acquiringThread = new Thread(acquiringRunnable);
        acquiringThread.start();

        // Waiting for acquiring thread to enter the semaphore.
        assertTrue(doneAcquireSignal.await(1, TimeUnit.SECONDS));

        //Assert that there is no available permits
        assertFalse(semaphore.tryAcquire());

        //Running thread that should be blocked
        Thread blockedThread = new Thread(blockedRunnable);
        blockedThread.start();


        assertTrue(doneBlockSignal.await(1,TimeUnit.SECONDS));

        //Time to release for thread that gets permit
        startReleaseSignal.countDown();

        //Waiting for all threads to finish their job
        acquiringThread.join();
        blockedThread.join();

        // Assert that acquiring thread was first to recorded "1" to the list.
        // Then blocked thread recorded "2" to the list before tryout to get permit.
        // Acquiring thread recorded "3" after release.
        // Blocked thread recorded "4" after acquiring thread calls notify();
        assertEquals(4, enters.size());
        assertTrue(enters.get(0)==beforeValue);
        assertTrue(enters.get(1)==blockedValue);
        assertTrue(enters.get(2)==afterValue);
        assertTrue(enters.get(3)==finishValue);

        System.out.println(enters);
    }


    /**
     * The same test but using callable!
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void testCallableAcquireOnePermit() throws InterruptedException, ExecutionException {

        int beforeValue = 1;
        int blockedValue = 2;
        int afterValue = 3;
        int finishValue = 4;

        int permitsCount = 1;

        final MySemaphore semaphore = new MySemaphoreImpl(permitsCount);
        final CountDownLatch doneAcquireSignal = new CountDownLatch(permitsCount);
        final CountDownLatch doneBlockSignal = new CountDownLatch(permitsCount);
        final CountDownLatch startReleaseSignal = new CountDownLatch(1);
        final ExecutorService es = Executors.newFixedThreadPool(2);

        final List<Boolean> results = new ArrayList<>();
        final CopyOnWriteArrayList<Integer> enters = new CopyOnWriteArrayList<>();

        Callable<Boolean> acquiringCallable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                // Step 1: Acquiring; Before acquire tryout boolean tryAcquire is added to results;
                // Moreover, adding  "1" to ints. This should be first value in the List

                //results.add(semaphore.tryAcquire());
                enters.add(beforeValue);
                semaphore.acquire();
                doneAcquireSignal.countDown();

                assertTrue(startReleaseSignal.await(1, TimeUnit.SECONDS));

                // Step 2: Release time.
                // Moreover, adding "3" to ints. This should be the third value in the List
                synchronized (enters) {
                    semaphore.release();
                    enters.add(afterValue);
                    //results.add(semaphore.tryAcquire());
                    System.out.println(enters);
                }
                return true;
            }
        };

        Callable<Boolean> blockedCallable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                // Step 1: Acquiring; Before acquire tryout boolean tryAcquire is added to results;
                // Moreover, adding  "1" to ints. This should be first value in the List
                results.add(semaphore.tryAcquire());
                enters.add(blockedValue);
                System.out.println(enters);

                doneBlockSignal.countDown();
                semaphore.acquire();

                // Step 2: Release time.
                // Moreover, adding "3" to ints. This should be the third value in the List
                synchronized (enters) {
                    enters.add(finishValue);
                    semaphore.release();
                    results.add(semaphore.tryAcquire());
                    System.out.println(enters);
                }
                return true;
            }
        };

        Future<Boolean> acquiringFuture = es.submit(acquiringCallable);

        // Waiting for acquiring thread to enter the semaphore.
        assertTrue(doneAcquireSignal.await(1, TimeUnit.SECONDS));

        //Assert that there is no available permits
        assertFalse(semaphore.tryAcquire());

        Future<Boolean> blockedFuture = es.submit(blockedCallable);

        // Waiting for acquiring thread to tryout enter the semaphore.
        assertTrue(doneBlockSignal.await(1,TimeUnit.SECONDS));

        //Time to release for thread that gets permit
        startReleaseSignal.countDown();

        //Assert that there is no available permits until threads complete their work
        while(!acquiringFuture.isDone() && !blockedFuture.isDone()) {
            System.out.println("wait for all threads");
        }

        //Both call() returns true =)
        assertTrue(acquiringFuture.get());
        assertTrue(blockedFuture.get());

        //Assert that there is available permit after all threads stopped
        //assertTrue(semaphore.tryAcquire());

        // Assert that acquiring thread was first to recorded "1" to the list.
        // Then blocked thread recorded "2" to the list before tryout to get permit.
        // Acquiring thread recorded "3" after release.
        // Blocked thread recorded "4" after acquiring thread calls notify();
        assertEquals(4, enters.size());
        assertTrue(enters.get(0)==beforeValue);
        assertTrue(enters.get(1)==blockedValue);
        assertTrue(enters.get(2)==afterValue);
        assertTrue(enters.get(3)==finishValue);

        System.out.println(enters);

        es.shutdown();
    }

    @Test
    public void testTryAcquireNoPermits() throws InterruptedException {

        final MySemaphore sem = new MySemaphoreImpl(0);
        final CountDownLatch latch = new CountDownLatch(1);

        Runnable releasingRunnable = () -> {
            // We release one permit from semaphore
            sem.release();
            latch.countDown();
        };

        //Assert that before start releasingRunnable there is no available permits
        assertFalse(sem.tryAcquire());
        new Thread(releasingRunnable).start();

        //Wait for releasingThread to release
        latch.await(1, TimeUnit.SECONDS);

        //Assert that after release there is one available permit, that should be acquired after first tryAcquire()
        assertTrue(sem.tryAcquire());
        assertFalse(sem.tryAcquire());
    }

    @Test
    public void testTryAcquireOnePermit() throws InterruptedException {

        final MySemaphore sem = new MySemaphoreImpl(1);
        final CountDownLatch latch = new CountDownLatch(2);

        Runnable releasingRunnable = () -> {
            // We release one permit from semaphore
            sem.release();
            latch.countDown();
        };

        //Assert that before start releasingRunnable there is no available permits
        assertTrue(sem.tryAcquire());
        assertFalse(sem.tryAcquire());

        new Thread(releasingRunnable).start();
        new Thread(releasingRunnable).start();

        //Wait for releasingThread to release
        latch.await(1, TimeUnit.SECONDS);

        //Assert that after release there is one available permit, that should be acquired after first tryAcquire()
        assertTrue(sem.tryAcquire());
        assertTrue(sem.tryAcquire());
    }

    @Test
    public void testTryAcquireTwoPermitsWithAcquire() throws InterruptedException {

        final MySemaphore sem = new MySemaphoreImpl(2);
        final CountDownLatch acquireDoneSignal = new CountDownLatch(2);
        final CountDownLatch releaseStartSignal = new CountDownLatch(1);
        final CountDownLatch releaseStopSignal = new CountDownLatch(2);

        //Thread acquires permit, countDown acquireSignal, wait for releaseStartSignal release semaphore
        Runnable releasingRunnable = () -> {
            try {
                sem.acquire();
                acquireDoneSignal.countDown();
                releaseStartSignal.await(1, TimeUnit.SECONDS);
                sem.release();
                releaseStopSignal.countDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        //Start two threads that acquires semaphore
        new Thread(releasingRunnable).start();
        new Thread(releasingRunnable).start();

        //Wait for threads to countdown
        acquireDoneSignal.await(1, TimeUnit.SECONDS);

        //Assert that after both threads acquiring semaphore there is no available permits
        assertFalse(sem.tryAcquire());

        //Start releasing for threads
        releaseStartSignal.countDown();

        //Waiting for all threads to release
        releaseStopSignal.await(1, TimeUnit.SECONDS);

        //Assert that there are only two available permits after all threads release();
        assertTrue(sem.tryAcquire());
        assertTrue(sem.tryAcquire());
        assertFalse(sem.tryAcquire());
    }

}

