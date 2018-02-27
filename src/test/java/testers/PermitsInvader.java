package testers;

import semaphore.MySemaphore;

/**
 * Created by Leshka on 27.02.18.
 */
public class PermitsInvader {

    private MySemaphore sem;

    public PermitsInvader(MySemaphore sem) {
        this.sem = sem;
    }

    public void invasion(int permits) {
        for (int i = 0; i < permits; i++) {
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void retreat(int releases) {
        for (int i = 0; i < releases; i++) {
            sem.release();
        }
    }
}
