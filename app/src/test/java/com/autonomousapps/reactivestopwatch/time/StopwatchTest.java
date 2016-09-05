package com.autonomousapps.reactivestopwatch.time;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import rx.schedulers.TestScheduler;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StopwatchTest {

    private TestScheduler testScheduler;

    private Stopwatch stopwatch;

    @Before
    public void setup() throws Exception {
        stopwatch = new Stopwatch();

        testScheduler = new TestScheduler();
        stopwatch.setScheduler(testScheduler);
    }

    @After
    public void teardown() throws Exception {
        stopwatch.reset();
    }

    @Test
    public void timerStartsWith1() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start(timeWatcher::onNext);

        // Exercise
        tick();

        // Verify
        assertThat(timeWatcher.getCurrentTime(), is(1L));
    }

    @Test
    public void timerAccuratelyTicksTheSeconds() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start(timeWatcher::onNext);

        // Exercise & verify repeatedly
        tick();
        assertThat(timeWatcher.getCurrentTime(), is(1L));

        tick();
        assertThat(timeWatcher.getCurrentTime(), is(2L));

        tick();
        assertThat(timeWatcher.getCurrentTime(), is(3L));

        tick();
        assertThat(timeWatcher.getCurrentTime(), is(4L));

        tick();
        assertThat(timeWatcher.getCurrentTime(), is(5L));
    }

    @Test
    public void timerAccuratelyAdvances5s() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start(timeWatcher::onNext);

        // Exercise
        advanceTimeBy(5L);

        // Verify
        assertThat(timeWatcher.getCurrentTime(), is(5L));
    }

    @Test
    public void pausePauses() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start(timeWatcher::onNext);
        tick();
        assertThat(timeWatcher.getCurrentTime(), is(1L));

        // Exercise
        stopwatch.togglePause();

        // Verify repeatedly
        tick();
        assertThat(timeWatcher.getCurrentTime(), is(1L));

        tick();
        assertThat(timeWatcher.getCurrentTime(), is(1L));

        tick();
        assertThat(timeWatcher.getCurrentTime(), is(1L));

        tick();
        assertThat(timeWatcher.getCurrentTime(), is(1L));

        tick();
        assertThat(timeWatcher.getCurrentTime(), is(1L));
    }

    @Test
    public void pausingTwiceTogglesPauseOff() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start(timeWatcher::onNext);
        tick();
        assertThat(timeWatcher.getCurrentTime(), is(1L));
        stopwatch.togglePause();
        advanceTimeBy(5L); // arbitrary

        // Exercise
        stopwatch.togglePause();
        tick();

        // Verify
        assertThat(timeWatcher.getCurrentTime(), is(2L));
    }

    @Test
    public void pausingThriceRepauses() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start(timeWatcher::onNext);
        tick();
        assertThat(timeWatcher.getCurrentTime(), is(1L));
        // 1
        stopwatch.togglePause();
        advanceTimeBy(5L); // arbitrary
        // 2
        stopwatch.togglePause();
        tick();
        assertThat(timeWatcher.getCurrentTime(), is(2L));

        // Exercise
        // 3
        stopwatch.togglePause();
        advanceTimeBy(3L); // arbitrary

        // Verify
        assertThat(timeWatcher.getCurrentTime(), is(2L));
    }

    private void tick() {
        advanceTimeBy(1L);
    }

    private void advanceTimeBy(long time) {
        testScheduler.advanceTimeBy(time, Stopwatch.TIME_UNIT);
    }

    static class TimeWatcher {

        private long currentTime = -1L;

        void onNext(long now) {
            System.out.printf("onNext with %d\n", now);
            currentTime = now;
        }

        long getCurrentTime() {
            return currentTime;
        }
    }
}