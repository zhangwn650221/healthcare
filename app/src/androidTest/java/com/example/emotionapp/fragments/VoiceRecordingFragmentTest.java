package com.example.emotionapp.fragments;

import androidx.test.espresso.contrib.AccessibilityChecks;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.rule.GrantPermissionRule;


import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

import com.example.emotionapp.R;

@RunWith(AndroidJUnit4.class)
public class VoiceRecordingFragmentTest {

    // Grant RECORD_AUDIO permission before running tests that need it.
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO);

    // Opt-in to accessibility checks
    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    @Test
    public void testInitialUIState() {
        // Launch the fragment in a container
        FragmentScenario.launchInContainer(VoiceRecordingFragment.class);

        // Check initial button states
        onView(withId(R.id.buttonStartRecording)).check(matches(isEnabled()));
        onView(withId(R.id.buttonStopRecording)).check(matches(not(isEnabled())));
        onView(withId(R.id.buttonPlayRecording)).check(matches(not(isEnabled())));
    }

    @Test
    public void testStartRecording_UIChanges() {
        FragmentScenario.launchInContainer(VoiceRecordingFragment.class);

        // Click start recording button
        onView(withId(R.id.buttonStartRecording)).perform(click());

        // Check button states after starting
        // It can take a moment for MediaRecorder to prepare, which might delay UI updates.
        // Espresso usually handles synchronization, but if tests are flaky, IdlingResource might be needed.
        onView(withId(R.id.buttonStartRecording)).check(matches(not(isEnabled())));
        onView(withId(R.id.buttonStopRecording)).check(matches(isEnabled()));
        onView(withId(R.id.buttonPlayRecording)).check(matches(not(isEnabled()))); // Play should still be disabled
    }

    @Test
    public void testStopRecording_UIChanges() {
        FragmentScenario.launchInContainer(VoiceRecordingFragment.class);

        // Start recording
        onView(withId(R.id.buttonStartRecording)).perform(click());

        // Wait a little for MediaRecorder to actually start and create a file.
        // Espresso should ideally sync, but real device/emulator timing can vary.
        // This is a common source of flakiness in media tests.
        // A more robust solution would use IdlingResource that waits for a specific app state.
        try {
            Thread.sleep(1000); // Simple delay, not ideal for robust tests
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click stop recording button
        onView(withId(R.id.buttonStopRecording)).perform(click());

        try { // Add delay for UI to update
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Check button states after stopping
        onView(withId(R.id.buttonStartRecording)).check(matches(isEnabled()));
        onView(withId(R.id.buttonStopRecording)).check(matches(not(isEnabled())));
        // Play button should be enabled if a recording was successfully made and file path is not null.
        // This depends on the internal logic of stopRecording() correctly setting audioFilePath.
        onView(withId(R.id.buttonPlayRecording)).check(matches(isEnabled()));
    }

    // Test for permission denial behavior is more complex and typically involves UI Automator
    // or other strategies to interact with the system permission dialog.
    // For this exercise, we focus on tests where permission is granted.
    // A simple test without GrantPermissionRule would show that clicking "Start"
    // does NOT lead to the "recording started" UI state if permission is immediately denied by the system
    // (though automated denial is not standard). If the dialog appears, Espresso can't see it.
}
>>>>>>> REPLACE
