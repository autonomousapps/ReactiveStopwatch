package com.autonomousapps.reactivestopwatch.time;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Random;

import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StopwatchTest {

    private final TestScheduler testScheduler = new TestScheduler();

    private TestTimeProvider timeProvider;
    private Stopwatch stopwatch;

    private final Random numberGenerator = new Random(1L);

    @Before
    public void setup() throws Exception {
        timeProvider = new TestTimeProvider();
        stopwatch = new Stopwatch(timeProvider);
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
        stopwatch.start().subscribe(timeWatcher::onNext);

        // Exercise
        tick();

        // Verify
        assertThat(timeWatcher.getCurrentTime(), is(1L));
    }

    @Test
    public void timerAccuratelyTicksTheSeconds() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start().subscribe(timeWatcher::onNext);

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
        stopwatch.start().subscribe(timeWatcher::onNext);

        // Exercise
        advanceTimeBy(5L);

        // Verify
        assertThat(timeWatcher.getCurrentTime(), is(5L));
    }

    @Test
    public void pausePauses() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start().subscribe(timeWatcher::onNext);
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
        stopwatch.start().subscribe(timeWatcher::onNext);
        tick();
        assertThat(timeWatcher.getCurrentTime(), is(1L));
        stopwatch.togglePause();
        advanceTimeTo(randomTick());

        // Exercise
        stopwatch.togglePause();
        tick();

        // Verify
        assertThat(timeWatcher.getCurrentTime(), is(2L));
    }

    @Test
    public void pausingThricePausesAgain() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start().subscribe(timeWatcher::onNext);
        tick();
        assertThat(timeWatcher.getCurrentTime(), is(1L));
        // 1
        stopwatch.togglePause();
        advanceTimeTo(randomTick());
        // 2
        stopwatch.togglePause();
        tick();
        assertThat(timeWatcher.getCurrentTime(), is(2L));

        // Exercise
        // 3
        stopwatch.togglePause();
        advanceTimeTo(randomTick(timeProvider.now()));

        // Verify
        assertThat(timeWatcher.getCurrentTime(), is(2L));
    }

    @Test
    public void callingLapImmediatelyReturnsVeryShortLap() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start().subscribe(timeWatcher::onNext);

        // Exercise
        Lap lap = stopwatch.lap();

        // Verify
        assertThat(lap.duration(), is(0L));
        assertThat(lap.endTime(), is(0L));
    }

    @Test
    public void callingLapAfterATickReturnsAShortLap() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start().subscribe(timeWatcher::onNext);

        // Exercise
        tick();
        Lap lap = stopwatch.lap();

        // Verify
        assertThat(lap.duration(), is(1L));
        assertThat(lap.endTime(), is(1L));
    }

    @Test
    public void twoLapsWorks() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start().subscribe(timeWatcher::onNext);

        // Exercise: 1st lap
        tick();
        Lap lap = stopwatch.lap();

        // Verify: 1st lap
        assertThat(lap.duration(), is(1L));
        assertThat(lap.endTime(), is(1L));

        // Exercise: 2nd lap
        advanceTimeBy(10L);
        lap = stopwatch.lap();

        // Verify: 2nd lap
        assertThat(lap.duration(), is(10L));
        assertThat(lap.endTime(), is(11L));
    }

    @Test
    public void threeLapsWorks() throws Exception {
        // Setup
        TimeWatcher timeWatcher = new TimeWatcher();
        stopwatch.start().subscribe(timeWatcher::onNext);

        // Exercise: 1st lap
        tick();
        Lap lap = stopwatch.lap();

        // Verify: 1st lap
        assertThat(lap.duration(), is(1L));
        assertThat(lap.endTime(), is(1L));

        // Exercise: 2nd lap
        advanceTimeBy(10L);
        lap = stopwatch.lap();

        // Verify: 2nd lap
        assertThat(lap, is(Lap.create(10L, 11L)));

        // Exercise: 3rd lap
        advanceTimeBy(60L);
        lap = stopwatch.lap();

        // Verify: 3rd lap
        assertThat(lap, is(Lap.create(60L, 71L)));
    }

    @Test
    public void resetEndsTheStreamOfEvents() throws Exception {
        resetTest();
    }

    @Test
    public void startingAfterResetStartsOver() throws Exception {
        // Setup
        TestSubscriber<Long> testSubscriber = resetTest();

        // Exercise
        stopwatch.start().subscribe(testSubscriber);
        advanceTimeBy(5L);

        // Verify
        testSubscriber.assertValues(1L, 2L, 3L, 4L, 5L);
    }

    private TestSubscriber<Long> resetTest() {
        // Setup
        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();
        stopwatch.start().subscribe(testSubscriber);
        advanceTimeBy(5L);

        // Exercise
        stopwatch.reset();
        advanceTimeBy(5L);

        // Verify
        testSubscriber.assertValueCount(5);
        testSubscriber.assertCompleted();
        assertThat(testSubscriber.isUnsubscribed(), is(true));

        // For additional testing
        return testSubscriber;
    }

    private void tick() {
        advanceTimeBy(1L);
    }

    private void advanceTimeBy(long time) {
        timeProvider.advanceTimeBy(time);
        testScheduler.advanceTimeBy(time, Stopwatch.TIME_UNIT);
    }

    private void advanceTimeTo(long time) {
        timeProvider.advanceTimeTo(time);
        testScheduler.advanceTimeTo(time, Stopwatch.TIME_UNIT);
    }

    private long randomTick() {
        return randomTick(0L);
    }

    private long randomTick(long left) {
        int max = 1_000_000;
        // I want the range clamped to [0, N), where N is sufficiently large to demonstrate the
        // robustness of the system, but not so large as to cause issues with the TestScheduler,
        // which attempts to trigger every action that gets queued when we advance time.
        return Math.min(max, left + numberGenerator.nextInt(max));
    }

    static class TimeWatcher {

        private long currentTime = -1L;

        void onNext(long now) {
            currentTime = now;
        }

        long getCurrentTime() {
            return currentTime;
        }
    }
}