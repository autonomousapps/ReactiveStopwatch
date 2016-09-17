package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.test.BaseEspressoTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

public class StopwatchFragmentTest extends BaseEspressoTest {

    @Rule
    public ActivityTestRule<StopwatchActivity> activityRule = new ActivityTestRule<>(StopwatchActivity.class, true, false);

    @Before
    public void setup() throws Exception {
        super.setup();
        testComponent.inject(this);
        launchApp();
    }

    private void launchApp() {
        activityRule.launchActivity(new Intent());
    }

    @Test
    public void firstTest() throws Exception {

    }
}