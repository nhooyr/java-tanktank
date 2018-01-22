package tank;

import java.util.concurrent.TimeUnit;

class FPSMeter {
    private long framesInSecond = 0;
    private long nextSecond = 0;
    private final static long SECOND = TimeUnit.SECONDS.toNanos(1);

    void handle(final long nanos) {
        framesInSecond++;
        if (nextSecond == 0) {
            nextSecond = nanos + SECOND;
        } else if (nanos >= nextSecond) {
            nextSecond = nanos + SECOND;
            System.out.println(framesInSecond);
            framesInSecond = 0;
        }

    }
}
