package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.mvp.ViewNotAttachedException;
import com.autonomousapps.reactivestopwatch.time.Lap;
import com.autonomousapps.reactivestopwatch.time.StopwatchImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.schedulers.TestScheduler;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StopwatchPresenterTest {

    @Mock StopwatchImpl stopwatch;
    @Mock StopwatchMvp.View view;

    private final Observable<Long> testObservable = Observable.just(1L, 2L, 3L);
    private final TestScheduler testScheduler = new TestScheduler();

    private StopwatchPresenter stopwatchPresenter;

    @Before
    public void setup() throws Exception {
        stopwatchPresenter = new StopwatchPresenter(stopwatch);
        stopwatchPresenter.setTestScheduler(testScheduler);

        stopwatchPresenter.attachView(view);
    }

    @After
    public void teardown() throws Exception {
        stopwatchPresenter.detachView();
    }

    @Test
    public void attachingCallsOnUiShown() throws Exception {
        verify(stopwatch).onUiShown();
    }

    @Test
    public void detachingCallsOnUiHidden() throws Exception {
        stopwatchPresenter.detachView();
        verify(stopwatch).onUiHidden();
    }

    @Test
    public void startOrPauseStartsOnFirstCall() throws Exception {
        // Setup
        when(stopwatch.start()).thenReturn(testObservable);

        // Exercise
        stopwatchPresenter.startOrStop();

        // Verify
        verify(view).onStopwatchStarted();
    }

    @Test
    public void startOrPausePausesOnSubsequentCalls() throws Exception {
        // Setup
        when(stopwatch.start()).thenReturn(testObservable);
        when(stopwatch.isPaused()).thenReturn(true);

        stopwatchPresenter.startOrStop();
        verify(view).onStopwatchStarted();

        // Exercise
        stopwatchPresenter.startOrStop();

        // Verify
        verify(stopwatch).togglePause();
        verify(view).onStopwatchStopped();
    }

    @Test
    public void pausingTwiceUnpauses() throws Exception {
        // Setup
        when(stopwatch.start()).thenReturn(testObservable);
        when(stopwatch.isPaused())
                .thenReturn(true)
                .thenReturn(false);

        stopwatchPresenter.startOrStop();
        verify(view).onStopwatchStarted();

        stopwatchPresenter.startOrStop();
        verify(stopwatch).togglePause();
        verify(view).onStopwatchStopped();

        // Exercise
        stopwatchPresenter.startOrStop();

        // Verify
        verify(view, times(2)).onStopwatchStarted();
    }

    @Test
    public void startTicksMerrilyAway() throws Exception {
        // Setup
        when(stopwatch.start()).thenReturn(testObservable);

        // Exercise (#start() subscribes, which causes source Observable to emit its items)
        stopwatchPresenter.startOrStop();
        testScheduler.triggerActions();

        // Verify
        verify(stopwatch).start();
        verify(view).onTick(1L);
        verify(view).onTick(2L);
        verify(view).onTick(3L);
    }

    //    @Test // TODO test stop procedure, but it'll be different shortly
    public void detachingStopsViewInteractions() throws Exception {
        // Setup
        when(stopwatch.start()).thenReturn(testObservable);

        // Exercise (#start() subscribes, which causes source Observable to emit its items if the TestScheduler is cool with that)
        stopwatchPresenter.detachView();
        stopwatchPresenter.startOrStop();
        testScheduler.triggerActions();

        // Verify
        verify(stopwatch).start();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void resetResetsAndStopsNewEvents() throws Exception {
        // Setup
        when(stopwatch.start()).thenReturn(testObservable);
        stopwatchPresenter.startOrStop();
        verify(view).onStopwatchStarted();

        when(stopwatch.isPaused()).thenReturn(true);
        stopwatchPresenter.startOrStop();
        verify(view).onStopwatchStopped();

        // Exercise
        stopwatchPresenter.resetOrLap();
        testScheduler.triggerActions();

        // Verify
        verify(stopwatch).reset();
        verify(view).onStopwatchStopped();
        verify(view, never()).onTick(anyLong());
    }

    @Test
    public void resetOrLapCallsOnNewLapWhenRunning() throws Exception {
        // Setup
        when(stopwatch.start()).thenReturn(testObservable);
        Lap lap = mock(Lap.class);
        when(stopwatch.lap()).thenReturn(lap);
        stopwatchPresenter.startOrStop();

        // Exercise
        stopwatchPresenter.resetOrLap();

        // Verify
        verify(view).onNewLap(lap);
    }

    @Test
    public void resetThrowsExceptionWhenNotAttached() throws Exception {
        // Setup
        stopwatchPresenter.detachView();

        // Exercise
        try {
            stopwatchPresenter.resetOrLap();
            fail();
        } catch (ViewNotAttachedException e) {
            // Success!
        }
    }
}