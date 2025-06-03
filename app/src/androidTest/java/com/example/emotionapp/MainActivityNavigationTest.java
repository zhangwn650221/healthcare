package com.example.emotionapp;

import androidx.test.espresso.contrib.AccessibilityChecks;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText; // For HomeFragment's TextView

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityNavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void enableAccessibilityChecks() {
        // Optional: Enable accessibility checks
        AccessibilityChecks.enable();
    }

    @Test
    public void testDefaultFragment_isHomeFragment() {
        // HomeFragment has a TextView with "Welcome Home!"
        // This text is defined in fragment_home.xml
        onView(withText("Welcome Home!")).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToDiary_DisplaysDiaryFragment() {
        // Click on the Diary item in the BottomNavigationView
        onView(withId(R.id.nav_diary)).perform(click());

        // Check if a view unique to DiaryFragment is displayed (e.g., the EditText)
        onView(withId(R.id.editTextDiary)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToVoice_DisplaysVoiceFragment() {
        // Click on the Voice item
        onView(withId(R.id.nav_voice)).perform(click());

        // Check if a view unique to VoiceRecordingFragment is displayed (e.g., buttonStartRecording)
        onView(withId(R.id.buttonStartRecording)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToVideo_DisplaysVideoFragment() {
        // Click on the Video item
        onView(withId(R.id.nav_video)).perform(click());

        // Check if a view unique to VideoRecordingFragment is displayed (e.g., buttonStartVideoCapture)
        onView(withId(R.id.buttonStartVideoCapture)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigation_BackToHome_DisplaysHomeFragment() {
        // First, navigate to a different fragment (e.g., Diary)
        onView(withId(R.id.nav_diary)).perform(click());
        onView(withId(R.id.editTextDiary)).check(matches(isDisplayed())); // Confirm navigation

        // Then, navigate back to Home
        onView(withId(R.id.nav_home)).perform(click());

        // Check if the unique view from HomeFragment is displayed again
        onView(withText("Welcome Home!")).check(matches(isDisplayed()));
    }
}
>>>>>>> REPLACE
