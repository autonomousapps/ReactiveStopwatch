package com.autonomousapps.reactivestopwatch.service;

import com.autonomousapps.common.LogUtil;
import com.autonomousapps.reactivestopwatch.test.AbstractMockedDependenciesTest;
import com.autonomousapps.reactivestopwatch.time.Stopwatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StopwatchServiceTest extends AbstractMockedDependenciesTest {

    @Inject
    @Named("local")
    Stopwatch stopwatch;

//    @Rule public DeGoogledServiceTestRule serviceTestRule = new DeGoogledServiceTestRule();
    private IStopwatchService service;

    @Before
    public void setup() throws Exception {
        LogUtil.e("TEST", "setup()");
        super.setup();
        testComponent.inject(this);

//        Intent serviceIntent = new Intent(InstrumentationRegistry.getTargetContext(), StopwatchService.class);
//        serviceTestRule.startService(serviceIntent);
//        IBinder binder = serviceTestRule.bindService(serviceIntent);
//        service = IStopwatchService.Stub.asInterface(binder);

        startAndBindService();
        service = IStopwatchService.Stub.asInterface(mServiceConn.getBinder());

        assertNotNull("Service is null!", service);
    }

    @After
    public void teardown() throws Exception {
        LogUtil.e("TEST", "teardown()");
//        service = null;
        unbindAndStopService();
    }

    @Test
    public void firstTest() throws Exception {
        when(stopwatch.isPaused()).thenReturn(true);

        assertTrue("Stopwatch isn't paused!", service.isPaused());
    }

    @Test
    public void startShouldSomething() throws Exception {
        IStopwatchTickListener listener = mock(IStopwatchTickListener.class);
        Observable<Long> observable = Observable.just(1L);
        when(stopwatch.start()).thenReturn(observable);

        LogUtil.e("TEST", "startShouldSomething(): stopwatch=" + stopwatch.toString());

        service.start(listener);

        verify(listener).onTick(1L);
    }

    /*
     * Annoying Service stuff.
     */

    private void startAndBindService() throws Exception {
        mServiceConn = new ProxyServiceConnection();
        Context context = InstrumentationRegistry.getTargetContext();

        Intent serviceIntent = new Intent(context, StopwatchService.class);
        context.startService(serviceIntent);
        boolean isBound = context.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        if (isBound) {
            // block until service connection is established
            waitOnLatch(mServiceConn.getLatch(), "connected");
        } else {
            LogUtil.e("TEST", "Failed to bind to service");
        }
    }

    private void waitOnLatch(CountDownLatch latch, String actionName) throws TimeoutException {
        try {
            if (!latch.await(5L, TimeUnit.SECONDS)) {
                throw new TimeoutException("Waited for " + 5 + " " + TimeUnit.SECONDS.name() + "," + " but service was never " + actionName);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for service to be " + actionName, e);
        }
    }

    private void unbindAndStopService() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        context.unbindService(mServiceConn);
        context.stopService(new Intent(context, StopwatchService.class));
        service = null;
    }

    private ProxyServiceConnection mServiceConn;

    static class ProxyServiceConnection implements ServiceConnection {

        private CountDownLatch connectedLatch = new CountDownLatch(1);

        private IBinder binder;

        public CountDownLatch getLatch() {
            return connectedLatch;
        }

        public IBinder getBinder() {
            return binder;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // store the service binder to return to the caller
            binder = service;
            connectedLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //The process hosting the service has crashed or been killed.
            LogUtil.e("TEST", "Connection to the Service has been lost!");
            binder = null;
        }
    }
}