package com.autonomousapps.reactivestopwatch.test;

import com.autonomousapps.reactivestopwatch.di.DaggerTestStopwatchComponent;
import com.autonomousapps.reactivestopwatch.di.DaggerUtil;
import com.autonomousapps.reactivestopwatch.di.TestStopwatchComponent;

import org.junit.Before;

import android.support.annotation.CallSuper;

public abstract class AbstractMockedDependenciesTest extends AbstractAnimationDisablingTest {

    protected TestStopwatchComponent testComponent;

    @Before
    @CallSuper
    public void setup() throws Exception {
        initInjections();
    }

    private void initInjections() {
        testComponent = DaggerTestStopwatchComponent.create();
        DaggerUtil.INSTANCE.setComponent(testComponent);
    }
}