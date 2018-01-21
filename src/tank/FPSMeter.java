package tank;

import java.util.concurrent.TimeUnit;

class FPSMeter {
    long framesInSecond = 0;
    long nextSecond = 0;
    final static long SECOND = TimeUnit.SECONDS.toNanos(1);

    protected void handle(long nanos) {
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
