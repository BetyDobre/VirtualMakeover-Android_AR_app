package com.shop;

import android.content.Context;
import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;

import kotlin.jvm.JvmField;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class SignInTest {
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
        Espresso.onView(withId(R.id.login_email_input)).perform(typeText("betty_dobre@yahoo.com"));
        Espresso.onView(withId(R.id.login_password_input)).perform(typeText("1234"));
        Espresso.onView(withId(R.id.login_password_input)).perform(closeSoftKeyboard());
        Espresso.onView(withId(R.id.login_btn))
                .perform(ViewActions.click());

        Espresso.onView(withText("Success login!"))
                .inRoot(withDecorView(not(rule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}