package com.autonomousapps.reactivestopwatch.test;

import com.autonomousapps.reactivestopwatch.di.DaggerTestStopwatchComponent;
import com.autonomousapps.reactivestopwatch.di.DaggerUtil;
import com.autonomousapps.reactivestopwatch.di.TestStopwatchComponent;
import com.metova.cappuccino.animations.SystemAnimations;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import android.support.annotation.CallSuper;
import android.support.test.InstrumentationRegistry;

public class BaseEspressoTest {

    @BeforeClass
    public static void disableAnimations() {
        SystemAnimations.disableAll(InstrumentationRegistry.getTargetContext());
    }

    @AfterClass
    public static void enableAnimations() {
        SystemAnimations.enableAll(InstrumentationRegistry.getTargetContext());
    }

    protected TestStopwatchComponent testComponent;

    @Before
    @CallSuper
    public void setup() throws Exception {
        initInjections();
    }

    private void initInjections() {
        testComponent = DaggerTestStopwatchComponent.create();
        DaggerUtil.INSTANCE.setTestComponent(testComponent);
    }
}