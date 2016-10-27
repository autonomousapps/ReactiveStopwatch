package com.autonomousapps.reactivestopwatch.time;

import com.autonomousapps.reactivestopwatch.service.SchedulerProvider;
import com.autonomousapps.reactivestopwatch.test.AbstractMockedDependenciesTest;

import org.junit.Before;
import org.junit.Test;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static com.autonomousapps.reactivestopwatch.di.StopwatchModule.LOCAL_STOPWATCH;
import static org.mockito.Mockito.when;

// By necessity, this is a component test TODO is there a way to get a mock service?
public class RemoteStopwatchTest extends AbstractMockedDependenciesTest {

    @Inject
    @Named(LOCAL_STOPWATCH)
    Stopwatch localStopwatch;

    //    @Inject @Named(COMPUTATION_SCHEDULER)
    TestScheduler testScheduler = Schedulers.test();

    private Context context;

    private RemoteStopwatch remoteStopwatch;

    @Before
    public void setup() throws Exception {
        super.setup();
        testComponent.inject(this);
        SchedulerProvider.setComputationScheduler(testScheduler);

        context = InstrumentationRegistry.getTargetContext();
        remoteStopwatch = new RemoteStopwatch(context);
        remoteStopwatch.onUiShown();
    }

    @Test
    public void firstTest() throws Exception {
        Observable<Long> obs = Observable.just(1L, 2L);
        when(localStopwatch.start()).thenReturn(obs);

        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();
        remoteStopwatch.start().subscribe(testSubscriber);
        testScheduler.triggerActions();

        testSubscriber.assertReceivedOnNext(Arrays.asList(1L, 2L));
    }
}