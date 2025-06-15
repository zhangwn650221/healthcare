package com.example.emotionapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.emotionapp.R;

import java.io.File;
import java.io.IOException;

public class VoiceRecordingFragment extends Fragment {

    private static final String LOG_TAG = "VoiceRecordingFragment";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private Button buttonStartRecording, buttonStopRecording, buttonPlayRecording;

    private MediaRecorder mediaRecorder;
    private String audioFilePath = null;
    private boolean isRecording = false;

    private MediaPlayer mediaPlayer;

    private boolean permissionToRecordAccepted = false;
    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};

    public VoiceRecordingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voice_recording, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.voice_activity_title));
        }

        buttonStartRecording = view.findViewById(R.id.buttonStartRecording);
        buttonStopRecording = view.findViewById(R.id.buttonStopRecording);
        buttonPlayRecording = view.findViewById(R.id.buttonPlayRecording);

        buttonStopRecording.setEnabled(false);
        buttonPlayRecording.setEnabled(false);

        buttonStartRecording.setOnClickListener(v -> startRecording());
        buttonStopRecording.setOnClickListener(v -> stopRecording());
        buttonPlayRecording.setOnClickListener(v -> playRecording());

        // Check for permission when view is created
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            permissionToRecordAccepted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionToRecordAccepted = true;
                Toast.makeText(getContext(), "Record audio permission granted", Toast.LENGTH_SHORT).show();
            } else {
                permissionToRecordAccepted = false;
                Toast.makeText(getContext(), "Permission denied. Cannot record audio.", Toast.LENGTH_LONG).show();
                buttonStartRecording.setEnabled(false);
            }
        }
    }

    private void startRecording() {
        if (!permissionToRecordAccepted) {
            requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }

        File audioDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (audioDir == null) {
            audioDir = requireContext().getFilesDir(); // Fallback
        }
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }

        File audioFile;
        try {
            audioFile = File.createTempFile("audio_record", ".3gp", audioDir);
            audioFilePath = audioFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(LOG_TAG, "createTempFile() failed: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to create audio file.", Toast.LENGTH_SHORT).show();
            return;
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            buttonStartRecording.setEnabled(false);
            buttonStopRecording.setEnabled(true);
            buttonPlayRecording.setEnabled(false);
            Toast.makeText(getContext(), "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed: " + e.getMessage());
            Toast.makeText(getContext(), "Recording failed to start.", Toast.LENGTH_SHORT).show();
            releaseMediaRecorder();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                if(isRecording) { // Only stop if it was actually recording
                    mediaRecorder.stop();
                }
            } catch (RuntimeException stopException) {
                Log.w(LOG_TAG, "RuntimeException during stopRecording: " + stopException.getMessage());
                if (audioFilePath != null) {
                    new File(audioFilePath).delete();
                    audioFilePath = null; // Avoid trying to play a corrupted file
                }
            } finally {
                releaseMediaRecorder();
            }
            isRecording = false; // Set after potential stop exception handling
            buttonStartRecording.setEnabled(true);
            buttonStopRecording.setEnabled(false);
            buttonPlayRecording.setEnabled(audioFilePath != null); // Enable play only if file exists
            if (audioFilePath != null) {
                 Toast.makeText(getContext(), "Recording stopped. File saved at " + audioFilePath, Toast.LENGTH_LONG).show();
            } else {
                 Toast.makeText(getContext(), "Recording stopped. File not saved due to error.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void playRecording() {
        if (audioFilePath == null) {
            Toast.makeText(getContext(), "No recording to play.", Toast.LENGTH_SHORT).show();
            return;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.setOnCompletionListener(mp -> {
                releaseMediaPlayer();
                buttonPlayRecording.setEnabled(true);
                buttonStartRecording.setEnabled(true);
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
            buttonPlayRecording.setEnabled(false);
            buttonStartRecording.setEnabled(false);
            Toast.makeText(getContext(), "Playing recording", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed for MediaPlayer: " + e.getMessage());
            Toast.makeText(getContext(), "Could not play recording.", Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        }
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.reset();
                mediaRecorder.release();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error releasing MediaRecorder: " + e.getMessage());
            } finally {
                mediaRecorder = null;
            }
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error releasing MediaPlayer: " + e.getMessage());
            } finally {
                mediaPlayer = null;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
        releaseMediaPlayer();
    }

    // onDestroyView is called when the fragment's view is destroyed.
    // This is a good place to clean up resources associated with the view.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseMediaRecorder(); // Ensure recorder is released
        releaseMediaPlayer(); // Ensure player is released
    }
}
