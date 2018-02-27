package semaphore;

import common.PermitsInvader;
import common.StoppableThread;
import common.StoppableThreadWithResult;
import org.junit.*;


import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Created by Leshka on 27.02.18.
 */
public class SemaphoreAcquireTest {


    private MySemaphore semTwoPermits;
    private MySemaphore semOnePermit;
    private MySemaphore semNoPermits;
    private PermitsInvader invaderForTwoPermits;
    private PermitsInvader invaderForOnePermit;
    private PermitsInvader invaderForNoPermits;
    private StoppableThread threadForTwoPermitsTest;
    private StoppableThreadWithResult threadForTwoPermitsResultTest;
    private AtomicInteger result;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before SemaphoreAcquireTest.class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("After SemaphoreAcquireTest.class");
    }

    @Before
    public void setUp() {
        result = new AtomicInteger(0);
        semTwoPermits = new MySemaphoreImpl(2);
        semOnePermit = new MySemaphoreImpl(1);
        semNoPermits = new MySemaphoreImpl(0);
        invaderForTwoPermits = new PermitsInvader(semTwoPermits);
        invaderForOnePermit = new PermitsInvader(semOnePermit);
        invaderForNoPermits = new PermitsInvader(semNoPermits);
        threadForTwoPermitsTest = new StoppableThread(semTwoPermits);
        threadForTwoPermitsResultTest = new StoppableThreadWithResult(semTwoPermits, result);
    }

    @After
    public void tearDown() {
        semTwoPermits = null;
        semOnePermit = null;
        semNoPermits = null;
        invaderForTwoPermits = null;
        invaderForOnePermit = null;
        invaderForNoPermits = null;
    }


}
