package com.shop;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class AdminLoginTest {
    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Assert.assertEquals("com.shop", appContext.getPackageName());
    }

    @Test
    public void login() {
        Espresso.onView(withId(R.id.admin_panel_link)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.login_email_input)).perform(typeText("admin@"));
        Espresso.onView(withId(R.id.login_password_input)).perform(typeText("1234"));
        Espresso.onView(withId(R.id.login_password_input)).perform(closeSoftKeyboard());
        Espresso.onView(withId(R.id.login_btn))
                .perform(ViewActions.click());

        Espresso.onView(withText("Success login, admin!"))
                .inRoot(withDecorView(not(rule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}
