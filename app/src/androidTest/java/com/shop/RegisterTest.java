package com.shop;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.shop.shopActivities.RegisterActivity;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

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
        Espresso.onView(ViewMatchers.withId(R.id.register_username_input)).perform(ViewActions.scrollTo(),ViewActions.typeText("Ana Ionescu"));
        Espresso.onView(ViewMatchers.withId(R.id.register_email_input)).perform(ViewActions.scrollTo(),ViewActions.typeText("anaionescu@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.register_password_input)).perform(ViewActions.scrollTo(),ViewActions.typeText("parola"));
        Espresso.onView(ViewMatchers.withId(R.id.register_confirm_password_input)).perform(ViewActions.scrollTo(),ViewActions.typeText("parola"));

        Espresso.onView(ViewMatchers.withId(R.id.register_btn))
                .perform(ViewActions.scrollTo(),ViewActions.click());

        Espresso.onView(ViewMatchers.withText("Your account was created successfully"))
                .inRoot(RootMatchers.withDecorView(Matchers.not(rule.getActivity().getWindow().getDecorView())))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

}
