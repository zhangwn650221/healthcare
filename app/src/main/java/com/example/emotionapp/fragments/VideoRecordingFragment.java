package com.example.emotionapp.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.emotionapp.R;

public class VideoRecordingFragment extends Fragment {

    private static final int REQUEST_VIDEO_CAPTURE = 101;
    private static final int PERMISSION_REQUEST_CODE = 102;

    private Button buttonStartVideoCapture;
    private VideoView videoViewPlayback;
    private TextView textViewVideoPath;

    private Uri videoUri = null;

    private final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public VideoRecordingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_recording, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.video_activity_title));
        }

        buttonStartVideoCapture = view.findViewById(R.id.buttonStartVideoCapture);
        videoViewPlayback = view.findViewById(R.id.videoViewPlayback);
        textViewVideoPath = view.findViewById(R.id.textViewVideoPath);

        buttonStartVideoCapture.setOnClickListener(v -> {
            if (checkPermissions()) {
                dispatchTakeVideoIntent();
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        });
    }

    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED;
    }

    // Renamed from requestPermissions to avoid conflict with Fragment's own method
    private void requestCameraAndStoragePermissions() {
        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean cameraGranted = false;
            boolean storageGranted = false;
            for(int i=0; i<permissions.length; i++){
                if(permissions[i].equals(Manifest.permission.CAMERA) && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    cameraGranted = true;
                }
                if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    storageGranted = true;
                }
            }

            if (cameraGranted) {
                if (storageGranted) {
                    Toast.makeText(getContext(), getString(R.string.permissions_granted_message), Toast.LENGTH_SHORT).show();
                } else {
                    // This case might be an issue if the intent NEEDS to write to external public storage on older APIs
                    // For MediaStore or app-specific storage, this might be less critical.
                    Toast.makeText(getContext(), getString(R.string.storage_permission_denied_message) + " (Proceeding with Camera)", Toast.LENGTH_LONG).show();
                }
                dispatchTakeVideoIntent();
            } else {
                Toast.makeText(getContext(), getString(R.string.camera_permission_denied_message), Toast.LENGTH_LONG).show();
                buttonStartVideoCapture.setEnabled(false);
            }
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        } else {
            Toast.makeText(getContext(), getString(R.string.video_no_app_handler_message), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Important for Fragment lifecycle
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    videoUri = data.getData();
                    textViewVideoPath.setText(getString(R.string.video_saved_at_message, videoUri.toString()));
                    videoViewPlayback.setVideoURI(videoUri);
                    MediaController mediaController = new MediaController(requireContext());
                    videoViewPlayback.setMediaController(mediaController);
                    mediaController.setAnchorView(videoViewPlayback);
                    videoViewPlayback.setVisibility(View.VISIBLE);
                    videoViewPlayback.start();
                    Toast.makeText(getContext(), getString(R.string.video_recording_successful_message), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.video_failed_retrieve_message), Toast.LENGTH_SHORT).show();
                    textViewVideoPath.setText(getString(R.string.video_failed_retrieve_message));
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), getString(R.string.video_recording_cancelled_message), Toast.LENGTH_SHORT).show();
                textViewVideoPath.setText(getString(R.string.video_recording_cancelled_message));
            } else {
                Toast.makeText(getContext(), getString(R.string.video_recording_failed_message), Toast.LENGTH_SHORT).show();
                textViewVideoPath.setText(getString(R.string.video_recording_failed_message));
            }
        }
    }
}
