package com.shop;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.shop.shopActivities.ResetPasswordActivity;

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

public class ResetPasswordTest {
    @Rule
    public ActivityTestRule<ResetPasswordActivity> rule = new ActivityTestRule<>(ResetPasswordActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Assert.assertEquals("com.shop", appContext.getPackageName());
    }

    @Test
    public void login() {
        Espresso.onView(withId(R.id.email_account_reset)).perform(typeText("alinam@gmail.com"));
        Espresso.onView(withId(R.id.email_account_reset)).perform(closeSoftKeyboard());
        Espresso.onView(withId(R.id.reset_password_btn))
                .perform(ViewActions.click());

        Espresso.onView(withText("Password changed successfully!"))
                .inRoot(withDecorView(not(rule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}
