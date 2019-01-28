package com.example.android.inventory;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddProduct {

    @Rule
    public ActivityTestRule<CatalogActivity> mActivityTestRule = new ActivityTestRule<>(CatalogActivity.class);

    @Test
    public void addProduct() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edit_product_name),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Apple"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.edit_product_price),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.container_price),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("900"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.edit_product_quantity),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.container_quantity),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("700"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.edit_product_supplier),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.container_supplier_name),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("Apple"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.edit_product_supplier_number),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.container_supplier_phone_number),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("8203669800"), closeSoftKeyboard());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.edit_product_supplier_number), withText("8203669800"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.container_supplier_phone_number),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(pressImeActionButton());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_save), withContentDescription("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
