package com.autonomousapps.reactivestopwatch.ui;

import com.autonomousapps.reactivestopwatch.test.AbstractAnimationDisablingTest;
import com.autonomousapps.reactivestopwatch.test.Timer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/*
 * NOTES:
 * (1) You can mix and match UiAutomator and Espresso calls, but I suspect this may prove to be dangerous or fragile, as Espresso
 * calls may block unexpectedly if the main thread is active.
 * (2)
 */
public class StopwatchAcceptanceTest extends AbstractAnimationDisablingTest {

    private static final String APPLICATION_PACKAGE = "com.autonomousapps.reactivestopwatch";

    // All time units in millis
    private static final long LAUNCH_TIMEOUT = 5000L;
    private static final long IDLE_TIMEOUT_0 = 0L;
    private static final long ERROR_MARGIN_100 = 100L; // 1/10th of one second
    private static final long ERROR_MARGIN_150 = 150L;

    private static final String START_TEXT = "start";
    private static final String STOP_TEXT = "stop";

    private static final String RESET_TEXT = "reset";
    private static final String LAP_TEXT = "lap";

    private Configurator configurator = Configurator.getInstance();
    private Collection<Runnable> teardownTasks = new LinkedList<>();

    private UiDevice device;

    private UiObject2 startStopBtn;
    private UiObject2 resetLapBtn;
    private UiObject2 stopwatch;

    @Before
    public void setup() throws Exception {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        launchApp();

        // UI thread is *never* idle because of the nature of the app, so set idle timeout to 0
        setIdleTimeout(IDLE_TIMEOUT_0);

        checkPreconditions();
    }

    private void checkPreconditions() throws Exception {
        assertThat(device, notNullValue());
        verifyUi();
    }

    private void verifyUi() throws Exception {
        stopwatch = getStopwatchView();
        startStopBtn = getStartPauseButton();
        resetLapBtn = getResetButton();

        assertThat(stopwatch, notNullValue());
        assertThat(startStopBtn, notNullValue());
        assertThat(resetLapBtn, notNullValue());
    }

    @After
    public void teardown() throws Exception {
        resetConfigurator();
    }

    private void resetConfigurator() {
        teardownTasks.forEach(Runnable::run);
    }

    private void launchApp() {
        device.pressHome();

        // Wait for launcher
        String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch app
        Context context = InstrumentationRegistry.getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(APPLICATION_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(APPLICATION_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    /*
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.
     */
    private static String getLauncherPackageName() {
        // Create launcher Intent
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    @Test
    public void stopwatchShouldBeAccurate() throws Exception {
        // Check precondition
        assertThat(stopwatch.getText(), is("00:00:00.0"));

        // Press 'start'
        startStopBtn.click();
        Timer timer = new Timer();
        assertThat(startStopBtn.getText(), equalToIgnoringCase(STOP_TEXT));

        // Exercise: wait for 2s
        await().pollInterval(10, TimeUnit.MILLISECONDS)
                .atMost(2000L + ERROR_MARGIN_100, TimeUnit.MILLISECONDS)
                .until(() -> stopwatch.getText().startsWith("00:00:02"));

        // Press pause
        long elapsedTime = timer.elapsedTime();
        startStopBtn.click();

        // Verify
        assertThat(startStopBtn.getText(), equalToIgnoringCase(START_TEXT));
        assertThat(Math.abs(elapsedTime - 2000L), lessThanOrEqualTo(ERROR_MARGIN_150));
        assertThat(stopwatch.getText(), is("00:00:02.0"));
    }

    @Test
    public void stoppingAndStartingShouldStopAndStartTimer() throws Exception {
        // Press 'start'
        startStopBtn.click();
        Timer timer = new Timer();

        // Wait for 0.5s
        await().pollInterval(10, TimeUnit.MILLISECONDS)
                .atMost(500L + ERROR_MARGIN_100, TimeUnit.MILLISECONDS)
                .until(() -> timer.elapsedTime() >= 500L);

        // Stop timer
        startStopBtn.click();
        String currentTime = stopwatch.getText();

        // Wait for 0.5s
        await().pollInterval(10, TimeUnit.MILLISECONDS)
                .atMost(500L + ERROR_MARGIN_100, TimeUnit.MILLISECONDS)
                .until(() -> timer.elapsedTime() >= 1000L);

        // Verify stopwatch hasn't changed
        assertThat(stopwatch.getText(), is(currentTime));

        // TODO more?
    }

    @Test
    public void startingAndStoppingShouldChangeTextOnBothButtons() throws Exception {
        // Exercise: press 'start'
        startStopBtn.click();
        assertThat(startStopBtn.getText(), equalToIgnoringCase(STOP_TEXT));
        assertThat(resetLapBtn.getText(), equalToIgnoringCase(LAP_TEXT));

        // Exercise: press pause
        startStopBtn.click();

        // Verify
        assertThat(startStopBtn.getText(), equalToIgnoringCase(START_TEXT));
        assertThat(resetLapBtn.getText(), equalToIgnoringCase(RESET_TEXT));
    }

    @Test
    public void resetButtonShouldResetClock() throws Exception {
        // Press 'start'
        startStopBtn.click();

        // Let a brief amount of time pass
        await().pollInterval(10, TimeUnit.MILLISECONDS)
                .atMost(1000L + ERROR_MARGIN_100, TimeUnit.MILLISECONDS)
                .until(() -> !stopwatch.getText().equals("00:00:00.0"));
        assertThat(stopwatch.getText(), not("00:00:00.0"));

        // Exercise: press 'reset'
        startStopBtn.click(); // so 'reset or lap' button is in 'reset' mode
        resetLapBtn.click();

        // Verify
        assertThat(stopwatch.getText(), is("00:00:00.0"));
        assertThat(startStopBtn.getText(), equalToIgnoringCase(START_TEXT));
    }

    private UiObject2 getStartPauseButton() {
        return findObjectByText(START_TEXT);
    }

    private UiObject2 getResetButton() {
        return findObjectByText(RESET_TEXT);
    }

    private UiObject2 findObjectByText(@NonNull String text) {
        return device.findObject(byTextIgnoreCase(text));
    }

    private UiObject2 getStopwatchView() {
        return device.findObject(By.res(APPLICATION_PACKAGE, "stopwatch"));
    }

    private static BySelector byTextIgnoreCase(@NonNull String text) {
        return By.text(caseInsensitivePatternFrom(text));
    }

    private static Pattern caseInsensitivePatternFrom(@NonNull String text) {
        return Pattern.compile(text, Pattern.CASE_INSENSITIVE);
    }

    private void setIdleTimeout(long timeout) {
        long oldIdleTimeout = configurator.getWaitForIdleTimeout();
        configurator.setWaitForIdleTimeout(timeout);
        teardownTasks.add(() -> configurator.setWaitForIdleTimeout(oldIdleTimeout));

        long oldSelectorTimeout = configurator.getWaitForSelectorTimeout();
        configurator.setWaitForSelectorTimeout(timeout);
        teardownTasks.add(() -> configurator.setWaitForSelectorTimeout(oldSelectorTimeout));
    }
}