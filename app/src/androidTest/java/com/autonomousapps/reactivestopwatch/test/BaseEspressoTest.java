package com.autonomousapps.reactivestopwatch.test;

import com.metova.cappuccino.animations.SystemAnimations;

import org.junit.AfterClass;
import org.junit.BeforeClass;

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
}