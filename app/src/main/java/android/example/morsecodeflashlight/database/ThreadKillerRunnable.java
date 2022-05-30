package android.example.morsecodeflashlight.database;

public class ThreadKillerRunnable implements Runnable {
    private final Thread t;

    public ThreadKillerRunnable(Thread t) {
        this.t = t;
    }

    @Override
    public void run() {
        if (t != null) {
            while (t.isAlive()) {
            }
            t.interrupt();
        }
    }
}
