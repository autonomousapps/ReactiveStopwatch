package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.R;
import com.autonomousapps.reactivestopwatch.test.AbstractMockedDependenciesTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class StopwatchFragmentTest extends AbstractMockedDependenciesTest {

    @Inject StopwatchMvp.Presenter stopwatchPresenter;

    @Rule public ActivityTestRule<StopwatchActivity> activityRule = new ActivityTestRule<>(StopwatchActivity.class, true, false);

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
        verifyUi();
    }

    private void verifyUi() throws Exception {
        onView(withId(R.id.stopwatch)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_reset)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_start)).check(matches(isDisplayed()));
    }
}