package com.example.emotionapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.emotionapp.R; // Assuming R class is in this package

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class DiaryFragmentTest {

    // Using production SharedPreferences name and key as per DiaryFragment's implementation
    private static final String PRODUCTION_PREFS_NAME = "sharedPrefs"; // From DiaryFragment.SHARED_PREFS
    private static final String PRODUCTION_LAST_DIARY_ENTRY_KEY = "last_diary_entry"; // From DiaryFragment.LAST_DIARY_ENTRY

    @Before
    public void setUp() {
        // Clear SharedPreferences before each test
        clearProductionSharedPreferences();
    }

    private void clearProductionSharedPreferences() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(PRODUCTION_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PRODUCTION_LAST_DIARY_ENTRY_KEY);
        editor.apply();
    }


    @Test
    public void testSaveAndLoadDiaryEntry() {
        // Launch fragment
        FragmentScenario<DiaryFragment> scenario = FragmentScenario.launchInContainer(DiaryFragment.class, null, R.style.Theme_EmotionApp, null);

        String testEntry = "This is a test diary entry.";

        // Type text into EditText and click save
        Espresso.onView(ViewMatchers.withId(R.id.editTextDiary)).perform(ViewActions.typeText(testEntry), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.buttonSaveDiary)).perform(ViewActions.click());

        // Verify SharedPreferences directly
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(PRODUCTION_PREFS_NAME, Context.MODE_PRIVATE);
        assertEquals(testEntry, prefs.getString(PRODUCTION_LAST_DIARY_ENTRY_KEY, null));

        // Re-launch fragment to check if entry is loaded
        scenario.recreate();

        // Check if EditText is populated with the saved entry
        Espresso.onView(ViewMatchers.withId(R.id.editTextDiary)).check(ViewAssertions.matches(ViewMatchers.withText(testEntry)));
    }

    @Test
    public void testSaveEmptyDiaryEntry_doesNotSave() {
         // Launch fragment
        FragmentScenario<DiaryFragment> scenario = FragmentScenario.launchInContainer(DiaryFragment.class, null, R.style.Theme_EmotionApp, null);

        // Click save with empty input
        Espresso.onView(ViewMatchers.withId(R.id.buttonSaveDiary)).perform(ViewActions.click());

        // Check SharedPreferences to ensure nothing was saved
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(PRODUCTION_PREFS_NAME, Context.MODE_PRIVATE);
        String entry = prefs.getString(PRODUCTION_LAST_DIARY_ENTRY_KEY, null);
        assertNull("Expected SharedPreferences to not contain last_diary_entry or for it to be null", entry);

        // Note: Verifying Toast messages with Espresso is complex and not included here.
        // The original DiaryFragment shows a Toast "Entry cannot be empty".
    }

    @Test
    public void testLoadDiaryEntry_whenNoEntryExists_editTextIsEmpty() {
        // Ensure prefs are clear (done in setUp)
        // Launch fragment
        FragmentScenario<DiaryFragment> scenario = FragmentScenario.launchInContainer(DiaryFragment.class, null, R.style.Theme_EmotionApp, null);

        // Check if EditText is empty
        Espresso.onView(ViewMatchers.withId(R.id.editTextDiary)).check(ViewAssertions.matches(ViewMatchers.withText("")));
    }


    @After
    public void tearDown() {
        // Clear SharedPreferences after each test
        clearProductionSharedPreferences();
    }
}
