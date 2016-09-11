package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.time.StopwatchImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.schedulers.TestScheduler;

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

    @Test
    public void attachingShouldTick0() throws Exception {
        verify(view).onTick(0L);
    }

    @Test
    public void startTicksMerrilyAway() throws Exception {
        // Setup
        when(stopwatch.start()).thenReturn(testObservable);

        // Exercise (#start() subscribes, which causes source Observable to emit its items)
        stopwatchPresenter.start();
        testScheduler.triggerActions();

        // Verify
        verify(stopwatch).start();
        verify(view).onTick(0L); // #doOnSubscribe()
        verify(view).onTick(1L);
        verify(view).onTick(2L);
        verify(view).onTick(3L);
    }

    @Test
    public void detachingStopsViewInteractions() throws Exception {
        // Setup
        when(stopwatch.start()).thenReturn(testObservable);

        // Exercise (#start() subscribes, which causes source Observable to emit its items)
        stopwatchPresenter.detachView();
        stopwatchPresenter.start();
        testScheduler.triggerActions();

        // Verify
        verify(stopwatch).start();
        verify(view).onTick(0L); // from attach
        verifyNoMoreInteractions(view);
    }

    @Test
    public void togglePauseTogglesPause() throws Exception {
        stopwatchPresenter.togglePause();

        verify(stopwatch).togglePause();
    }

    @Test
    public void resetResets() throws Exception {
        stopwatchPresenter.reset();

        verify(stopwatch).reset();
    }
}