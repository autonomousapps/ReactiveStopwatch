package com.autonomousapps.reactivestopwatch.time;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import rx.schedulers.TestScheduler;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StopwatchTest {

//    @Mock TimeProvider timeProvider;
    TimeProvider timeProvider = new SystemTimeProvider();
    private TestScheduler testScheduler;

    private Stopwatch stopwatch;

    @Before
    public void setup() throws Exception {
        stopwatch = new Stopwatch(timeProvider);

        testScheduler = new TestScheduler();
        stopwatch.setScheduler(testScheduler);
    }

    @Test
    public void startingTimerTriggerStartWith0() throws Exception {
        TimeWatcher timeWatcher = new TimeWatcher();

        stopwatch.start(timeWatcher::onNext);
        testScheduler.triggerActions();

        assertThat(timeWatcher.getTime(), is(0L));
    }

    @Test
    public void timerAccuratelyTicksTheSeconds() throws Exception {
//        Mockito.when(timeProvider.now())
//                .thenReturn(1L)
//                .thenReturn(2L)
//                .thenReturn(3L)
//                .thenReturn(4L)
//                .thenReturn(5L);
        TimeWatcher timeWatcher = new TimeWatcher();

        stopwatch.start(timeWatcher::onNext);
        testScheduler.advanceTimeBy(20, TimeUnit.MILLISECONDS);
        testScheduler.triggerActions();

        assertThat(timeWatcher.getTime(), is(1L));
    }

    static class TimeWatcher {

        private long time = -1L;

        void onNext(long time) {
            System.out.println("onNext called with " + time);
            this.time = time;
        }

        long getTime() {
            return time;
        }
    }
}