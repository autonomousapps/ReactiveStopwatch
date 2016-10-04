package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.R;
import com.autonomousapps.reactivestopwatch.test.AbstractMockedDependenciesTest;
import com.autonomousapps.reactivestopwatch.view.TimeTeller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.autonomousapps.reactivestopwatch.test.CommonEspressoCalls.verifyViewIsDisplayedWithTextIgnoreCase;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StopwatchFragmentTest extends AbstractMockedDependenciesTest {

    private static final String START_TEXT = "start";
    private static final String STOP_TEXT = "stop";

    private static final String RESET_TEXT = "reset";
    private static final String LAP_TEXT = "lap";

    @Inject StopwatchMvp.Presenter stopwatchPresenter;

    private StopwatchMvp.View view = null;

    @Rule public ActivityTestRule<StopwatchActivity> activityRule = new ActivityTestRule<>(StopwatchActivity.class, true, false);

    @Before
    public void setup() throws Exception {
        super.setup();
        testComponent.inject(this);
        setupMockView();

        launchApp();
        verifyUi();
    }

    private void setupMockView() {
        doAnswer(invocation -> view = (StopwatchMvp.View) invocation.getArguments()[0])
                .when(stopwatchPresenter).attachView(any(StopwatchMvp.View.class));
        doAnswer(invocation -> view = null)
                .when(stopwatchPresenter).detachView();
    }

    private void verifyUi() throws Exception {
        onView(withId(R.id.stopwatch)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_reset)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_start)).check(matches(isDisplayed()));
    }

    private void launchApp() {
        activityRule.launchActivity(new Intent());
    }

    @Test
    public void onTickShouldUpdateTime() throws Throwable {
        TimeTeller timeTeller = mock(TimeTeller.class);
        view.setTimeTeller(timeTeller);

        onMainThreadDo(() -> view.onTick(1000L));

        verify(timeTeller).tellTime(1000L);
    }

    @Test
    public void onStartedShouldChangeText() throws Throwable {
        onMainThreadDo(() -> view.onStopwatchStarted());

        verifyViewIsDisplayedWithTextIgnoreCase(STOP_TEXT);
    }

    @Test
    public void onPausedShouldChangeText() throws Throwable {
        onMainThreadDo(() -> view.onStopwatchPaused());

        verifyViewIsDisplayedWithTextIgnoreCase(START_TEXT);
    }

    private void onMainThreadDo(@NonNull Runnable action) throws Throwable {
        activityRule.runOnUiThread(action);
    }
}