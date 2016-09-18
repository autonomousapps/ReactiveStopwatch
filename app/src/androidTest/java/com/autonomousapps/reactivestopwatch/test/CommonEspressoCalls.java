package com.autonomousapps.reactivestopwatch.test;

import android.support.annotation.IdRes;
import android.support.test.espresso.ViewInteraction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class CommonEspressoCalls {

    public static ViewInteraction verifyViewIsDisplayedWithId(@IdRes int id) {
        return onView(withId(id)).check(matches(isDisplayed()));
    }

    public static ViewInteraction performClickOnViewWithId(@IdRes int id) {
        return onView(withId(id)).perform(click());
    }
}