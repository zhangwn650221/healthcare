package com.example.emotionapp.fragments;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.test.espresso.contrib.AccessibilityChecks;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.emotionapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class VideoRecordingFragmentTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    );

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    @Before
    public void setUpIntents() {
        Intents.init();
    }

    @After
    public void tearDownIntents() {
        Intents.release();
    }

    @Test
    public void testStartVideoCapture_SendsCorrectIntent() {
        FragmentScenario.launchInContainer(VideoRecordingFragment.class);

        onView(withId(R.id.buttonStartVideoCapture)).perform(click());

        intended(hasAction(MediaStore.ACTION_VIDEO_CAPTURE));
    }

    @Test
    public void testActivityResult_Success_DisplaysVideoPathAndMakesVideoViewVisible() {
        // Prepare a dummy URI
        Uri dummyVideoUri = Uri.parse("file:///dev/video_test.mp4");
        Intent resultData = new Intent();
        resultData.setData(dummyVideoUri);

        // Stub the intent that will be launched
        intending(hasAction(MediaStore.ACTION_VIDEO_CAPTURE))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));

        FragmentScenario.launchInContainer(VideoRecordingFragment.class);

        // Click the button to "start video capture"
        onView(withId(R.id.buttonStartVideoCapture)).perform(click());

        // Check that the TextView displays the path
        onView(withId(R.id.textViewVideoPath))
                .check(matches(withText(containsString(dummyVideoUri.toString()))));

        // Check that the VideoView becomes visible
        onView(withId(R.id.videoViewPlayback)).check(matches(isDisplayed()));
    }

    @Test
    public void testActivityResult_Cancelled_UpdatesUIAppropriately() {
        // Stub the intent to return RESULT_CANCELED
        intending(hasAction(MediaStore.ACTION_VIDEO_CAPTURE))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

        FragmentScenario.launchInContainer(VideoRecordingFragment.class);

        onView(withId(R.id.buttonStartVideoCapture)).perform(click());

        // Check that the TextView shows a cancellation message
        onView(withId(R.id.textViewVideoPath))
                .check(matches(withText(R.string.video_recording_cancelled_message)));

        // Check that the VideoView is not visible
        onView(withId(R.id.videoViewPlayback)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testActivityResult_Failed_UpdatesUIAppropriately() {
        // Stub the intent to return a generic failure (neither OK nor CANCELED)
        // For example, if the video capture app crashes or returns an unexpected result code.
        intending(hasAction(MediaStore.ACTION_VIDEO_CAPTURE))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_FIRST_USER, null)); // Some other result code

        FragmentScenario.launchInContainer(VideoRecordingFragment.class);

        onView(withId(R.id.buttonStartVideoCapture)).perform(click());

        // Check that the TextView shows a failure message
        onView(withId(R.id.textViewVideoPath))
                .check(matches(withText(R.string.video_recording_failed_message)));

        // Check that the VideoView is not visible
        onView(withId(R.id.videoViewPlayback)).check(matches(not(isDisplayed())));
    }
}
>>>>>>> REPLACE
