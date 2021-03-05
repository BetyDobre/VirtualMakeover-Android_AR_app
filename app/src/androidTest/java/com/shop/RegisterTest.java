package com.shop;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class RegisterTest {
    @Rule
    public ActivityTestRule<RegisterActivity> rule = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Assert.assertEquals("com.shop", appContext.getPackageName());
    }

    @Test
    public void correctData() {
        Espresso.onView(ViewMatchers.withId(R.id.register_username_input)).perform(ViewActions.scrollTo(),ViewActions.typeText("Alina Ionescu"));
        Espresso.onView(ViewMatchers.withId(R.id.register_email_input)).perform(ViewActions.scrollTo(),ViewActions.typeText("alinam@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.register_password_input)).perform(ViewActions.scrollTo(),ViewActions.typeText("parola"));
        Espresso.onView(ViewMatchers.withId(R.id.register_confirm_password_input)).perform(ViewActions.scrollTo(),ViewActions.typeText("parola"));

        Espresso.onView(ViewMatchers.withId(R.id.register_btn))
                .perform(ViewActions.scrollTo(),ViewActions.click());

        Espresso.onView(ViewMatchers.withText("Your account was created successfully"))
                .inRoot(RootMatchers.withDecorView(Matchers.not(rule.getActivity().getWindow().getDecorView())))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

}
