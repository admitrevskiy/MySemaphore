package common;

import semaphore.MySemaphore;

/**
 * Created by Leshka on 26.02.18.
 */
public class Tester {

    /**
     * Number of permits to acquire
     */
    private int acquiredPermits;
    private int releasedPermits;

    private MySemaphore sem;

    public void releasePermits(int remainingPermits) {
        for (int i = 0; i < remainingPermits; i++) {
            sem.release();
        }
        releasedPermits+= remainingPermits;
        System.out.println("Tester released " + remainingPermits + " permits.");
    }

    public Tester(int acquiredPermits, MySemaphore sem) {
        this.acquiredPermits = acquiredPermits;
        this.releasedPermits = 0;
        this.sem = sem;
    }

    public void acquirePermits() {
        for (int i = 0; i < acquiredPermits; i++) {
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(acquiredPermits + " permits are acquired");
    }


}
