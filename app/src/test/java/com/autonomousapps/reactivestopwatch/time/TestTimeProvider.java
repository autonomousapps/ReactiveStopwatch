package com.autonomousapps.reactivestopwatch.time;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestTimeProvider implements TimeProvider {

    private final List<Long> queuedNows = new ArrayList<>();

    private long now = 0L;

    @Override
    public long now() {
        if (!queuedNows.isEmpty()) {
            now = queuedNows.get(0);
            queuedNows.remove(0);
        }

        return now;
    }

    // int / long issues?
    public void advanceTimeBy(long l) {
        for (int i = 1; i <= l; i++) {
            queuedNows.add(now + i);
        }
    }

    public void advanceTimeTo(long l) {
        if (l <= now()) {
            throw new IllegalArgumentException(
                    String.format(Locale.US, "%d cannot be less than or equal to current time, which is %d", l, now));
        }

        queuedNows.add(l);
    }
}
