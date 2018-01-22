package tank;

import java.util.concurrent.TimeUnit;

class FPSMeter {
    private static final long SECOND = TimeUnit.SECONDS.toNanos(1);
    private long framesInSecond = 0;
    private long nextSecond = 0;

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
