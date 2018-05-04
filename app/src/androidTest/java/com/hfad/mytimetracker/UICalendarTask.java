package com.hfad.mytimetracker;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UICalendarTask {

    Context context;
    TimeTrackerDataBaseHelper helper;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void deleteDatabaseTasks() {
        context = InstrumentationRegistry.getTargetContext();
        helper = new TimeTrackerDataBaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from TASK_STATS");
        db.execSQL("delete from TASK_INFORMATION");
        db.execSQL("delete from TASK_CATEGORY_INFO");
    }


    @Test
    public void mainActivityTest() {

        String categoryName = "newcategory84";
        SQLfunctionHelper.enterCatInDB(context, categoryName, 1);

        CharSequence[] taskCategoryNames = {categoryName};
        SQLfunctionHelper.enterTaskInDB(context, "Task", "2018-04-27", "12-00-00", "12-00-00", taskCategoryNames);

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.task_calendar),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction viewPager2 = onView(
                allOf(withId(R.id.pager),
                        childAtPosition(
                                allOf(withId(R.id.yolo),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        viewPager2.perform(swipeRight());

        ViewInteraction textView = onView(
                allOf(withId(R.id.expand_button), withText("Task"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recycler_container),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Task")));

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
