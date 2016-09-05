package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.time.Stopwatch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.schedulers.TestScheduler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StopwatchPresenterTest {

    @Mock Stopwatch stopwatch;
    @Mock StopwatchMvp.View view;
    @Mock Observable<Long> mockObservable;

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
        when(stopwatch.start()).thenReturn(mockObservable);

        stopwatchPresenter.start();

        verify(stopwatch).start();
        // TODO how to use the mocked observable? Maybe use a real one and have it emit things...?
    }
}